#
# This Makefile further automates the deploy process.
# use 
# 	make gandalf
# to deploy the webapp to gandalf. This Makefile then
# - sets the environment variables
# - undeploys/deploys the webapp
# - sends the maven output to ${RECIPIENT}
# - copies the WAR file with a timestamp into ${ARCHIVE}
#
# You can select another server by just giving its name,
# e.g., "make gromit" deploys to gromit.
#
# Changes:
# 2012-11-21 (rja):
# - migrated maven goals to new tomcat plugin, see http://tomcat.apache.org/maven-plugin-2.0/context-goals.html
# 2011-12-06 (dbe):
# - added two new deploy locations (slave_wuerzburg, slave_kassel
#   including email notification; removed old ones (joe, wuerzburg, odie)
# 2011-10-04 (rja):
# - deployments to wuerzburg are also send out via e-mail
# 2011-04-08 (rja):
# - deployments to joe are also send out via e-mail
# 2010-05-27 (dbe):
# - moved definition of tomcat server url to settings.xml
#   (profiles activated by -Dtomcat-server=XY) 
# 2010-02-09 (rja):
# - moved cleanup of WEB-INF/{lib,classes} into "clean" goal
# - added "document" goal
# - maven output is now appended to TMPLOG, thus we remove
#   TMPLOG before using it for documentation
# 2010-01-20 (dbe):
# - added cleanup of WEB-INF/lib and WEB-INF/classes
# 2009-07-30 (rja):
# - initial vesion
#


# environment variables
export MAVEN_OPTS=-Xmx1024m -Xms512m
export JAVA_HOME=/usr/lib/jvm/java-6-sun/

# programs
MAVEN=mvn
TEE=tee
MAIL=mail
JAVA=${JAVA_HOME}/bin/java
DEPLOY_SERVER=${MAKECMDGOALS}

# files
TMPLOG=/tmp/deploy.log
ARCHIVE=~/archived_war_files
# the war files to be archived
WARPATTERN = target/bibsonomy-webapp-*.war

# email addresses
EMAILDEVEL=bibsonomy2-devel@cs.uni-kassel.de
EMAILWEBMASTER=webmaster@bibsonomy.org
EMAILRJA=jaeschke@cs.uni-kassel.de
# the one which really gets the mail; pick one of the above
RECIPIENT=${EMAILWEBMASTER}
# comma separated: further recipients
CCRECIPIENTS=stefani@cs.uni-kassel.de

# today's date (used to timestamp WAR file)
TODAY=`date +"%Y-%m-%dT%H:%M:%S"`


####################################################################
# goals

# default: print help
help:
	@echo "use "
	@echo "  make gandalf"
	@echo "to deploy to gandalf"
	@echo "for further information, read the Makefile"

install:
	${MAVEN} install | ${TEE} ${TMPLOG}

test:
	${MAVEN} test | ${TEE} ${TMPLOG}

# 
# the main system: document and archive
#
gandalf: document mvn_deploy mail archive
#
# test system (just deploy)
#
gromit: mvn_deploy
#
# slave systems: document, deploy, email
# 
slave_kassel: document mvn_deploy mail
slave2_kassel: document mvn_deploy mail
slave_wuerzburg: document mvn_deploy mail
#
# when deploying to puma, we first need to open an ssh connection 
#
puma: ssh_puma document mvn_deploy mail


################################
# SUBGOALS
###############################

#
# deploy to tomcat server
#
# (the property "tomcat-server" activates the profiles defined in ~/.m2/settings.xml
mvn_deploy: clean
	${MAVEN} -Dtomcat-server=${DEPLOY_SERVER} -Dmaven.test.skip tomcat6:redeploy | ${TEE} -a ${TMPLOG}

#
# send notification mail
#
mail:
	${MAIL} -s "[BibSonomy-Deploy] make ${MAKECMDGOALS}" ${RECIPIENT} -c ${CCRECIPIENTS} < ${TMPLOG}

#
# archive the deployed war file
#
archive:
	for i in $(wildcard ${WARPATTERN}); do \
		j=`echo $$i | sed "s/.*\///"`; \
		cp $$i ${ARCHIVE}/${TODAY}_$$j; \
	done
#
# open SSH connection with tunnel to PUMA tomcat
#
ssh_puma:
	ssh -L 8077:bib-ws24.bibliothek.uni-kassel.de:8080 pumadeploy@puma.uni-kassel.de -p 30 -qTnN &

#
# Ask the user who he is and why he's deploying.
#
document:
	-rm ${TMPLOG}
	@read -p "Who are you? " WHO; \
	read -p "Why are you deploying to ${MAKECMDGOALS}? " WHY ;\
	echo "\n### who: $$WHO\n### why: $$WHY\n\n" >> ${TMPLOG}
#
# clean up WEB-INF/classes and WEB-INF/lib
#
clean:
	@echo "Deleting files from WEB-INF/classes and WEB-INF/lib..."
	@rm -rf src/main/webapp/WEB-INF/classes/*
	@rm -rf src/main/webapp/WEB-INF/lib/*.jar
	@echo "Done."
