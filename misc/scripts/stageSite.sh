#!/bin/sh

test ! -f README.txt -a -f pom.xml && echo "Change to the bibsonomy2 directory" && exit 1

DIR=$HOME/bibsonomy2-staged-site
test -d $DIR && rm -r $DIR
mvn clean site:stage -DstagingDirectory=$DIR
