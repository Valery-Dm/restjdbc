#!/bin/bash

PATH=/bin:/usr/bin:/usr/sbin
#app_url=http://$OPENSHIFT_APP_DNS/

#curl --insecure --location --silent --fail "$app_url" >/dev/null


curl -u 'demo.admin@spring.demo:123456' -X GET --header 'Accept: application/json' --header 'api_key: apiKey' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' >> ${OPENSHIFT_DIY_LOG_DIR}/cron_hourly.log
