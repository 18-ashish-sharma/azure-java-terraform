terraform {
  required_version = ">= 1.6"

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
    time = {
      source  = "hashicorp/time"
      version = "~> 0.12"
    }
  }

  # Partial configuration: the storage account details are passed at init time
  #   terraform init -backend-config=backend.hcl
  # See backend.hcl.example. State locking uses a blob lease, no extra setup.
  backend "azurerm" {}
}

provider "azurerm" {
  features {
    key_vault {
      # Keep soft-deleted vaults recoverable after a destroy.
      purge_soft_delete_on_destroy = false
    }
  }
  # Subscription comes from ARM_SUBSCRIPTION_ID (CI) or 'az account set' (local).
}
