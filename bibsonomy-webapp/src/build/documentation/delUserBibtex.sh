#!/bin/sh


HOST="www.bibsonomy.org"
#HOST="www.biblicious.org"
#HOST=goofy:8080


USER=$1                                      # user name is first argument
echo -n "password hash > " && read PHASH     # read in password hash
COOKIE="Cookie: _currUser=$USER%20$PHASH;"   # build cookie header


# get hashes
HASHES=$(wget -q -O -  "http://$HOST/bib/user/$USER?Items=10000" | grep biburl | sed "s/.*bibtex\/0//" | sed "s/\/.*//")

# delete items
for HASH in $HASHES; do
    echo deleting $HASH
    wget -q -O - --header "$COOKIE" "http://$HOST/BibtexHandler?hash=$HASH&requTask=delete" > /dev/null
done