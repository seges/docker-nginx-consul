[![Build Status](https://travis-ci.org/seges/docker-nginx-consul.svg?branch=master)](https://travis-ci.org/seges/docker-nginx-consul)

TODO: update img tag
[![](https://badge.imagelayers.io/seges/nginx-consul:1.9.9.svg)](https://imagelayers.io/?images=seges/nginx-consul:1.9.9 'Get your own badge on imagelayers.io')

# Nginx with Consul Template

This Docker image follows official Nginx image extended with Consul Template to allow to refresh configuration based on the changes in Consul repository

# How-to

Create your data volume image and place required configuration files based on the following matrix:

* /etc/nginx/conf.d -> nginx configuration
* /etc/consul-template/conf -> Consul Template configurations taken and merged alphabetically - https://github.com/hashicorp/consul-template
* /etc/consul-template/templates -> a good place to situate your templates defined in the configuration

# Run

```
docker run --rm --volumes-from=yourdata-image -ti seges/nginx-consul:1.14.0
```

# Refs to blog

[Blog1](https://medium.com/@ladislavGazo/easy-routing-and-service-discovery-with-docker-consul-and-nginx-acfd48e1a291)