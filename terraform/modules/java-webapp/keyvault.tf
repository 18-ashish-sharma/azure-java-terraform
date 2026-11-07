resource "azurerm_key_vault" "this" {
  #checkov:skip=CKV_AZURE_109:Firewall stays open so the CI runner (no fixed IP) can write the secret; access is still RBAC-only. Use a private endpoint + self-hosted runner to close it.
  #checkov:skip=CKV_AZURE_189:Same reason as CKV_AZURE_109 - public endpoint kept for the hosted CI runner, RBAC-only access.
  #checkov:skip=CKV2_AZURE_32:Private endpoint is the documented next step (see README security notes).
  name                = "kv-${local.prefix}-${local.suffix}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  tenant_id           = data.azurerm_client_config.current.tenant_id
  sku_name            = "standard"

  # Azure RBAC instead of access policies: permissions live in IAM like everything else.
  rbac_authorization_enabled = true
  # Purge protection is always on. A destroyed vault stays soft-deleted for 7 days;
  # the random suffix means a re-created environment gets a new name, so no clash.
  purge_protection_enabled   = true
  soft_delete_retention_days = 7

  network_acls {
    default_action = "Allow"
    bypass         = "AzureServices"
  }

  tags = local.tags
}

# Whoever runs Terraform (CI identity or you via az login) needs to write the secret.
resource "azurerm_role_assignment" "deployer_secrets_officer" {
  scope                = azurerm_key_vault.this.id
  role_definition_name = "Key Vault Secrets Officer"
  principal_id         = data.azurerm_client_config.current.object_id
}

# The web app's managed identity may only read secrets.
resource "azurerm_role_assignment" "webapp_secrets_user" {
  scope                = azurerm_key_vault.this.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_linux_web_app.this.identity[0].principal_id
}

# Role assignments are eventually consistent; writing the secret straight away can 403.
resource "time_sleep" "wait_for_rbac" {
  create_duration = "60s"
  depends_on      = [azurerm_role_assignment.deployer_secrets_officer]
}

resource "azurerm_key_vault_secret" "db_password" {
  #checkov:skip=CKV_AZURE_41:No expiry - the password is rotated by Terraform (apply -replace on random_password); an expiry date would break the app silently.
  name         = "db-password"
  value        = random_password.postgres_admin.result
  key_vault_id = azurerm_key_vault.this.id
  content_type = "text/plain"

  depends_on = [time_sleep.wait_for_rbac]
}
