output "resource_group_name" {
  description = "Resource group name."
  value       = module.app.resource_group_name
}

output "web_app_name" {
  description = "Web app name (input to the deploy step)."
  value       = module.app.web_app_name
}

output "web_app_url" {
  description = "Public HTTPS URL."
  value       = module.app.web_app_url
}

output "postgres_fqdn" {
  description = "PostgreSQL server FQDN."
  value       = module.app.postgres_fqdn
}

output "key_vault_name" {
  description = "Key Vault holding the DB password."
  value       = module.app.key_vault_name
}
