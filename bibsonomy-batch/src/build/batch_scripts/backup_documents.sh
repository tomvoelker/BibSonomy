#!/bin/sh

BASE_DIR=/home/bibsonomy
# these directories will be backuped
DIRS="bibsonomy_docs bibsonomy_pics"

#### daffy
RHOST=daffy.cs.uni-kassel.de
RUSER=bibbackup
RDIR=backup
ROPTIONS="-av --rsh=ssh"

# backup directories
for DIR in $DIRS; do
  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

#### joe (Slave)
RHOST=joe.cs.uni-kassel.de
RUSER=bibsonomy
RDIR=/home/bibsonomy
ROPTIONS="-av --rsh=ssh"

# backup directories
for DIR in $DIRS; do
  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

#### joe (Slave)

#### melkor (Würzburg)
RHOST=melkor.informatik.uni-wuerzburg.de
RUSER=bibbackup
RDIR=backup
ROPTIONS="-av --rsh=ssh"

# backup directories
for DIR in $DIRS; do
  echo "rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR"
        rsync $ROPTIONS $BASE_DIR/$DIR $RUSER@$RHOST:$RDIR
done

