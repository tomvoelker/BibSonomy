#/bin/bash

export MASTER_DB=bibsonomy
export MASTER_HOST=TODO
export MASTER_PORT=3306
export MASTER_SOCK=/var/run/mysql.sock
export MASTER_USER=***
export MASTER_PASS=***

# for backup enable the following line
# mysqldump -P $MASTER_PORT -u $MASTER_USER -p$MASTER_PASS $MASTER_DB user pendingUser log_user > usertable_backup.sql
# for schema migration enable the following line
# mysql -u $MASTER_USER -p$MASTER_PASS $MASTER_DB -P $MASTER_PORT -h $MASTER_HOST < add_salt_to_user_tables.sql

./addSaltToPassword.pl