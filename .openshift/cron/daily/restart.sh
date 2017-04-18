#!/bin/bash

PATH=/bin:/usr/bin:/usr/sbin
cd $OPENSHIFT_REPO_DIR/.openshift/action_hooks

echo stopping...
source stop 2>&1
sleep 1m
echo starting...
source start 2>&1
sleep 1m
echo testing...
curl -u 'demo.admin@spring.demo:123456' -X GET --header 'Accept: application/json' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' >> ${OPENSHIFT_DIY_LOG_DIR}/cron_daily.log &

