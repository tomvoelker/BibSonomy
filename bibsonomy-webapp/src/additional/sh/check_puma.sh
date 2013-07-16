#!/bin/bash
PUMA_HOST=$1
PUMA_URL="http://$PUMA_HOST/postBookmark"
wget -t 5 --timeout 30 -O /dev/null -o /dev/null --no-cache "$PUMA_URL"
echo $?
