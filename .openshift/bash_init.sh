#!/bin/bash

source $OPENSHIFT_CARTRIDGE_SDK_BASH

set -x

export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
export PATH=$JAVA_HOME/bin:/bin:/usr/bin:/usr/sbin:$PATH

