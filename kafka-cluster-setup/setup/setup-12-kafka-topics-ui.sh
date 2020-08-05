#!/bin/bash

# copy the kafka-topics-ui-docker-compose.yml file
nano kafka-topics-ui-docker-compose.yml

# launch it
docker-compose -f  kafka-topics-ui-docker-compose.yml up -d
