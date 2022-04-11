locals {
  resource_group = "odw-backend-${var.env}"
  application_name = "odw-${var.env}-app"
  location = "centralindia"
  
  tags = {
      env         = var.env,
      Application = "odw-backend-service"
  }
}