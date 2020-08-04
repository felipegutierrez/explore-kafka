# explore-kafka


Install [Insomnia](https://support.insomnia.rest/article/23-installation#linux) and import the [rest-proxy-insomnia.json](kafka-rest-proxy/src/main/resources/rest-proxy-insomnia.json).

```
# Add to sources
echo "deb https://dl.bintray.com/getinsomnia/Insomnia /" \
    | sudo tee -a /etc/apt/sources.list.d/insomnia.list

# Add public key used to verify code signature
wget --quiet -O - https://insomnia.rest/keys/debian-public.key.asc \
    | sudo apt-key add -

# Refresh repository sources and install Insomnia
sudo apt-get update
sudo apt-get install insomnia
```

![Load kafka-rest-proxy/src/main/resources/rest-proxy-insomnia.json inside Insomnia](figures/insomnia.png)





