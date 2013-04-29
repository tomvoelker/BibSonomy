#!/bin/sh

#
# small shellscript to release the root pom into the maven repository.
#
# versions:
# * 2013/04/29: cleanup (dzo)
# * 2011/04/29: initial version (dbe)
#

WEB_DIR_BASE=~/www/maven2/org/bibsonomy/bibsonomy
USER=bibsonomyupload
HOST=bugs.cs.uni-kassel.de
SERVER=${USER}@${HOST}
WORKSPACE_LOC=~/workspace/bibsonomy

# check command line args
if [ $# -ne 1 ]
then
	echo "USAGE: $0 <new_version_number>"
	exit 1
fi

# create dir
FOLDER=$WEB_DIR_BASE/$1
ssh $SERVER "mkdir $FOLDER"

# copy file
FILE=$FOLDER/bibsonomy-$1.pom
scp $WORKSPACE_LOC/pom.xml $SERVER:$FILE

# create checksums
ssh $SERVER "md5sum $FILE | cut -f1 -d ' ' > ${FILE}.md5"
ssh $SERVER "sha1sum $FILE | cut -f1 -d ' ' > ${FILE}.sha1"