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
  description = "App Service plan SKU for stage."
  default     = "B1"
}

variable "postgres_sku" {
  type        = string
  description = "PostgreSQL Flexible Server SKU for stage."
  default     = "B_Standard_B1ms"
}

variable "tags" {
  type        = map(string)
  description = "Extra tags for every resource."
  default     = {}
}
