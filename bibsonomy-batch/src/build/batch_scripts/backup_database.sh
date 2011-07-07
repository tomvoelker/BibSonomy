#!/bin/sh

#
# Makes full and incremental backups of a bibsonomy database.
#
# Changes:
#   2011-07-07 (rja)
#   - changed paths (DB_DATA_DIR, DB_SOCKET, DB_MYSQLDUMP, DB_MYSQLADMIN) for backup on new slave (former gandalf)
#   2011-04-07 (rja)
#   - removed options for search tables
#   2010-05-18 (rja)
#   - renamed search tables to search_old_*
#   - adopted options for joe ($DB_DIR, $DB_SOCKET)
#   2009-11-30
#   - added ignore-table options to mysqldump to ignore search tables
#   2007-11-29
#   - adopted to odie
#   2007-11-20
#   - cleaned paths and file names
#
#
# Requirements:
# 
# database must write proper relay logs and bin logs; changes in /etc/my.cnf:
#   log-slave-updates   = 1
#   log-bin             = mysql-bin
#   relay-log-purge     = 1  # purge, we don't need it for backup!
#   relay-log           = mysqld-relay-bin
#   relay-log-index     = mysqld-relay-bin.index
#   relay-log-info-file = relay-log.info
# 
# database needs user accounts for backup':
#   GRANT RELOAD, SUPER, REPLICATION CLIENT ON *.* TO 'bibdump'@'localhost' IDENTIFIED BY '';
#   GRANT SELECT ON `bibsonomy`.* TO 'bibdump'@'localhost';
# 
# ~/.my.cnf:
#   [client]
#   socket   = /var/run/mysqld/mysqld.sock        # joe
#   user     = bibdump
#   password = ***


# get current day of year (to name SQL dump)
CURRENT_DAY=`date +%j`

# location of backup directory
BACKUP_DIR=/home/kde/bibbackup/backup/database_backup

# database specific paths
DB_DATA_DIR=/home/bibsonomy/mysql-var/data             # bin-/relay-log directory
DB_RELAY_LOG=$DB_DATA_DIR/mysqld-relay-bin             # prefix of relay log files
DB_RELAY_LOG_INDEX=$DB_DATA_DIR/mysqld-relay-bin.index # NOTE: configure those variables
DB_RELAY_LOG_INFO=$DB_DATA_DIR/relay-log.info          # in my.cnf accordingly!
DB_MASTER_INFO_FILE=$DB_DATA_DIR/master.info           #
DB_BIN_LOG=$DB_DATA_DIR/mysql-bin                      # here is the crucial data!
DB_BIN_LOG_INDEX=$DB_DATA_DIR/mysql-bin.index          #


DB_SOCKET=/home/bibsonomy/mysql-var/run/mysql.sock     # MySQL socket file
DB_MYSQLDUMP=/home/bibsonomy/mysql/bin/mysqldump       # location of mysqldump
DB_MYSQLADMIN=/home/bibsonomy/mysql/bin/mysqladmin     # location of mysqladim

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

