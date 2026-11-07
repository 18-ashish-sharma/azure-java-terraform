locals {
  # e.g. "javademo-dev"
  prefix = "${var.app_name}-${var.environment}"

  # Web app, PostgreSQL and Key Vault names are global DNS names, so they get a
  # short random suffix that is stable for the life of the state.
  suffix = random_string.suffix.result

  tags = merge(
    {
      app         = var.app_name
      environment = var.environment
      managed_by  = "terraform"
    },
    var.tags,
  )
}
