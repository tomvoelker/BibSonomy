#!/bin/sh

# get current day of year (to name SQL dump)
CURRENT_DAY=`date +%j`

# location of backup directory
BACKUP_DIR=$HOME/backup/database_backup

# database specific paths
DB_BINLOG_DIR=/var/mysql/data/        # binlog directory
DB_BINLOG_NAME=mysql-bin              # name (prefix) of binlog files
DB_SOCKET=/var/mysql/run/mysqld.sock  # MySQL socket file
DB_MYSQLDUMP=/usr/bin/mysqldump       # location of mysqldump
DB_MYSQLADMIN=/usr/bin/mysqladmin     # location of mysqladim

# options for mysqldump
DB_MYSQLDUMP_OPTIONS="\
         --add-drop-table \
         --add-locks \
         --create-options\
         --disable-keys\
         --quick\
         --quote-names\
         --flush-logs\
         --no-autocommit\
         --delete-master-logs\
         --single-transaction\
         --result-file=$BACKUP_DIR/dump_$CURRENT_DAY.sql"

# check number of arguments
if [ $# -ne 2 ]; then
  echo "usage:"
  echo "  $0 action database"
  echo "where action is either full or incr"
  exit
fi
ACTION=$1
DB=$2

#########################################################################
# FULL backup
if [ $ACTION = "full" ]; then
  # remove last backup
  if [ -d $BACKUP_DIR.last ]; then
    rm -Rf $BACKUP_DIR.last
  fi
  # if exists, move current backup to .last
  if [ -d $BACKUP_DIR ]; then
    mv $BACKUP_DIR $BACKUP_DIR.last
  fi
  # create new backup dir
  mkdir $BACKUP_DIR
  # do full backup
  $MYSQLDUMP $DB_MYSQLDUMP_OPTIONS $DB
fi

#########################################################################
# INCREMENTAL backup
if [ $ACTION = "incr" ]; then
  # if not exists, create BACKUP DIRECTORY
  if [ ! -d $BACKUP_DIR ]; then
    mkdir $BACKUP_DIR
  fi
  # flush logs
  $DB_MYSQLADMIN flush-logs
  # copy every non-existing file 
  for i in $DB_BINLOG_DIR/$DB_BINLOG_NAME.*; do
    FILE=$(echo $i | sed "s/.*\///")
    if [ ! -f $BACKUP_DIR/$FILE ]; then
      cp $i $BACKUP_DIR/$FILE
    fi
  done 
  # delete last two files (index and current binlog) from backup dir
  for i in $(ls $DB_BINLOG_DIR/$DB_BINLOG_NAME.* | tail -n 2); do
    FILE=$(echo $i | sed "s/.*\///")
    rm $BACKUP_DIR/$FILE
  done
fi
