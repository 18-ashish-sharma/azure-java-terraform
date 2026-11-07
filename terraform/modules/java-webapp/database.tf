resource "random_password" "postgres_admin" {
  length           = 32
  special          = true
  override_special = "_-!#"
  min_lower        = 2
  min_upper        = 2
  min_numeric      = 2
}

# Azure Database for PostgreSQL - Flexible Server (Single Server was retired in 2025).
resource "azurerm_postgresql_flexible_server" "this" {
  #checkov:skip=CKV_AZURE_136:Geo-redundant backup is a per-environment choice (on in prod via postgres_geo_redundant_backup).
  #checkov:skip=CKV2_AZURE_57:Private endpoint/VNet integration is the documented next step; public access is limited to Azure services by the firewall rule below.
  name                = "psql-${local.prefix}-${local.suffix}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location

  version    = var.postgres_version
  sku_name   = var.postgres_sku
  storage_mb = var.postgres_storage_mb

  administrator_login    = var.postgres_admin_login
  administrator_password = random_password.postgres_admin.result

  backup_retention_days         = var.postgres_backup_retention_days
  geo_redundant_backup_enabled  = var.postgres_geo_redundant_backup
  public_network_access_enabled = true

  tags = local.tags

  lifecycle {
    # Azure picks an availability zone when none is given; don't fight it on later plans.
    ignore_changes = [zone]
  }
}

resource "azurerm_postgresql_flexible_server_database" "app" {
  name      = var.database_name
  server_id = azurerm_postgresql_flexible_server.this.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

# 0.0.0.0 - 0.0.0.0 is Azure's special rule for "allow access from Azure services".
# It lets App Service reach the server without opening it to the internet.
# For production, replace with VNet integration + private access (see README).
resource "azurerm_postgresql_flexible_server_firewall_rule" "azure_services" {
  #checkov:skip=CKV2_AZURE_26:0.0.0.0 here means "Azure services only", not the internet; replace with private access for prod.
  name             = "allow-azure-services"
  server_id        = azurerm_postgresql_flexible_server.this.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

# TLS is enforced by default on Flexible Server (require_secure_transport = ON);
# pin it explicitly so nobody can quietly turn it off.
resource "azurerm_postgresql_flexible_server_configuration" "require_tls" {
  name      = "require_secure_transport"
  server_id = azurerm_postgresql_flexible_server.this.id
  value     = "on"
}
