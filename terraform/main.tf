terraform {
  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      version = "2.87.0"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "example" {
  name     = "diskyapi"
  location = "norwayeast"
}

resource "azurerm_app_service_plan" "example" {
  name                = "prod-diskyapi-asp"
  location            = azurerm_resource_group.example.location
  resource_group_name = azurerm_resource_group.example.name

  kind = "Linux"
  reserved = true


  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource "azurerm_app_service" "example" {
  name                = "prod-diskyapi-as"
  location            = azurerm_resource_group.example.location
  resource_group_name = azurerm_resource_group.example.name
  app_service_plan_id = azurerm_app_service_plan.example.id

    site_config {
      linux_fx_version = "TOMCAT|9.0-java11"
      always_on = true
      
    }
    app_settings = {
      "DISKY_DB_IP" = "81.166.183.145"
    }
}