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

variable "morten_ip_address" {
    type = string
    description = "morten ip address"
}

variable "whitelisted_ips" {
  type = map(string)
  description = "whitelisted ips"
}