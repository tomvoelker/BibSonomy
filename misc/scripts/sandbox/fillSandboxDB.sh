#! /bin/bash

# This Script fills the sandbox with a dump of the system and updates the dates of all posts                                                     
# It expects the dump to be in the directory $sandboxInitDir together with all documentes and previews                                           

database="sandbox_puma"
dbUser="sandbox_puma"
dbPw="INSERT PASSWORD"
dbHost="p210.puma.bibliothek.uni-kassel.de"
pumaDataDir=/home/puma/pumadata
sandboxInitDir=/home/puma/init_sandbox

mysqlConnection="mysql -h $dbHost -u $dbUser -p$dbPw -N --database $database"


# RESET Database
# we delete from all tables and than just add new data from the dump. This way we will not need SUPER privileges

# Get list of all tables, then delete from each table individually
showTables="$mysqlConnection -e \"show tables\""
IFS=$'\n'

for table in $(eval $showTables); do
  eval $mysqlConnection -e "\"TRUNCATE ${table}\""
done

# Enter all default data into the database
eval $mysqlConnection < ${sandboxInitDir}/initial_sandbox_data/init_sandbox_dump.sql

# RESET File directoris (uploaded pictures, documents, etc.)
# delete current directory, copy default directories

/etc/init.d/tomcat stop
rm -fr ${pumaDataDir}/lucene
rm -fr ${pumaDataDir}/puma*
cp -r ${sandboxInitDir}/initial_sandbox_data/pumadata/* ${pumaDataDir}

# Modify the dates of the posts such that the posts look "fresh" (recently added)
# otherwise, the popular page will soon be empty and the system look deserted

# dayDiff is the difference between today and the youngest date in the dump we have loaded into the database 2013-11-09

dayDiff=$(( ($(date +%s) - $(date --date="20140103" +%s) )/(60*60*24) -1))

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


/etc/init.d/tomcat start
## TODO Was ist mit Lucene?
