# Example 

## How to build

Execute `./build.sh`


## How to up 

from `./docker-compose-example` directory  
execute `docker-compose up -d`

## Check container statuses

`docker-compose ps`

## Check nginx logs

`docker-compose logs -f nginx`

## Testing part 

Perform several times 

`curl localhost:8080/health` OR from browser [localhost:8080/health](localhost:8080/health)

Verify that load-balancer works properly and returns for each request one of three replicas

Stop one replica application replica `docker-compose stop backend2` 

Perform requests on [localhost:8080/health](localhost:8080/health)

Verify that load-balancer returns for each request one of two replicas 

Up replica back and repeat requests

`docker-compose up -d backend2`

## Notes

Consul UI is accessible on [localhost:8501](localhost:8501)
