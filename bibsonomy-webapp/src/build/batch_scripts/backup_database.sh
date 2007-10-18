#!/bin/sh

# get current day of year
CURRENT_DAY=`date +%j`
# location of directories
HOME_DIR=/home/bibsonomy
BACK_DIR_NAME=database_backup
BACK_DIR=$HOME_DIR/$BACK_DIR_NAME
BINLOG_DIR=$HOME_DIR/mysql-var/data
# document dir
DOC_DIR=$HOME_DIR/bibsonomy_docs
# rsync options
RHOST=daffy.kde.informatik.uni-kassel.de
RUSER=bibbackup
RDIR=backup
ROPTIONS="-av --rsh=ssh"
# location of mysqldump
MYSQLDUMP=$HOME_DIR/mysql/bin/mysqldump
OPTIONS="--add-drop-table \
         --add-locks \
         --create-options\
         --disable-keys\
         --quick\
         --quote-names\
         --flush-logs\
         --no-autocommit\
         --delete-master-logs\
         --single-transaction\
         --result-file=$BACK_DIR/dump_$CURRENT_DAY.sql"
# needed to get binlog names (improvement: give binlog generic name)
DBHOST=`hostname`

# check number of arguments
if [ $# -ne 2 ]; then
  echo "usage:"
  echo "  SKRIPT action database"
  echo "where action is either full or incr"
  exit
fi
ACTION=$1
DB=$2

#########################################################################
# FULL backup
if [ $ACTION = "full" ]; then
  # remove last backup
  if [ -d $BACK_DIR.last ]; then
    rm -Rf $BACK_DIR.last
    # do this on the remote host
    ssh $RUSER@$RHOST rm -Rf $RDIR/$BACK_DIR_NAME.last
  fi
  # if exists, move current backup to .last
  if [ -d $BACK_DIR ]; then
    mv $BACK_DIR $BACK_DIR.last
    # do this on the remote host
    ssh $RUSER@$RHOST mv $RDIR/$BACK_DIR_NAME $RDIR/$BACK_DIR_NAME.last
  fi
  mkdir $BACK_DIR
  # do this on the remote host
  chmod go-rwx $BACK_DIR
  # do full backup
  $MYSQLDUMP $OPTIONS $DB
fi

#########################################################################
# INCREMENTAL backup
if [ $ACTION = "incr" ]; then
  # if not exists, create BACKUP DIRECTORY
  if [ ! -d $BACK_DIR ]; then
    mkdir $BACK_DIR
  fi
  # flush logs
  $HOME_DIR/mysql/bin/mysqladmin flush-logs
  # copy every non-existing file 
  for i in $BINLOG_DIR/$DBHOST-bin.*; do
    FILE=$(echo $i | sed "s/.*\///")
    if [ ! -f $BACK_DIR/$FILE ]; then
      cp $i $BACK_DIR/$FILE
    fi
  done 
  # delete last two files (index and current binlog) from backup dir
  for i in $(ls $BINLOG_DIR/$DBHOST-bin.* | tail -n 2); do
    FILE=$(echo $i | sed "s/.*\///")
    rm $BACK_DIR/$FILE
  done
fi

#########################################################################
# do rsync with daffy
rsync $ROPTIONS $BACK_DIR $RUSER@$RHOST:$RDIR
rsync $ROPTIONS $BACK_DIR.last $RUSER@$RHOST:$RDIR

# backup document directory
rsync $ROPTIONS $DOC_DIR $RUSER@$RHOST:$RDIR
