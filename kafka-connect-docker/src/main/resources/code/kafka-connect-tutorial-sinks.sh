#!/bin/bash

# Make sure you change the ADV_HOST variable in docker-compose.yml
# if you are using docker Toolbox

# 1) Source connectors
# Start our kafka cluster
docker-compose up kafka-cluster elasticsearch postgres
# Wait 2 minutes for the kafka cluster to be started

###############
# A) ElasticSearch Sink
# Info here: http://docs.confluent.io/3.2.0/connect/connect-elasticsearch/docs/elasticsearch_connector.html
# We make sure elasticsearch is working. Replace 127.0.0.1 by 192.168.99.100 if needed
http://127.0.0.1:9200/
# Go to the connect UI and apply the configuration at :
sink/demo-elastic/sink-elastic-twitter-distributed.properties
# Visualize the data at:
http://127.0.0.1:9200/_plugin/dejavu
# http://docs.confluent.io/3.1.1/connect/connect-elasticsearch/docs/configuration_options.html
# Counting the number of tweets:
http://127.0.0.1:9200/demo-3-twitter/_count
# You can download the data from the UI to see what it looks like
# We can query elasticsearch for users who have a lot of friends, see query-high-friends.json
###############

###############
# B) REST API Demo
# Examples are covered from here: http://docs.confluent.io/3.2.0/connect/managing.html#common-rest-examples
# See File sink/demo-rest-api/demo-rest-api.sh
###############

###############
# B) JDBC Sink demo
# Examples are covered from here: http://docs.confluent.io/3.2.0/connect/managing.html#common-rest-examples
# See File sink/demo-rest-api/demo-rest-api.sh
###############

###############
# C) PostgresSQL demo
# Examples are taken from here: http://docs.confluent.io/3.2.0/connect/connect-jdbc/docs/sink_connector.html#quickstart
