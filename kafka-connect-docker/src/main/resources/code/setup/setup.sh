#!/bin/bash

# 0. Make sure Java is installed on your machine https://java.com/en/download/
# 1. Download Kafka at https://kafka.apache.org/downloads (>= 0.11.0.1)
# 2. Unzip Kafka to a directory of your choice (for example ~/kafka)

# 3. Open a terminal or text editor on the Kafka directory
# 4. Edit the file at config/connect-distributed.config
nano config/connect-distributed.properties
vi config/connect-distributed.properties
atom config/connect-distributed.properties

# 5. change bootstrap.servers to the correct ones
# 6. change rest.port=8083 to rest.port=8084 (or any available port)
# 7. Optional (if you want a separate cluster) -
  # 7a. Set offset.storage.topic=connect-offsets-2
  # 7b. Set config.storage.topic=connect-configs-2
  # 7c. Set status.storage.topic=connect-status-2
  # 7d. Set group.id=connect-cluster-2
# 8. Set plugin.path=/directory/of/your/choice
# 9. Adjust any other settings that matters to your setup (see doc)
# 10. Place any connector (jars) you need in the plugin.path sub-directory

# 11. In order to add connect workers, duplicate the connect-distributed file and change the rest.port if running on the same host. The rest of the settings are the same

# 12. Optionally, setup Kafka Connect UI from Landoop
  # Instructions are at: https://github.com/Landoop/kafka-connect-ui
  # You can build from source or use docker for that (see their github)
