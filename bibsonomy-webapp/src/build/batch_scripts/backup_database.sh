#!/bin/sh

# get current day of year (to name SQL dump)
CURRENT_DAY=`date +%j`

# location of backup directory
BACKUP_DIR=$HOME/backup/database_backup

# database specific paths
DB_DATA_DIR=/var/mysql/data               # bin-/relay-log directory
DB_RELAY_LOG=mysqld-relay-bin             # prefix of relay log files
DB_RELAY_LOG_INDEX=mysqld-relay-bin.index # NOTE: configure those variables
DB_RELAY_LOG_INFO=relay-log.info          # in my.cnf accordingly!
DB_MASTER_INFO_FILE=master.info           #
DB_BIN_LOG=mysql-bin                      # here the crucial data is!
DB_BIN_LOG_INDEX=mysql-bin.index          #


DB_SOCKET=/var/mysql/run/mysqld.sock  # MySQL socket file
DB_MYSQLDUMP=/usr/bin/mysqldump       # location of mysqldump
DB_MYSQLADMIN=/usr/bin/mysqladmin     # location of mysqladim

# options for mysqldump
DB_MYSQLDUMP_OPTIONS="--add-drop-table \
         --add-locks \
         --create-options \
         --disable-keys \
         --quick \
         --quote-names \
         --flush-logs \
         --no-autocommit \
         --master-data=1 \
         --delete-master-logs \
         --single-transaction \
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
  echo $DB_MYSQLDUMP $DB_MYSQLDUMP_OPTIONS $DB
  $DB_MYSQLDUMP $DB_MYSQLDUMP_OPTIONS $DB
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

  # copy every non-existing bin log file 
  for i in $DB_DATA_DIR/$DB_BIN_LOG.*; do
    FILE=$(echo $i | sed "s/.*\///")
    if [ ! -f $BACKUP_DIR/$FILE ]; then
      cp $i $BACKUP_DIR/$FILE
    fi
  done 
  # delete last bin log (will be copied at next run)
  for i in $(ls $DB_DATA_DIR/$DB_BIN_LOG.0* | tail -n 1); do
    FILE=$(echo $i | sed "s/.*\///")
    rm $BACKUP_DIR/$FILE
  done

  # copy every non-existing relay log file 
  for i in $DB_DATA_DIR/$DB_RELAY_LOG.*; do
    FILE=$(echo $i | sed "s/.*\///")
    if [ ! -f $BACKUP_DIR/$FILE ]; then
      cp $i $BACKUP_DIR/$FILE
    fi
  done 

  # copy remaining files (index, info, etc)
  cp $DB_DATA_DIR/$DB_RELAY_LOG_INDEX $BACKUP_DIR/$DB_RELAY_LOG_INDEX
  cp $DB_DATA_DIR/$DB_RELAY_LOG_INFO $BACKUP_DIR/$DB_RELAY_LOG_INFO
  cp $DB_DATA_DIR/$DB_MASTER_INFO_FILE $BACKUP_DIR/$DB_MASTER_INFO_FILE
  cp $DB_DATA_DIR/$DB_BIN_LOG_INDEX $BACKUP_DIR/$DB_BIN_LOG_INDEX

fi
