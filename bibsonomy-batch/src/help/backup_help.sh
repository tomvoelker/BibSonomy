#!/bin/sh                                                                       
# Backup the help files (the wiki content)
# TODO on installation
# * configure the BASEDIR (point towards the content folders of the help)
# * configure the target (RHOST, RUSER, RDIR)                                   # this cronjob should run on the same system, that runs the help                                                                                            

BASEDIR=/home/bibsonomyhelp/bibsonomy-help_

RHOST=venus.cs.uni-kassel.de
RUSER=bibbackup
RDIR=/home/kde/bibbackup/backup/

ROPTIONS="--archive --quiet --rsh=ssh --delete"

# add languages here                                                           
for lang in en de; do
  rsync $ROPTIONS $BASEDIR$lang $RUSER@$RHOST:$RDIR
done
