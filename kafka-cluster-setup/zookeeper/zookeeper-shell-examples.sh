#!/bin/bash
# include zookeeper-shell examples
bin/zookeeper-shell.sh localhost:2181
# display help
help
# display root
ls /
create /my-node "foo"
ls /
get /my-node
get /zookeeper
create /my-node/deeper-node "bar"
ls /
ls /my-node
ls /my-node/deeper-node
get /my-node/deeper-node
# update data version to see increased version counter
set /my-node/deeper-node "newdata"
get /my-node/deeper-node
# removes are recursive
rmr /my-node
ls /
# create a watcher
create /node-to-watch ""
get /node-to-watch true
set /node-to-watch "has-changed"
rmr /node-to-watch
