#!/usr/bin/env bash

CONTAINER_NAME=postgres_parking
DB_PORT=7677
DB_NAME=parking
DB_USER=beby
DB_PASS=chobeeh

# actual script #
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$CONTAINER_NAME -f status=exited)" ]; then
		echo "Stopping postgres container"
		docker stop $CONTAINER_NAME
	fi
	echo "Starting postgres container"
	docker start $CONTAINER_NAME
else
	echo "Creating & starting postgres container"
	docker run -d \
		--name $CONTAINER_NAME \
		-p $DB_PORT:5432 \
		-e POSTGRES_DB=$DB_NAME \
		-e POSTGRES_USER=$DB_USER \
		-e POSTGRES_PASSWORD=$DB_PASS \
		postgres:10.4
fi