#!/bin/bash
# https://zookeeper.apache.org/doc/r3.4.8/zookeeperAdmin.html#sc_zkCommands
# conf
# New in 3.3.0: Print details about serving configuration.
echo "conf" | nc localhost 2181

# cons
# New in 3.3.0: List full connection/session details for all clients connected to this server. Includes information on numbers of packets received/sent, session id, operation latencies, last operation performed, etc...
echo "cons" | nc localhost 2181

# crst
# New in 3.3.0: Reset connection/session statistics for all connections.
# echo "crst" | nc localhost 2181

# dump
# Lists the outstanding sessions and ephemeral nodes. This only works on the leader.
echo "dump" | nc localhost 2181

# envi
# Print details about serving environment
echo "envi" | nc localhost 2181

# ruok
# Tests if server is running in a non-error state. The server will respond with imok if it is running. Otherwise it will not respond at all.
echo "ruok" | nc localhost 2181
# A response of "imok" does not necessarily indicate that the server has joined the quorum, just that the server process is active and bound to the specified client port. Use "stat" for details on state wrt quorum and client connection information.

# srst
# Reset server statistics.
# echo "srst" | nc localhost 2181

# srvr
# New in 3.3.0: Lists full details for the server.
echo "srvr" | nc localhost 2181

# stat
# Lists brief details for the server and connected clients.
echo "stat" | nc localhost 2181

# wchs
# New in 3.3.0: Lists brief information on watches for the server.
echo "wchs" | nc localhost 2181

#wchc
#New in 3.3.0: Lists detailed information on watches for the server, by session. This outputs a list of sessions(connections) with associated watches (paths). Note, depending on the number of watches this operation may be expensive (ie impact server performance), use it carefully.
echo "wchc" | nc localhost 2181

# wchp
# New in 3.3.0: Lists detailed information on watches for the server, by path. This outputs a list of paths (znodes) with associated sessions. Note, depending on the number of watches this operation may be expensive (ie impact server performance), use it carefully.
echo "wchp" | nc localhost 2181

# mntr
# New in 3.4.0: Outputs a list of variables that could be used for monitoring the health of the cluster.
echo "mntr" | nc localhost 2181
