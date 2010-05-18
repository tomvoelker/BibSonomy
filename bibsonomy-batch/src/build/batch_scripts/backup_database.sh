#!/bin/sh

#
# Makes full and incremental backups of a bibsonomy database.
#
# Changes:
#   2010-05-18 (rja)
#   - renamed search tables to search_old_*
#   2009-11-30
#   - added ignore-table options to mysqldump to ignore search tables
#   2007-11-29
#   - adopted to odie
#   2007-11-20
#   - cleaned paths and file names
#
#


# get current day of year (to name SQL dump)
CURRENT_DAY=`date +%j`

# location of backup directory
BACKUP_DIR=$HOME/backup/database_backup

# database specific paths
DB_DIR=/var/mysql
DB_DATA_DIR=$DB_DIR/data                               # bin-/relay-log directory
DB_RELAY_LOG=$DB_DATA_DIR/mysqld-relay-bin             # prefix of relay log files
DB_RELAY_LOG_INDEX=$DB_DATA_DIR/mysqld-relay-bin.index # NOTE: configure those variables
DB_RELAY_LOG_INFO=$DB_DATA_DIR/relay-log.info          # in my.cnf accordingly!
DB_MASTER_INFO_FILE=$DB_DATA_DIR/master.info           #
DB_BIN_LOG=$DB_DATA_DIR/mysql-bin                      # here the crucial data is!
DB_BIN_LOG_INDEX=$DB_DATA_DIR/mysql-bin.index          #


DB_SOCKET=$DB_DIR/run/mysqld.sock     # MySQL socket file
DB_MYSQLDUMP=/usr/bin/mysqldump       # location of mysqldump
DB_MYSQLADMIN=/usr/bin/mysqladmin     # location of mysqladim

# check number of arguments
if [ $# -ne 2 ]; then
  echo "usage:"
  echo "  $0 action database"
  echo "where action is either full or incr"
  exit
fi
ACTION=$1
DB=$2

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
         --ignore-table=${DB}.search_old_bibtex \
         --ignore-table=${DB}.search_old_bookmark \
         --result-file=$BACKUP_DIR/dump_$CURRENT_DAY.sql"

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
  for i in $DB_BIN_LOG.*; do
    if [ ! -f $BACKUP_DIR/`basename $i` ]; then
      cp $i $BACKUP_DIR
    fi
  done 
  # delete last bin log (will be copied at next run)
  for i in $(ls $DB_BIN_LOG.0* | tail -n 1); do
    rm $BACKUP_DIR/`basename $i`
  done

  # copy every non-existing relay log file 
  for i in $DB_RELAY_LOG.*; do
    if [ ! -f $BACKUP_DIR/`basename $i` ]; then
      cp $i $BACKUP_DIR
    fi
  done 

  # copy remaining files (index, info, etc)
  cp $DB_RELAY_LOG_INDEX  $BACKUP_DIR
  cp $DB_RELAY_LOG_INFO   $BACKUP_DIR
  cp $DB_MASTER_INFO_FILE $BACKUP_DIR
  cp $DB_BIN_LOG_INDEX    $BACKUP_DIR

fi
