#!/bin/bash

cd $OPENSHIFT_REPO_DIR/.openshift

source bash_init.sh

curl -u 'demo.admin@spring.demo:123456' -X GET --header 'Accept: application/json' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' >> ${OPENSHIFT_DIY_LOG_DIR}/cron_hourly.log
