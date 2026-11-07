resource "azurerm_service_plan" "this" {
  #checkov:skip=CKV_AZURE_211:SKU is chosen per environment (B1 in dev/stage, P0v3 in prod via app_service_sku).
  #checkov:skip=CKV_AZURE_212:Single instance keeps the sample cheap; set worker_count >= 2 for real prod traffic.
  #checkov:skip=CKV_AZURE_225:Zone redundancy needs Premium v3 and >= 2 instances; out of scope for this sample.
  name                = "asp-${local.prefix}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  os_type             = "Linux"
  sku_name            = var.app_service_sku
  tags                = local.tags
}

resource "azurerm_linux_web_app" "this" {
  #checkov:skip=CKV_AZURE_17:Client certificates are not used; the API is public behind HTTPS.
  #checkov:skip=CKV_AZURE_13:App Service Authentication is not used by this sample API.
  #checkov:skip=CKV_AZURE_88:App keeps no local files, so no Azure Files mount is needed.
  #checkov:skip=CKV_AZURE_222:This is a public web API; private access would need a private endpoint + front door.
  name                = "app-${local.prefix}-${local.suffix}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  service_plan_id     = azurerm_service_plan.this.id

  https_only                    = true
  public_network_access_enabled = true

  # System-assigned managed identity: used to resolve Key Vault references.
  identity {
    type = "SystemAssigned"
  }

  site_config {
    always_on                         = true
    ftps_state                        = "Disabled"
    http2_enabled                     = true
    minimum_tls_version               = "1.2"
    health_check_path                 = var.health_check_path
    health_check_eviction_time_in_min = 5

    application_stack {
      java_server         = "JAVA"
      java_server_version = var.java_version
      java_version        = var.java_version
    }
  }

  app_settings = {
    DB_HOST    = azurerm_postgresql_flexible_server.this.fqdn
    DB_NAME    = azurerm_postgresql_flexible_server_database.app.name
    DB_USER    = azurerm_postgresql_flexible_server.this.administrator_login
    DB_SSLMODE = "require"

    # Resolved by App Service at runtime using the managed identity; the secret
    # value never appears in app settings, Terraform outputs or the portal.
    DB_PASSWORD = "@Microsoft.KeyVault(SecretUri=${azurerm_key_vault_secret.db_password.versionless_id})"

    APPLICATIONINSIGHTS_CONNECTION_STRING = azurerm_application_insights.this.connection_string
  }

  logs {
    detailed_error_messages = true
    failed_request_tracing  = true

    http_logs {
      file_system {
        retention_in_days = 7
        retention_in_mb   = 35
      }
    }
  }

  tags = local.tags
}
