variable "app_name" {
  type        = string
  description = "Short application name used as a resource prefix."
  default     = "javademo"
}

variable "location" {
  type        = string
  description = "Azure region."
  default     = "centralindia"
}

variable "app_service_sku" {
  type        = string
  description = "App Service plan SKU for prod."
  default     = "P0v3"
}

variable "postgres_sku" {
  type        = string
  description = "PostgreSQL Flexible Server SKU for prod."
  default     = "GP_Standard_D2ds_v5"
}

variable "tags" {
  type        = map(string)
  description = "Extra tags for every resource."
  default     = {}
}
