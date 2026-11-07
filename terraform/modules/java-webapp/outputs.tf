output "resource_group_name" {
  description = "Resource group holding every resource of this environment."
  value       = azurerm_resource_group.this.name
}

output "web_app_name" {
  description = "Name of the Linux web app (used by the deploy workflow)."
  value       = azurerm_linux_web_app.this.name
}

output "web_app_url" {
  description = "HTTPS URL of the web app."
  value       = "https://${azurerm_linux_web_app.this.default_hostname}"
}

output "web_app_principal_id" {
  description = "Object ID of the web app's system-assigned managed identity."
  value       = azurerm_linux_web_app.this.identity[0].principal_id
}

output "postgres_fqdn" {
  description = "FQDN of the PostgreSQL Flexible Server."
  value       = azurerm_postgresql_flexible_server.this.fqdn
}

output "key_vault_name" {
  description = "Key Vault holding the database password."
  value       = azurerm_key_vault.this.name
}

output "key_vault_uri" {
  description = "Key Vault URI."
  value       = azurerm_key_vault.this.vault_uri
}
