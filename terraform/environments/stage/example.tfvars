# Copy to stage.tfvars (git-ignored) and adjust:
#   terraform plan -var-file=stage.tfvars
app_name = "javademo"
location = "centralindia"

tags = {
  owner       = "platform-team"
  cost_center = "demo"
}
