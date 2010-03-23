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
export DEPLOY_SERVER=${MAKECMDGOALS}

# programs
MAVEN=mvn
TEE=tee
MAIL=mail
JAVA=${JAVA_HOME}/bin/java

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

gromit: mvn_deploy 

# TODO: document/mail/archive, too?
odie: mvn_deploy
joe: mvn_deploy

mvn_deploy: clean
	${MAVEN} -Dmaven.test.skip war:inplace tomcat:undeploy tomcat:deploy | ${TEE} -a ${TMPLOG}

mail:
	${MAIL} -s "[BibSonomy-Deploy] make ${MAKECMDGOALS}" ${RECIPIENT} < ${TMPLOG}

archive:
	for i in $(wildcard ${WARPATTERN}); do \
		j=`echo $$i | sed "s/.*\///"`; \
		cp $$i ${ARCHIVE}/${TODAY}_$$j; \
	done

#
# Ask the user who he is and why he's deploying.
#
document:
	@rm ${TMPLOG}
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
