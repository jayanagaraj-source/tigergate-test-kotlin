terraform {
  required_providers {
    # Deliberately old provider versions for SCA / SBOM scanner testing.
    aws     = { source = "hashicorp/aws", version = "3.0.0" }
    azurerm = { source = "hashicorp/azurerm", version = "2.40.0" }
    google  = { source = "hashicorp/google", version = "3.0.0" }
  }
}
