#!/bin/bash
#
# BibSonomy - A blue social bookmark and publication sharing system.
#
# Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
#                               University of Kassel, Germany
#                               http://www.kde.cs.uni-kassel.de/
#                           Data Mining and Information Retrieval Group,
#                               University of Würzburg, Germany
#                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
#                           L3S Research Center,
#                               Leibniz University Hannover, Germany
#                               http://www.l3s.de/
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#


# REGISTRATION
# * if you want the system to archive its webapp register it as archivable
# * if you want to disable mails and security questions before deployment register it in the array $unnoticed

# environment variables
export MAVEN_OPTS='-Xmx1024m -Xms512m'
export JAVA_HOME=/usr/lib/jvm/java-7-oracle

# programs
MAVEN=mvn
TEE=tee
MAIL=mail
JAVA=${JAVA_HOME}bin/java

# files
TMPLOG=/tmp/deploy.log
BODY_MAIL=/tmp/body.txt
ARCHIVE=homes.cs.uni-kassel.de:archived_war_files
BIBSONOMY_PATH=$PWD

# the war files to be archived currently only for the bibsonomy-webapp
WARPATTERN=target/bibsonomy-webapp-*.war

TOMCAT_VERSION=7

# email addresses
EMAILWEBMASTER=tom.hanika@cs.uni-kassel.de
# the one which really gets the mail; pick one of the above
RECIPIENT=${EMAILWEBMASTER}
# comma separated: further recipients
CCRECIPIENTS=

# today's date (used to timestamp WAR file)
TODAY=`date +"%Y-%m-%dT%H:%M:%S"`

declare -A archivable
archivable[gandalf]=true

declare -A unnoticed
unnoticed[gromit]=true

DEFAULT_WEBAPP=bibsonomy-webapp

checkParams() {
    if [ -z "$1" ]; then
        echo "Specifiy the target server as command line argument. E.g. \"./deploy gromit\""
        exit
    fi
}

#
# Create an email for documentation purposes
#
document() {
    server=$1
    if [ ! -z ${unnoticed[$server]} ] && [ ${unnoticed[$server]} = true ]; then return; fi
    rm -f ${TMPLOG}
    rm -f ${BODY_MAIL}
    read -p "Who are you? " WHO
    read -p "Why are you deploying to ${server}? " WHY 
    echo -e "### who: $WHO\n### why: $WHY\n\n" > ${BODY_MAIL}
}

#
# Call the deploy of a webapp ($1) to a target server ($2)
#
deploy() {
    webapp=$1
    server=$2
    action=$3
    if [ -z "$action"]; then
        action="redeploy"
    fi
    echo -e "\n${action}ing webapp $webapp to target $server ...";
    ${MAVEN} -f $webapp/pom.xml -Dtomcat-server=${server} -Dmaven.test.skip tomcat${TOMCAT_VERSION}:${action} | ${TEE} -a ${TMPLOG}
    echo "Done."
    if [ ! -z ${archivable[$server]} ] && [ ${archivable[$server]} = true ]; then archive $webapp; fi
}

build() {
    clean $1 #TODO: this should already be done by the clean target
}

sendMail() {
    server=$1
    if [ ! -z ${unnoticed[$server]} ] && [ ${unnoticed[$server]} = true ]; then return; fi
    echo -e "\nSending report mail ..."
    ${MAIL} -s "[BibSonomy-Deploy] make ${server}" -a ${TMPLOG} -c ${CCRECIPIENTS} ${RECIPIENT} < ${BODY_MAIL}
    echo "Done."
}

clean() {
    cleanPath=$1/src/main/webapp
    echo -e "\nDeleting files from WEB-INF/classes and WEB-INF/lib (basedir: $cleanPath)..."
    rm -rf $cleanPath/WEB-INF/classes/*
    rm -rf $cleanPath/WEB-INF/lib/*.jar
    echo "Done."
}

archive() {
    archivePath=$1
    for i in `ls $archivePath/${WARPATTERN}`; do
        j=`echo $i | sed "s/.*\///"`; 
        echo -e "\nArchiving $i to ${ARCHIVE}/${TODAY}_$j";
        <scp $i "${ARCHIVE}/${TODAY}_$j"
        echo "scp $i \"${ARCHIVE}/${TODAY}_$j\""
    done
}
