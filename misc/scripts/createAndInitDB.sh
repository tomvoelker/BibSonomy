#! /bin/bash



HOST=$1;

if ([[ -z $HOST ]]);
then
echo "Arguments are: host(required) and prefix (optional)"
exit -1
fi

PREFIX=$2;

ROOT_PASSWORD=""
BASE_PASSWORD=""
BASE_ROPASSWORD=""

PASSWORD=${PREFIX}$BASE_PASSWORD
ROPASSWORD=${PREFIX}$BASE_ROPASSWORD

BASE_NAME="";

if ([[ -n $PREFIX ]]);
then
BASE_NAME=${PREFIX}_$BASE_NAME;
fi


commands="
CREATE USER '${BASE_NAME}puma'@'$HOST' IDENTIFIED BY '$PASSWORD'; 
CREATE DATABASE IF NOT EXISTS \`${BASE_NAME}puma\`; 
GRANT ALL PRIVILEGES ON ${BASE_NAME}puma.* TO '${BASE_NAME}puma'@'$HOST'; 

CREATE USER '${BASE_NAME}puma_ro'@'$HOST' IDENTIFIED BY '$ROPASSWORD';
GRANT SELECT ON ${BASE_NAME}puma.* TO '${BASE_NAME}puma_ro'@'$HOST';

CREATE USER '${BASE_NAME}osocial'@'$HOST' IDENTIFIED BY '$PASSWORD';
CREATE DATABASE IF NOT EXISTS ${BASE_NAME}puma_opensocial;
GRANT ALL PRIVILEGES ON ${BASE_NAME}puma_opensocial.* TO '${BASE_NAME}osocial'@'$HOST';

CREATE USER '${BASE_NAME}reco'@'$HOST' IDENTIFIED BY '$PASSWORD';
CREATE DATABASE IF NOT EXISTS ${BASE_NAME}puma_recommender;
GRANT ALL PRIVILEGES ON ${BASE_NAME}puma_recommender.* TO '${BASE_NAME}reco'@'$HOST';
CREATE DATABASE IF NOT EXISTS ${BASE_NAME}puma_item_recommender;
GRANT ALL PRIVILEGES ON ${BASE_NAME}puma_item_recommender.* TO '${BASE_NAME}reco'@'$HOST';

CREATE USER '${BASE_NAME}logger'@'$HOST' IDENTIFIED BY '$PASSWORD';
CREATE DATABASE IF NOT EXISTS ${BASE_NAME}logging;
GRANT ALL PRIVILEGES ON ${BASE_NAME}logging.* TO '${BASE_NAME}logger'@'$HOST';
flush privileges;
"

mysql -u root -p${ROOT_PASSWORD} -e "$commands"
#echo $commands

 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma < bibsonomy-db-schema.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma < bibsonomy-db-init.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma < bibsonomy-db-init-user.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma_opensocial < opensocial-db-schema.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma_recommender < recommender-db-schema.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}puma_item_recommender < item-recommender-db-schema.sql
 mysql -u root -p${ROOT_PASSWORD} -D ${BASE_NAME}logging < logging-db-schema.sql


