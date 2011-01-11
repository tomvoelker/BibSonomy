#!/bin/sh

#
# Runs the scraper tests by using the latest builds from continuum
# 
# 
# Input:
# - Environment variables AmazonAccessKey and AmazonSecretKey must
#   be set!
# 
# Output:
# - test runs results on STDOUT/STDERR
# 
# Changes:
# rja, 2009-10-16
# - added head comments
# - removed amazon access keys
# - documented additional xerces JAR
# dbe, 2010-03-02
# - changed location of scraper jar to hudson
# rja, 2010-05-10
# - added "-tr ... |tail -n1" to extraction of $VERSION to always get the
#   latest JAR
#

##########################################################################
# config variables
# 
# directory on this machine, where the BibSonomy (and other needed) JAR's
# are (except bibsonomy-scraper-test*.jar)
TOMCAT="/home/bibsonomy/tomcat/"
JARDIR=$TOMCAT"/webapps/bibsonomy-webapp/WEB-INF/lib"

#
# directory on the remote continuum machine, where the 
# bibsonomy-scraper-test*.jar is 
REPOSI="hudson@hudson:/var/lib/hudson/jobs/.m2/repository/org/bibsonomy/bibsonomy-scraper"
##########################################################################
# first: copy latest scraper-test JAR to this machine ...
# 
# get version number of current scraper JAR in tomcat dir
VERSION=$(ls -tr $JARDIR/bibsonomy-scraper*.jar | tail -n1 | sed -e 's/.*bibsonomy-scraper-//' -e 's/.jar//')
#
# copy corresponding test JAR from continuum to /tmp
#
TESTJAR=bibsonomy-scraper-${VERSION}-tests.jar
FROM=$REPOSI/$VERSION/$TESTJAR
TO=/tmp/$TESTJAR


# echo "copying version $VERSION of scraper test JAR from $FROM to $TO"
scp $FROM $TO


##########################################################################
# set class path
#
# initial:
#  - scraper test JAR in /tmp
#  - log4j JAR in tomcat/common/lib
#  - xerces in tomcat/common/lib
CLASSPATH=$TO:\
$(ls $TOMCAT/common/lib/log4j*.jar):\
$(ls $TOMCAT/common/lib/xerces*.jar):\
$(ls $TOMCAT/common/lib/commons-logging*.jar)
for jar in $JARDIR/*.jar; do
  CLASSPATH=$CLASSPATH:$jar
done


##########################################################################
# run tests
java -cp $CLASSPATH org.bibsonomy.scraper.UnitTestRunner

