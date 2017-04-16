#!/bin/bash

cd $OPENSHIFT_REPO_DIR/.openshift

source bash_init.sh

cd $OPENSHIFT_REPO_DIR/.openshift/action_hooks

source stop
sleep 3m
source start
sleep 3m

cd $OPENSHIFT_REPO_DIR

curl -X GET --header 'Accept: application/json' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' >> ${OPENSHIFT_DIY_LOG_DIR}/cron_daily.log
