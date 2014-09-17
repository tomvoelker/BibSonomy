#!/bin/bash

# TODO: 
# * switch back emailwebmaster 
# * switch on actual maven deployment
# * switch on actual archiving


# REGISTRATION
# * register all projects in $webapp
# ** as key use the deploy profile (e.g. gromit)
# ** as webapp specify the fitting webapp overlay
# * currently most projects use tomcat6 for deployment with another tomcat register the version in $tomcat
# * if you want the system to be build upon calling the "pumas" target, register it in the array $pumas
# * if you want the system to archive its webapp register it as archivable
# * if you want to disable mails and security questions before deployment register it in the array $unnoticed

declare -A webapp
# Tests
webapp[gromit]=bibsonomy-webapp
# PUMA test systems
webapp[puma_ks_dev]=bibsonomy-webapp
webapp[puma_ffm_rc]=bibsonomy-webapp-puma-frankfurt-main
webapp[puma_s_dev]=bibsonomy-webapp-puma-stuttgart

# BibSonomy
webapp[gandalf]=bibsonomy-webapp
webapp[slave_kassel]=bibsonomy-webapp
webapp[slave_wuerzburg]=bibsonomy-webapp
webapp[slave_hannover]=bibsonomy-webapp
webapp[slave_hetzner]=bibsonomy-webapp
# PUMA sandbox
webapp[puma_sandbox]=bibsonomy-webapp-puma-sandbox
# PUMA productive systems
webapp[puma_ks_prod]=bibsonomy-webapp
webapp[puma_ffm_prod]=bibsonomy-webapp-puma-frankfurt-main
webapp[puma_mz_prod]=bibsonomy-webapp-puma-mainz
webapp[puma_mr_prod]=bibsonomy-webapp-puma-marburg
webapp[puma_da_prod]=bibsonomy-webapp-puma-darmstadt
webapp[puma_gi_prod]=bibsonomy-webapp-puma-giessen

declare -A tomcat
tomcat[slave_hannover]=7
tomcat[slave_kassel]=7
tomcat[slave_hetzner]=7
tomcat[gromit]=7

declare -A archivable
archivable[gandalf]=true

declare -A unnoticed
unnoticed[gromit]=true
unnoticed[puma_ks_dev]=true
unnoticed[puma_ffm_rc]=true
unnoticed[puma_gi_prod]=true
unnoticed[puma_s_dev]=true

pumas=(puma_ks_prod puma_ffm_prod puma_mz_prod puma_mr_prod puma_da_prod puma_gi_prod)

############################


targetProject=$1
PUMAS_TARGET=pumas

if [ -z "$targetProject" ]; then
    echo "Specifiy the target Project as command line argument. E.g. \"./deploy gromit\""
    exit
fi

if [ -z "${webapp[$targetProject]}" ] && [ $targetProject != $PUMAS_TARGET ]; then
    echo "The specified target project $targetProject is not registered for deployment in this script"
    exit
fi


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
BIBSONOMY_PATH=`pwd`

# the war files to be archived currently only for the bibsonomy-webapp
WARPATTERN=target/bibsonomy-webapp-*.war

# email addresses
EMAILWEBMASTER=webmaster@bibsonomy.org
#EMAILWEBMASTER=webmaster@bibsonomy.org
# the one which really gets the mail; pick one of the above
RECIPIENT=${EMAILWEBMASTER}
# comma separated: further recipients
#CCRECIPIENTS=stefani@cs.uni-kassel.de
CCRECIPIENTS=sbo@cs.uni-kassel.de,stefani@cs.uni-kassel.de

# today's date (used to timestamp WAR file)
TODAY=`date +"%Y-%m-%dT%H:%M:%S"`

# default tomcat version
DEFAULT_TOMCAT_VERSION=6

#
# Print some help text
#
help() {
    echo "Use \"./deploy.sh PROJECT\" to start the deployment. E.g. use \"deploy.sh gromit\" to deploy to gromit. Specify any valid project name as command line argument. Specify \"${PUMAS_TARGET}\" as argument to deploy to all PUMA productive systems at once."
}
    
#
# Create an email for documentation purposes
#
document() {
    if [ ! -z ${unnoticed[$targetProject]} ] && [ ${unnoticed[$targetProject]} = true ]; then return; fi
    rm -f ${TMPLOG}
    rm -f ${BODY_MAIL}
    read -p "Who are you? " WHO
    read -p "Why are you deploying to ${targetProject}? " WHY 
    echo -e "### who: $WHO\n### why: $WHY\n\n" > ${BODY_MAIL}
}

#
# Create an email for documentatino purposes and ask additional security question 
# To be used when many systems are deployed at once
#
documentMany() {
    if [ ! -z ${unnoticed[$targetProject]} ] && [ ${unnoticed[$targetProject]} = true ]; then return; fi
    document
    echo -e "Do you really want to consecutively deploy to the following systems:"    
    for i in ${pumas[*]}
    do
	echo -e "* $i"
    done
    read -p "Deploy (YES/NO)? " DEPLOY_MANY
    if [ "$DEPLOY_MANY" != "YES" ]; then echo "Deployment aborted."; exit; fi
    echo -e "### what: ${pumas[*]} ###" >> ${BODY_MAIL}
}


#
# Call the deploy of a webapp ($1) to a target server ($2)
#
deploy() {
    target=$1
    webapp=${webapp[$target]}
    tomcatVersion=${tomcat[$target]}
    if [ -z "$tomcatVersion" ]; then
	tomcatVersion=$DEFAULT_TOMCAT_VERSION
    fi    
    cd $BIBSONOMY_PATH/$webapp
    clean

    if [ $webapp != 'bibsonomy-webapp' ]; then
	echo -e "\nInstalling overlay $webapp ...";
	${MAVEN} clean install
    fi
    echo -e "\nDeploying webapp $webapp to target $target ...";
    ${MAVEN} -Dtomcat-server=${target} -Dmaven.test.skip tomcat${tomcatVersion}:redeploy | ${TEE} -a ${TMPLOG}
    echo "Done."
    if [ ! -z ${archivable[$target]} ] && [ ${archivable[$target]} = true ]; then archive; fi
    cd $BIBSONOMY_PATH
}


sendMail() {
    if [ ! -z ${unnoticed[$targetProject]} ] && [ ${unnoticed[$targetProject]} = true ]; then return; fi
    echo -e "\nSending report mail ..."
    ${MAIL} -s "[BibSonomy-Deploy] make ${targetProject}" -a ${TMPLOG} -c ${CCRECIPIENTS} ${RECIPIENT} < ${BODY_MAIL}
    echo "Done."
}

clean() {
    echo -e "\nDeleting files from WEB-INF/classes and WEB-INF/lib..."
    rm -rf src/main/webapp/WEB-INF/classes/*
    rm -rf src/main/webapp/WEB-INF/lib/*.jar
    echo "Done."
}

archive() {
    for i in `ls ${WARPATTERN}`; do
	j=`echo $i | sed "s/.*\///"`; 
	echo -e "\nArchiving $i in ${ARCHIVE}/${TODAY}_$j";
	scp $i "${ARCHIVE}/${TODAY}_$j"
    done
}

if [ $targetProject = $PUMAS_TARGET ]; then 
    documentMany
    for puma in ${pumas[*]}; do 
	deploy $puma
    done
else
    document
    deploy $targetProject
fi

sendMail
