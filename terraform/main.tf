# Deliberately insecure Terraform fixtures for IaC scanner validation. Never apply.
terraform { required_version = ">= 1.0" }

provider "aws" {
  region     = "us-east-1"
  access_key = "AKIAIOSFODNN7EXAMPLE"                     # CWE-798 credentials in provider block
  secret_key = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
}

provider "azurerm" {
  features {}
  subscription_id = "00000000-0000-0000-0000-000000000000"
  client_id       = "11111111-1111-1111-1111-111111111111"
  client_secret   = "fixture-azure-client-secret-not-real" # CWE-798
  tenant_id       = "22222222-2222-2222-2222-222222222222"
}

provider "google" {
  project     = "tigergate-fixture"
  region      = "us-central1"
  credentials = file("../config/gcp-service-account.json")
}
