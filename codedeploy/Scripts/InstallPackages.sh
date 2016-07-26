#!/usr/bin/env bash

set +e
echo "# APT update"
sudo apt-get update -y 1>/dev/null
set -e

echo "#### Install prerequisites"
sudo apt-get install unzip software-properties-common python-software-properties python python-pip ntp cloud-utils -y

###############################################################
echo "#### Install Java 8"

#Agree license
echo "# Add oracle license"
echo debconf shared/accepted-oracle-license-v1-1 select true | \
sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | \
sudo debconf-set-selections

#Java repository
echo "# Add webupd8team/java repository"
sudo add-apt-repository -y ppa:webupd8team/java

set +e
echo "# APT update"
sudo apt-get update -y 1>/dev/null
set -e

sudo apt-get install oracle-java8-installer -y
echo "#### Java 8 installed"


###############################################################