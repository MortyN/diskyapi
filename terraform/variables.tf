variable "aws_access_key_id" {
    type = string
    description = "AWS env var id"
}

variable "aws_secret_access_key" {
    type = string
    description = "AWS secret key var"
}

variable "disky_mysql_adminuser" {
    type = string
    description = "mysql admin username"
}
variable "disky_mysql_adminpass" {
    type = string
    description = "mysql admin password"
}

variable "whitelisted_ips" {
  type = map(string)
  description = "whitelisted ips"
}

variable "mysql_ssl_ca" {
  type = string
  description = "ssl ca from mysql db"
}