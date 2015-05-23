FROM nginx:1.9.0
MAINTAINER Ladislav Gazo <gazo@seges.sk>

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update -qq && \
    apt-get -y install curl runit && \
    rm -rf /var/lib/apt/lists/*

ENV CT_URL https://github.com/hashicorp/consul-template/releases/download/v0.9.0/consul-template_0.9.0_linux_amd64.tar.gz
RUN curl -L $CT_URL | tar -C /usr/local/bin --strip-components 1 -zxf -

ADD nginx.service /etc/service/nginx/run
ADD consul-template.service /etc/service/consul-template/run

RUN mkdir /etc/consul-template && chmod +x /etc/service/nginx/run && chmod +x /etc/service/consul-template/run

CMD ["/usr/bin/runsvdir", "/etc/service"]
