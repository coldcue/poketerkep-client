#!/usr/bin/env bash

if [ -f /etc/init.d/poketerkep-client ];
then
    /etc/init.d/poketerkep-client stop
fi