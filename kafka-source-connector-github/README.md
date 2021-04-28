# kafka-source-connector-github

This code is based on the repository [https://github.com/simplesteph/kafka-connect-github-source](https://github.com/simplesteph/kafka-connect-github-source).

Start the Confluent platform: `confluent local services start`
Package the project: `mvn clean package`

Add the 
```
plugin.path=share/java,/home/felipe/workspace-idea/explore-kafka/kafka-source-connector-github/target/kafka-source-connector-github-1.0/
```
on the file `confluent-6.1.1/etc/schema-registry/connect-avro-distributed.properties`

Create the GitHub source connector using the confluent command line tool: `connect-distributed config/worker.properties config/GitHubSourceConnectorExample.properties`






```
sudo docker run -it --rm -p 2181:2181 -p 3030:3030 -p 8081:8081 -p 8082:8082 -p 8083:8083 -p 9092:9092 \
    -e ADV_HOST=127.0.0.1 -e RUNTESTS=0 \
    -v /home/felipe/workspace-idea/explore-kafka/kafka-source-connector-github/target/kafka-source-connector-github-1.0:/connectors/GitHub \
    lensesio/fast-data-dev:2.3.0
```

Access the Landoop Kafka Development Environment at [http://127.0.0.1:3030/](http://127.0.0.1:3030/). Access the log service file [http://127.0.0.1:3030/logs/connect-distributed.log](http://127.0.0.1:3030/logs/connect-distributed.log) for errors. You should see one connector alive as it is shown on the figure below.


![Kafka Development Environment](figures/connector-alive.png)

![GitHubSourceConnector available](figures/github-source-connector.png)

Then configure the new github source connector.

![new github source connector configuration](figures/new-github-connector-kubernetes.png)

![new github source connector created](figures/new-github-connector-kubernetes-created.png)

The `github-issues` topic is available and consumming data.

![GitHub issues topic available](figures/github-issues-topic.png)

### Rest API:
Examples are taken from here: http://docs.confluent.io/3.2.0/connect/managing.html#common-rest-examples . Replace 127.0.0.1 by 192.168.99.100 if you're using docker toolbox
 - Get Worker information
`curl -s 127.0.0.1:8083/ | jq`
 - List Connectors available on a Worker
`curl -s 127.0.0.1:8083/connector-plugins | jq`
 - Ask about Active Connectors
`curl -s 127.0.0.1:8083/connectors | jq`
  Get information about a Connector Tasks and Config
`curl -s 127.0.0.1:8083/connectors/source-twitter-distributed/tasks | jq`
 - Get Connector Status
`curl -s 127.0.0.1:8083/connectors/file-stream-demo-distributed/status | jq`
 - Pause / Resume a Connector (no response if the call is succesful)
`curl -s -X PUT 127.0.0.1:8083/connectors/file-stream-demo-distributed/pause`
`curl -s -X PUT 127.0.0.1:8083/connectors/file-stream-demo-distributed/resume`
 - Get Connector Configuration
`curl -s 127.0.0.1:8083/connectors/file-stream-demo-distributed | jq`
 - Delete our Connector
`curl -s -X DELETE 127.0.0.1:8083/connectors/file-stream-demo-distributed`
 - Create a new Connector
`curl -s -X POST -H "Content-Type: application/json" --data '{"name": "file-stream-demo-distributed", "config":{"connector.class":"org.apache.kafka.connect.file.FileStreamSourceConnector","key.converter.schemas.enable":"true","file":"demo-file.txt","tasks.max":"1","value.converter.schemas.enable":"true","name":"file-stream-demo-distributed","topic":"demo-2-distributed","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}}' http://127.0.0.1:8083/connectors | jq`
`curl -s -X POST -H "Content-Type: application/json" --data '{"name": "GitHubSourceConnectorDemo", "config":{"connector.class":"com.github.felipegutierrez.kafka.connector.connectors.GitHubSourceConnector","key.converter.schemas.enable":"true","tasks.max":"1","value.converter.schemas.enable":"true","topic":"github-issues","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}}' http://127.0.0.1:8083/connectors | jq`
 - Update Connector configuration
`curl -s -X PUT -H "Content-Type: application/json" --data '{"connector.class":"org.apache.kafka.connect.file.FileStreamSourceConnector","key.converter.schemas.enable":"true","file":"demo-file.txt","tasks.max":"2","value.converter.schemas.enable":"true","name":"file-stream-demo-distributed","topic":"demo-2-distributed","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}' 127.0.0.1:8083/connectors/file-stream-demo-distributed/config | jq`


### Examples:
 - [https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/](https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/)
 - [https://github.com/riferrei/kafka-source-connector](https://github.com/riferrei/kafka-source-connector)
 - [https://github.com/jcustenborder/kafka-connect-archtype](https://github.com/jcustenborder/kafka-connect-archtype)
 - [https://www.confluent.io/blog/create-dynamic-kafka-connect-source-connectors/](https://www.confluent.io/blog/create-dynamic-kafka-connect-source-connectors/)
 - [https://medium.com/@gowdasunil15/building-custom-connector-for-kafka-connect-c163a7ed84c2](https://medium.com/@gowdasunil15/building-custom-connector-for-kafka-connect-c163a7ed84c2)

