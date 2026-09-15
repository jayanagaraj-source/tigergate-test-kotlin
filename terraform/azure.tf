resource "azurerm_resource_group" "fixture" {
  name     = "tigergate-fixture"
  location = "eastus"
}

resource "azurerm_storage_account" "fixture" {
  name                      = "tigergatefixture"
  resource_group_name       = azurerm_resource_group.fixture.name
  location                  = azurerm_resource_group.fixture.location
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  enable_https_traffic_only = false
  min_tls_version           = "TLS1_0"
  allow_blob_public_access  = true
  network_rules {
    default_action = "Allow"
  }
}

resource "azurerm_network_security_group" "fixture" {
  name                = "tigergate-fixture"
  location            = azurerm_resource_group.fixture.location
  resource_group_name = azurerm_resource_group.fixture.name
  security_rule {
    name                       = "allow-all-inbound"
    priority                   = 100
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "*"
    source_port_range          = "*"
    destination_port_range     = "*"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}

resource "azurerm_sql_server" "fixture" {
  name                         = "tigergate-fixture"
  resource_group_name          = azurerm_resource_group.fixture.name
  location                     = azurerm_resource_group.fixture.location
  version                      = "12.0"
  administrator_login          = "sqladmin"
  administrator_login_password = "Sq1-Adm1n-Passw0rd!"
}

resource "azurerm_sql_firewall_rule" "open" {
  name                = "allow-all"
  resource_group_name = azurerm_resource_group.fixture.name
  server_name         = azurerm_sql_server.fixture.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "255.255.255.255"
}

resource "azurerm_key_vault" "fixture" {
  name                        = "tigergate-fixture"
  location                    = azurerm_resource_group.fixture.location
  resource_group_name         = azurerm_resource_group.fixture.name
  tenant_id                   = "22222222-2222-2222-2222-222222222222"
  sku_name                    = "standard"
  purge_protection_enabled    = false
  soft_delete_enabled         = false
  network_acls {
    default_action = "Allow"
    bypass         = "AzureServices"
  }
}
