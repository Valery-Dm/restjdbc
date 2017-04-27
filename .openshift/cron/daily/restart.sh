echo $$
PATH=/bin:/usr/bin:/usr/sbin
cd $OPENSHIFT_REPO_DIR/.openshift/action_hooks

echo stopping
source stop &> /dev/null 
sleep 1m
echo starting
source start &> /dev/null

