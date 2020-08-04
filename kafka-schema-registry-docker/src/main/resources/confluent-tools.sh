#!/bin/bash

# Two options to get access to the confluent tools

# Option 1:
# Download the confluent binaries at:
# https://www.confluent.io/download/
# Put them on your system and put the confluent/bin directory in your path
kafka-avro-console-consumer.sh

# Option 2:
# Use a docker image to have access to all the binaries right away:

sudo docker run -it --rm --net=host confluentinc/cp-schema-registry:3.3.1 bash
# Then you can do 
kafka-avro-console-consumer


# Produce a record with one field
# we can only create values with respect to the schema registry applied to the kafka-avro-console-producer
kafka-avro-console-producer \
    --broker-list 127.0.0.1:9092 --topic test-avro \
    --property schema.registry.url=http://127.0.0.1:8081 \
    --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'
# testing...
{"f1": "value1"}
{"f1": "value2"}
{"f1": "value3"}
# let's trigger an error:
{"f2": "value4"}
# let's trigger another error:
{"f1": 1}

# Consume the records from the beginning of the topic:
kafka-avro-console-consumer --topic test-avro \
    --bootstrap-server 127.0.0.1:9092 \
    --property schema.registry.url=http://127.0.0.1:8081 \
    --from-beginning


# Produce some errors with an incompatible schema (we changed to int) - should produce a 409
# use the 'docker run' in another terminal
sudo docker run -it --rm --net=host confluentinc/cp-schema-registry:3.3.1 bash
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic test-avro \
    --property schema.registry.url=http://127.0.0.1:8081 \
    --property value.schema='{"type":"int"}'

{"f1": "evolution", "f2": 1 }

# Some schema evolution (we add a field f2 as an int with a default)
kafka-avro-console-producer \
    --broker-list localhost:9092 --topic test-avro \
    --property schema.registry.url=http://127.0.0.1:8081 \
    --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"},{"name": "f2", "type": "int", "default": 0}]}'

{"f1": "evolution", "f2": 1 }


