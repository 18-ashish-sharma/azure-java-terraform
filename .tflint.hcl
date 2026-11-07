config {
  call_module_type = "local"
}

plugin "terraform" {
  enabled = true
  preset  = "recommended"
}

plugin "azurerm" {
  enabled = true
  version = "0.31.1"
  source  = "github.com/terraform-linters/tflint-ruleset-azurerm"
}

# prevent_destroy cannot be driven by a variable, and dev/stage must be
# destroyable. Prod protection comes from the approval gate on the 'prod'
# GitHub environment and Key Vault purge protection instead.
rule "azurerm_resources_missing_prevent_destroy" {
  enabled = false
}

# The health_check_path + eviction already replaces unhealthy instances;
# auto-heal rules are tuned per workload once there is real traffic data.
rule "azurerm_app_service_missing_auto_heal_setting" {
  enabled = false
}
