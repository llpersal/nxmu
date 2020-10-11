#!/bin/bash
IMAGE=bigdata-view

docker stop app

sleep 3s

docker run -d --rm -p 9090:8080 --volume="/opt/joweywen/data:/data" --name app ${IMAGE}