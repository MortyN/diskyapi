terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      version = "2.88.1"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "rg" {
  name     = "diskyapi"
  location = "norwayeast"
}

resource "azurerm_app_service_plan" "example" {
  name                = "prod-diskyapi-asp"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name

  kind = "Linux"
  reserved = true


  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource "azurerm_app_service" "example" {
  name                = "prod-diskyapi-as"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  app_service_plan_id = azurerm_app_service_plan.example.id

    site_config {
      linux_fx_version = "TOMCAT|9.0-java11"
      always_on = true
      
    }
    app_settings = {
      "DISKY_DB_IP" = "81.166.183.145"
      "AWS_ACCESS_KEY_ID" = var.aws_access_key_id
      "AWS_SECRET_ACCESS_KEY" = var.aws_secret_access_key
    }
}

resource "azurerm_mysql_flexible_server" "mysqlserver" {
  name                   = "prod-diskyapi-mysql-flexible-server"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  administrator_login    = var.disky_mysql_adminuser
  administrator_password = var.disky_mysql_adminpass
  connection {
    
  }
  sku_name               = "B_Standard_B1s"
}

resource "azurerm_mysql_flexible_database" "example" {
  name                = "diskyapidb"
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azurerm_mysql_flexible_server.mysqlserver.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

resource "azurerm_mysql_flexible_server_firewall_rule" "example" {
  for_each = var.whitelisted_ips
  name                = each.key
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azurerm_mysql_flexible_server.mysqlserver.name
  start_ip_address    = each.value
  end_ip_address      = each.value
}
