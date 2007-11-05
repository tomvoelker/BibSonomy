#!/bin/sh

# get current day of year
CURRENT_DAY=`date +%j`
# location of directories
HOME_DIR=/home/bibsonomy
BACK_DIR_NAME=database_backup
BACK_DIR=$HOME_DIR/$BACK_DIR_NAME
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
         --no-data\
         --master-data=1\
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
  $MYSQLDUMP $OPTIONS $DB
fi

#########################################################################
# INCREMENTAL backup
if [ $ACTION = "incr" ]; then
  # flush logs
  $HOME_DIR/mysql/bin/mysqladmin flush-logs
fi
