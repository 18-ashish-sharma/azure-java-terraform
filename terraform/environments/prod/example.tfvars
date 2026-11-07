# Copy to prod.tfvars (git-ignored) and adjust:
#   terraform plan -var-file=prod.tfvars
app_name = "javademo"
location = "centralindia"

tags = {
  owner       = "platform-team"
  cost_center = "demo"
}
