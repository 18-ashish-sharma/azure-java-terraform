module "app" {
  source = "../../modules/java-webapp"

  app_name    = var.app_name
  environment = "stage"
  location    = var.location
  tags        = var.tags

  app_service_sku = var.app_service_sku
  postgres_sku    = var.postgres_sku

  postgres_backup_retention_days = 7
  postgres_geo_redundant_backup  = false
}
