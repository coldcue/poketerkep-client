#!/usr/bin/env bash

cd /home/ubuntu/poketerkep-client/PokemonGo-Map
pip install --upgrade six
pip install -r requirements.txt

chown -R ubuntu:ubuntu /home/ubuntu/poketerkep-client

#http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html
ln -s /home/ubuntu/poketerkep-client/poketerkep-client.jar /etc/init.d/poketerkep-client
update-rc.d poketerkep-client defaults
chmod 500 /home/ubuntu/poketerkep-client/poketerkep-client.jar
#chattr +i /home/ubuntu/poketerkep-client/poketerkep-client.jar
