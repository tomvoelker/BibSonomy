#!/bin/bash

# This Script fills the sandbox with a dump of the system and updates the dates of all posts
# It expects the dump to be in the directory $sandboxInitDir together with all documentes and previews
# Arguments
# The date of the youngest post in the database in the format "yyyymmdd" (used to update all posts to the current date)

shopt -s expand_aliases
source ~/.bash_aliases


database="sandbox_puma"
dbUser="sandbox_puma"
dbPw=""
pumaHome=/home/puma-sandbox
pumaDataDir=${pumaHome}/pumadata
sandboxInitDir=${pumaHome}/init_sandbox
diffDateString=$1
mysqlConnection="mysql --default-character-set=utf8 -u $dbUser -p$dbPw -N --database $database"


# RESET Database
# we delete from all tables and than just add new data from the dump. This way we will not need SUPER privileges

# Get list of all tables, then delete from each table individually
showTables="$mysqlConnection -e \"show tables\""
IFS=$'\n'

for table in $(eval $showTables); do
  eval $mysqlConnection -e "\"DELETE FROM ${table}\""
done

# Enter all default data into the database
eval $mysqlConnection < ${sandboxInitDir}/initial_sandbox_data/init_sandbox_dump.sql

# RESET File directoris (uploaded pictures, documents, etc.)
# delete current directory, copy default directories

#/etc/init.d/tomcat stop
rm -fr ${pumaDataDir}/lucene
rm -fr ${pumaDataDir}/puma*
cp -r ${sandboxInitDir}/initial_sandbox_data/pumadata/* ${pumaDataDir}


# Modify the dates of the posts such that the posts look "fresh" (recently added)
# otherwise, the popular page will soon be empty and the system look deserted

# dayDiff is the difference between today and the youngest date in the dump we have loaded into the database 2013-11-09

dayDiff=$(( ($(date +%s) - $(date --date="$diffDateString" +%s) )/(60*60*24) -1))

# add the dayDiff to all posts

function updateDate() {
    table=$1;
    dateVar=$2;
    eval $mysqlConnection -e "\"UPDATE $table SET $dateVar = DATE_ADD($dateVar,INTERVAL $dayDiff DAY)\""
}

updateDate "bibtex" "date"
updateDate "bibtex" "change_date"
updateDate "bookmark" "date"
updateDate "bookmark" "change_date"
updateDate "tas" "date"
updateDate "tas" "change_date"
updateDate "user_wiki" "date"

# rebuild all lucene indexes (with the new dates)
/usr/bin/java -cp .:${pumaHome}:${pumaHome}/bibsonomy-internal-tools-3.0.1-SNAPSHOT-executable.jar org.bibsonomy.lucene.util.LuceneIndexGenerator 1 publication bookmark goldStandardPublication

/etc/init.d/puma-sandbox restart
