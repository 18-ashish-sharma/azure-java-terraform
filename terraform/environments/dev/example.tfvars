# Copy to dev.tfvars (git-ignored) and adjust:
#   terraform plan -var-file=dev.tfvars
app_name = "javademo"
location = "centralindia"

tags = {
  owner       = "platform-team"
  cost_center = "demo"
}
