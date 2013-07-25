#!/bin/bash 
PUMA_HOST=$1
FILE_NAME="/var/tmp/$PUMA_HOST"
#PUMA_URL="http://$PUMA_HOST/postBookmark"
PUMA_URL="http://$PUMA_HOST/swrc/"
#TIMES=$3
#SLEEP=`expr 60 / $TIMES`
SLEEP=30
for I in `seq 1 2`; do
	LINES=`wget -t 5 --timeout 15 -O /dev/stdout -o /dev/null --no-cache "$PUMA_URL" | grep 'Ontology' | wc -l`
	if [ $LINES -gt 0 ]; then
		touch "$FILE_NAME"
	else
		if [ -e "$FILE_NAME" ]; then
			rm "$FILE_NAME"
		fi
	fi
	if [ $I -lt 2 ]; then
		sleep $SLEEP
	fi
done
