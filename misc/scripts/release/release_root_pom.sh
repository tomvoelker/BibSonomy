#!/bin/sh

#
# small shellscript to release the root pom into the maven repository.
#
# versions:
# * 2013/04/29: cleanup (dzo)
# * 2011/04/29: initial version (dbe)
#

read -p "enter version nr.: " VERSION
WEB_DIR_BASE="~/www/maven2/org/bibsonomy/bibsonomy"
USER=bibsonomyupload
HOST=bugs.cs.uni-kassel.de
SERVER=${USER}@${HOST}
WORKSPACE_LOC=~/workspace/bibsonomy

# check version
if [ -z "$VERSION" ]
then
	echo "no version number"
	exit 1
fi

# create dir
FOLDER=$WEB_DIR_BASE/$VERSION
ssh $SERVER "mkdir $FOLDER"

# copy file
FILE=$FOLDER/bibsonomy-$VERSION.pom
scp $WORKSPACE_LOC/pom.xml $SERVER:$FILE

# create checksums
ssh $SERVER "md5sum $FILE | cut -f1 -d ' ' > ${FILE}.md5"
ssh $SERVER "sha1sum $FILE | cut -f1 -d ' ' > ${FILE}.sha1"
