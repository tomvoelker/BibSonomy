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
# e.g., "maven gromit" deploys to gromit.
#
# Changes: 
# 2009-07-30 (rja):
# - initial vesion
#


# environment variables
export MAVEN_OPTS=-Xmx1024m -Xms512m
export JAVA_HOME=/usr/lib/jvm/java-6-sun/
export DEPLOY_SERVER=${MAKECMDGOALS}

# programs
MAVEN=mvn
JAVA=${JAVA_HOME}/bin/java

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
	${MAVEN} install

test:
	${MAVEN} test

gandalf: mvn_deploy

gromit: mvn_deploy 

odie: mvn_deploy

mvn_deploy:
	${MAVEN} -Dmaven.test.skip war:inplace tomcat:undeploy tomcat:deploy

