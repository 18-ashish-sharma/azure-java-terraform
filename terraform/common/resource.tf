resource "azurerm_resource_group" "odw-backend-rg" {
  name     = local.resource_group
  location = local.location

  tags = {
    "Terraform" = "true"
  }
}

resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

# This creates a PostgresSQL server

resource "azurerm_postgresql_server" "odw-np-db-server" {
  name                = "${azurerm_resource_group.odw-backend-rg.name}db-server"
  location            = azurerm_resource_group.odw-backend-rg.location
  resource_group_name = azurerm_resource_group.odw-backend-rg.name

  administrator_login          = "oneDoorWay"
  administrator_login_password = random_password.password.result

  sku_name = "B_Gen5_1"
  version  = "11"

  storage_mb        = 5120
  auto_grow_enabled = true

  backup_retention_days            = 7
  geo_redundant_backup_enabled     = false
  public_network_access_enabled    = true
  ssl_enforcement_enabled          = true
  ssl_minimal_tls_version_enforced = "TLS1_2"
}


#  This creates a PostgresSQL DB 

resource "azurerm_postgresql_database" "odw-np-database" {
  name                = "${azurerm_resource_group.odw-backend-rg.name}-db"
  resource_group_name = azurerm_resource_group.odw-backend-rg.name
  server_name         = azurerm_postgresql_server.odw-np-db-server.name
  charset             = "utf8"
  collation           = "English_United States.1252"
}


# This rule is to enable the 'Allow access to Azure services' checkbox

resource "azurerm_postgresql_firewall_rule" "odw-np-db-rule" {
  name                = "${azurerm_resource_group.odw-backend-rg.name}db-firewall"
  resource_group_name = azurerm_resource_group.odw-backend-rg.name
  server_name         = azurerm_postgresql_server.odw-np-db-server.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}


# This creates the plan that the service use

resource "azurerm_app_service_plan" "odw-np-app-plan" {
  name                = "${local.application_name}-plan"
  location            = azurerm_resource_group.odw-backend-rg.location
  resource_group_name = azurerm_resource_group.odw-backend-rg.name
  kind                = "Linux"
  reserved            = true

  sku {
    tier = "Basic"
    size = "B2"
  }
}


# This creates the service definition

resource "azurerm_app_service" "odw-app-service" {
  name                = local.application_name
  location            = azurerm_resource_group.odw-backend-rg.location
  resource_group_name = azurerm_resource_group.odw-backend-rg.name
  app_service_plan_id = azurerm_app_service_plan.odw-np-app-plan.id
  https_only          = true

  site_config {
    always_on        = true
    linux_fx_version = "JAVA|8-jre8"
  }

  app_settings = {
    "WEBSITES_ENABLE_APP_SERVICE_STORAGE" = "false"

    # These are app specific environment variables
    "SPRING_PROFILES_ACTIVE"     = "postgressql"
    "SPRING_DATASOURCE_URL"      = "jdbc:postgressql://${azurerm_postgresql_server.odw-np-db-server.fqdn}:3306/${azurerm_postgresql_database.odw-np-database.name}?useUnicode=true&characterEncoding=utf8&useSSL=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
    "SPRING_DATASOURCE_USERNAME" = "${azurerm_postgresql_server.odw-np-db-server.administrator_login}@${azurerm_postgresql_server.odw-np-db-server.name}"
    "SPRING_DATASOURCE_PASSWORD" = azurerm_postgresql_server.odw-np-db-server.administrator_login_password
  }
}
