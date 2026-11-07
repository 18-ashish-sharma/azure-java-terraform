variable "app_name" {
  type        = string
  description = "Short application name used as a prefix for every resource (lowercase letters and digits, 3-10 chars)."
  default     = "javademo"

  validation {
    condition     = can(regex("^[a-z][a-z0-9]{2,9}$", var.app_name))
    error_message = "app_name must be 3-10 lowercase letters/digits and start with a letter (it ends up in globally unique names such as the Key Vault)."
  }
}

variable "environment" {
  type        = string
  description = "Deployment environment."

  validation {
    condition     = contains(["dev", "stage", "prod"], var.environment)
    error_message = "environment must be one of: dev, stage, prod."
  }
}

variable "location" {
  type        = string
  description = "Azure region for all resources."
  default     = "centralindia"
}

variable "tags" {
  type        = map(string)
  description = "Extra tags merged onto the default tags (app, environment, managed_by)."
  default     = {}
}

# --- App Service --------------------------------------------------------------

variable "app_service_sku" {
  type        = string
  description = "App Service plan SKU, e.g. B1 (dev), P0v3 (prod)."
  default     = "B1"
}

variable "java_version" {
  type        = string
  description = "Java SE runtime version for the Linux web app."
  default     = "17"
}

variable "health_check_path" {
  type        = string
  description = "Path App Service probes to decide whether an instance is healthy."
  default     = "/actuator/health"
}

# --- PostgreSQL Flexible Server -------------------------------------------------

variable "postgres_version" {
  type        = string
  description = "PostgreSQL major version."
  default     = "16"
}

variable "postgres_sku" {
  type        = string
  description = "Flexible Server SKU, e.g. B_Standard_B1ms (burstable, dev) or GP_Standard_D2ds_v5 (prod)."
  default     = "B_Standard_B1ms"
}

variable "postgres_storage_mb" {
  type        = number
  description = "Allocated storage in MB (minimum 32768)."
  default     = 32768
}

variable "postgres_backup_retention_days" {
  type        = number
  description = "Point-in-time restore window in days (7-35)."
  default     = 7

  validation {
    condition     = var.postgres_backup_retention_days >= 7 && var.postgres_backup_retention_days <= 35
    error_message = "postgres_backup_retention_days must be between 7 and 35."
  }
}

variable "postgres_geo_redundant_backup" {
  type        = bool
  description = "Store backups in the paired region as well. Cannot be changed after the server is created."
  default     = false
}

variable "postgres_admin_login" {
  type        = string
  description = "Administrator login for the PostgreSQL server. The password is generated and kept in Key Vault."
  default     = "pgadmin"
}

variable "database_name" {
  type        = string
  description = "Name of the application database."
  default     = "appdb"
}

# --- Monitoring -----------------------------------------------------------------

variable "log_retention_days" {
  type        = number
  description = "Retention for the Log Analytics workspace behind Application Insights."
  default     = 30
}
