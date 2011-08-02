#!/bin/sh

BASE_DIR=/home/bibsonomy
# these directories will be backuped
DIRS="bibsonomy_docs bibsonomy_pics"
ROPTIONS="--archive --quiet --rsh=ssh --no-owner --no-perms --no-group --omit-dir-times" 

#### daffy
RHOST=daffy.cs.uni-kassel.de
RUSER=bibbackup
RDIR=backup

# backup directories
for DIR in $DIRS; do
#  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

#### joe (Slave)
RHOST=joe.cs.uni-kassel.de
RUSER=bibsonomy
RDIR=/home/bibsonomy

# backup directories
for DIR in $DIRS; do
#  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

#### joe (Slave)

#### melkor (Würzburg)
#melkor.informatik.uni-wuerzburg.de
RHOST=132.187.15.90
RUSER=bibbackup
RDIR=backup

# backup directories
for DIR in $DIRS; do
#  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

