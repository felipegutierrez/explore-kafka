#!/usr/bin/env bash

# make sure that you execute "mvn clean package" first to create the jar file
# confluent local services start

# export the path
# export CLASSPATH="$(find target/kafka-source-connector-github-1.0/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
export CLASSPATH="$(find target/ -type f -name '*.jar' | tr '\n' ':')"
echo $CLASSPATH
if hash docker 2>/dev/null; then
    echo "1"
    # for docker lovers
    sudo docker build . -t explore-kafka/kafka-source-connector-github:1.0
    sudo docker run --net=host --rm -t \
           -v $(pwd)/offsets:/kafka-source-connector-github/offsets \
           explore-kafka/kafka-source-connector-github:1.0
elif hash connect-standalone 2>/dev/null; then
    echo "2"
    # for mac users who used homebrew
    connect-standalone config/worker.properties config/GitHubSourceConnectorExample.properties
elif [[ -z $KAFKA_HOME ]]; then
    echo "3"
    # for people who installed kafka vanilla
    $KAFKA_HOME/bin/connect-standalone.sh $KAFKA_HOME/etc/schema-registry/connect-avro-standalone.properties config/GitHubSourceConnectorExample.properties
elif [[ -z $CONFLUENT_HOME ]]; then
    echo "4"
    # for people who installed kafka confluent flavour
    $CONFLUENT_HOME/bin/connect-standalone $CONFLUENT_HOME/etc/schema-registry/connect-avro-standalone.properties config/GitHubSourceConnectorExample.properties
else
    printf "Couldn't find a suitable way to run kafka connect for you.\n \
Please install Docker, or download the kafka binaries and set the variable KAFKA_HOME."
fi;
