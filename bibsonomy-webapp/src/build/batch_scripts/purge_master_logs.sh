#!/bin/sh

# purges the binary logs (on the master) which are older than two weeks

# which day was it two weeks ago?
TWO_WEEKS_BEFORE=`date --date='-2 weeks' +'%Y-%m-%d'`

# where can I find mysql?
MYSQL=$HOME/mysql/bin/mysql

# purge master logs
$MYSQL -e "PURGE MASTER LOGS BEFORE '$TWO_WEEKS_BEFORE'";
