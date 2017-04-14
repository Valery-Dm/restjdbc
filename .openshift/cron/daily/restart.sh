#!/bin/bash

PATH=/bin:/usr/bin:/usr/sbin
#app_url=http://$OPENSHIFT_APP_DNS/

#curl --insecure --location --silent --fail "$app_url" >/dev/null


cd $OPENSHIFT_REPO_DIR/.openshift/action_hooks

source stop;
sleep 3m;
source start;
sleep 3m;
curl -X GET --header 'Accept: application/json' --header 'api_key: apiKey' 'http://restjdbc-va1ery.rhcloud.com:80/rest/roles/ADM' >> ${OPENSHIFT_DIY_LOG_DIR}/cron_daily.log
