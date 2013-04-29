#!/bin/sh

#
# small shellscript to release the root pom into the maven repository.
#
# versions:
# * 2011/04/29: initial version (dbe)
#

# check command line args
if [ $# -ne 1 ]
then
echo "USAGE: $0 <new_version_number>"
exit 1
fi

# create dir
ssh bibsonomyupload@bugs "mkdir ~/www/maven2/org/bibsonomy/bibsonomy/$1"
# copy file
scp pom.xml bibsonomyupload@bugs:~/www/maven2/org/bibsonomy/bibsonomy/$1/bibsonomy-$1.pom
# create checksums
ssh bibsonomyupload@bugs "md5sum ~/www/maven2/org/bibsonomy/bibsonomy/$1/bibsonomy-$1.pom | cut -f1 -d ' ' > ~/www/maven2/org/bibsonomy/bibsonomy/$1/bibsonomy-$1.pom.md5"
ssh bibsonomyupload@bugs "sha1sum ~/www/maven2/org/bibsonomy/bibsonomy/$1/bibsonomy-$1.pom | cut -f1 -d ' ' > ~/www/maven2/org/bibsonomy/bibsonomy/$1/bibsonomy-$1.pom.sha1"
