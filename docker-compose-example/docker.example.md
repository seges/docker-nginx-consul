# Docker-compose example

* Consul cluster: 3 nodes
* Example app: 3 replicas, each replica connect to different consul node in cluster
* Nginx + consul-template node. It is used as gateway & loadbalancer 

## Notes

A. Each example instance needs consul address, which provided as a ENV variable.

```
  backend2:
    build: ../example
    image: zhenik/akka-http-example
    environment:
    - DISCOVERY_HOST=consul1
    - APPLICATION_HOST=backend2
```

B. Nginx + consul-template requires 2 volumes.  

```
  nginx:
    build: ../
    image: seges/nginx-consul
    ports:
    - "80:80"
    - "8080:8080"
    - "443:443"
    volumes:
    #      templates
    - "./nginx-setup/templates/template.ctmpl:/etc/consul-template/templates/template.ctmpl"
    #      config
    - "./nginx-setup/conf:/etc/consul-template/conf"
    depends_on:
      - consul1
```

- Volume with templates. Templates contains instruction how to render target files.

```
upstream app-example {
  least_conn;
    {{range service "example-app"}}server {{.Address}}:{{.Port}} max_fails=3 fail_timeout=60 weight=1;
  {{else}}server 127.0.0.1:65535; # force a 502{{end}}
}
server {
  listen 8080;
  server_name localhost;

  location / {
    proxy_pass http://app-example;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

- Volume with config for `consul-template`

All config can be found in [consul-template documentation](https://github.com/hashicorp/consul-template#configuration-file-format)

```
consul{
  address = "consul1:8500" // which consul connect to 
}

template {
    source = "/etc/consul-template/templates/template.ctmpl"  // source file for tmpl
    destination = "/etc/nginx/conf.d/default.conf"            // target file with rendered data
    command = "/etc/init.d/nginx reload"                     
    command_timeout = "60s"
}
```

## If you want extend solution

### Problems

``Problem A``: What if `consul1` is down, but cluster is resilient and `consul2` `consul3` are accessible.  

``Problem B``: Volumes with templates, point `explicitly` to specific file. How to add more templates.

``Problem C``: Gateway is bottleneck. It is only one node. 

### Possible solutions

``Problem A1``: Bootstrap consul cluster.

``Problem A2``: Setup consul-agent on `seges/nginx-consul` image. Solution from [documentation](https://github.com/hashicorp/consul-template#configuration-file-format)  

`address = "127.0.0.1:8500"`
> This is the address of the Consul agent. By default, this is
    127.0.0.1:8500, which is the default bind and port for a local Consul
    agent. It is not recommended that you communicate directly with a Consul
    server, and instead communicate with the local Consul agent. There are many
    reasons for this, most importantly the Consul agent is able to multiplex
    connections to the Consul server and reduce the number of open HTTP
    connections. Additionally, it provides a "well-known" IP address for which
    clients can connect.  


``Problem B1``: Use directories for `consul-template` config and [render multiple templates](https://github.com/hashicorp/consul-template#command-line-flags). 

``Problem C1 draft``: Create cluster of gateways. Redirect if not accessible.