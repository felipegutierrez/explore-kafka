
## Pre-requisites
Update and install packages
```
sudo apt-get update -y
sudo apt-get -y install wget ca-certificates zip net-tools vim nano tar netcat
```
Use Java Open JDK 8
```
sudo apt-get -y install openjdk-8-jdk
java -version
```
Disable RAM Swap - can set to 0 on certain Linux distro
```
sudo sysctl vm.swappiness=1
echo 'vm.swappiness=1' | sudo tee --append /etc/sysctl.conf
cat /etc/sysctl.conf
```
Add hosts entries (mocking DNS) - put relevant IPs here
```
127.0.0.1	localhost cow-11 kafka00 zookeeper00
192.168.56.1    cow-11 kafka00 zookeeper00
# Virtual Machines
192.168.56.101  worker01 kafka01 zookeeper01
192.168.56.102  worker02 kafka02 zookeeper02
192.168.56.103  worker03 kafka03 zookeeper03
```
Follow instructions at ...




