#!/bin/bash
echo $$
PATH=/bin:/usr/bin:/usr/sbin

nohup curl -u 'demo.admin@spring.demo:123456' -X GET --header 'Accept: application/json' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' > /dev/null &>> ${OPENSHIFT_DIY_LOG_DIR}/cron_hourly.log &

