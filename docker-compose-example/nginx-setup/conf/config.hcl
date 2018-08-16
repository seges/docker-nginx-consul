consul{
  address = "consul1:8500"
}

template {
    source = "/etc/consul-template/templates/template.ctmpl"
    destination = "/etc/nginx/conf.d/default.conf"
    command = "/etc/init.d/nginx reload"
    command_timeout = "60s"
}