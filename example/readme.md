## Application example

RESTapi service.

### Tech stack

* [Akka (actor, http, stream)](https://akka.io/)
* [Consul-client java](https://github.com/rickfast/consul-client)
* [Shade plugin](https://maven.apache.org/plugins/maven-shade-plugin/) for fat-jar
* Java 8
* Docker

### REST

| REQUEST             | RESPONSE            | 
| -------------       |:-------------:      | 
| GET /health         | 200 OK and app-id   | 
| POST /app           | 200 OK              | 


### Notes

Pay attention to config file `application.conf`, it is loaded by [typesafe config](https://github.com/lightbend/config).

`DiscoveryAgentActor` responsible for registration this app instance in consul and send each (10 sec default) health check to consul.


