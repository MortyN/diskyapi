terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "2.88.1"
    }
    aws = {
      source = "hashicorp/aws"
      version = "3.68.0"
    }
  }
}

provider "azurerm" {
  features {}
}

provider "aws" {
  region = "eu-north-1"
}

locals {
  app_name = "diskyapi"
  env = "prod"
}

resource "azurerm_resource_group" "rg" {
  name     = local.app_name
  location = "norwayeast"
}

resource "azurerm_app_service_plan" "example" {
  name                = "${local.env}-${local.app_name}-asp"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name

  kind     = "Linux"
  reserved = true


  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource "azurerm_app_service" "example" {
  name                = "${local.env}-${local.app_name}-as"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  app_service_plan_id = azurerm_app_service_plan.example.id

  site_config {
    linux_fx_version = "TOMCAT|9.0-java11"
    always_on        = true

  }
  app_settings = {
    "AWS_ACCESS_KEY_ID"     = var.aws_access_key_id
    "AWS_SECRET_ACCESS_KEY" = var.aws_secret_access_key
    "AWS_SECRET_ACCESS_KEY" = var.aws_secret_access_key
    "DISKYAPIADMINUSER"     = var.disky_mysql_adminuser
    "DISKYAPIADMINPASS"     = var.disky_mysql_adminpass
    "DISKYAPIDBNAME"        = azurerm_mysql_flexible_database.example.name
    "DISKYAPIDBSERVERNAME"  = azurerm_mysql_flexible_server.mysqlserver.name
    "DISKYS3NAME"           = "${local.env}-${local.app_name}-s3"
    "MYSQL_SSL_CA"          = var.mysql_ssl_ca
  }
}

resource "azurerm_mysql_flexible_server" "mysqlserver" {
  name                   = "${local.env}-${local.app_name}-mysql-flexible-server"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  administrator_login    = var.disky_mysql_adminuser
  administrator_password = var.disky_mysql_adminpass
  sku_name               = "B_Standard_B1s"
}

resource "azurerm_mysql_flexible_database" "example" {
  name                = "${local.app_name}db"
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azurerm_mysql_flexible_server.mysqlserver.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"
}

resource "azurerm_mysql_flexible_server_firewall_rule" "example" {
  for_each            = var.whitelisted_ips
  name                = each.key
  resource_group_name = azurerm_resource_group.rg.name
  server_name         = azurerm_mysql_flexible_server.mysqlserver.name
  start_ip_address    = each.value
  end_ip_address      = each.value
}

resource "aws_s3_bucket" "b" {
  bucket = "${local.env}-${local.app_name}-s3"
  acl    = "public-read-write"
  
  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["POST", "PUT", "DELETE"]
    allowed_origins = ["https://${azurerm_app_service.example.name}.azurewebsites.net"]
  }
}