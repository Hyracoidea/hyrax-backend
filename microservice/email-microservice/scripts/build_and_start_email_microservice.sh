#!/usr/bin/env bash

sh build_email_microservice.sh

if [ $? -ne 0 ]; then
    exit 1
fi

sh start_email_microservice.sh

if [ $? -ne 0 ]; then
    exit 1
else
    exit 0
fi