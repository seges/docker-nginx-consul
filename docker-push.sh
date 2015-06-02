#!/bin/bash

if [ $# -ne 1 ]; then
  echo "Provide version"
  exit 42
fi

version=":$1"

docker push seges/nginx-consul$version
