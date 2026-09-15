# Fake static credentials used only to exercise secret-scanning rules.
locals {
  fixture_access_key = "AKIAIOSFODNN7EXAMPLE"
  fixture_secret_key = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
  db_master_password = "Pr0d-DB-Passw0rd!"
}

variable "api_token" {
  description = "Default value is a secret (CKV_SECRET / tfsec AWS-general)."
  type        = string
  default     = "ghp_FIXTUREtoken0000000000000000000000000"
}
