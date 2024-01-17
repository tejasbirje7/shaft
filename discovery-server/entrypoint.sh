#!/bin/sh
export ECS_INSTANCE_IP_ADDRESS=$(curl — retry 5 — connect-timeout 3 -s 13.232.118.60/latest/meta-data/local-ipv4)
