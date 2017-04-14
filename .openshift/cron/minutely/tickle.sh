#!/bin/bash

source $OPENSHIFT_CARTRIDGE_SDK_BASH

set -x

export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
export PATH=$JAVA_HOME/bin:$PATH

date >> ${OPENSHIFT_DIY_LOG_DIR}/cron_minutely.log 
