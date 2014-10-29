#!/bin/bash

# REGISTRATION
# * if you want the system to archive its webapp register it as archivable
# * if you want to disable mails and security questions before deployment register it in the array $unnoticed

# environment variables
source deploy-functions.sh

# main
checkParams "$@"

targetServer=$1

document $targetServer
WEBAPP_PATH=$BIBSONOMY_PATH/$DEFAULT_WEBAPP
build $WEBAPP_PATH
deploy $WEBAPP_PATH $targetServer $2
sendMail $targetServer