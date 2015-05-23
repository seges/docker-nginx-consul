# Nginx with Consul Template

This Docker image follows official Nginx image extended with Consul Template to allow to refresh configuration based on the changes in Consul repository

# How-to

Create your data volume image and place required configuration files based on the following matrix:

* /etc/nginx/conf.d -> nginx configuration
* /etc/consul-template -> Consul Template configurations taken and merged alphabetically - https://github.com/hashicorp/consul-template

# Run

```
docker run --rm --volumes-from=yourdata-image -ti seges/nginx-consul:1.9.0
```
