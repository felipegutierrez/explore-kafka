#!/bin/bash

# put this in any directory you like
wget -nc https://repo1.maven.org/maven2/org/apache/avro/avro-tools/1.10.0/avro-tools-1.10.0.jar -P /tmp/avro-output/

# run this from our project folder. Make sure ~/avro-tools-1.8.2.jar is your actual avro tools location
echo
echo "============================================="
echo "Using 'tojson' from 'avro-tools' on the customer-generic.avro"
java -jar /tmp/avro-output/avro-tools-1.10.0.jar tojson --pretty /tmp/avro-output/customer-generic.avro

echo
echo "============================================="
echo "Using 'tojson' from 'avro-tools' on the customer-specific.avro"
java -jar /tmp/avro-output/avro-tools-1.10.0.jar tojson --pretty /tmp/avro-output/customer-specific.avro

# getting the schema
echo
echo "============================================="
echo "Using 'getschema' from 'avro-tools' on the customer-specific.avro"
java -jar /tmp/avro-output/avro-tools-1.10.0.jar getschema /tmp/avro-output/customer-specific.avro
echo
