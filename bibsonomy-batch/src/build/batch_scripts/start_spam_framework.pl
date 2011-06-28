#!/usr/bin/perl
##############
# start spam framework
#
use strict; 
use Common qw(check_running);

check_running();

# export spam framework directory
#system ("export SPAM_HOME=/home/kde/bibbackup/bibsonomy-spam-framework");
$ENV{"SPAM_HOME"} = "/home/kde/bibbackup/bibsonomy-spamframework-client-api-2.0.2-dist.dir";

# start spam framework
system ("/usr/lib/jvm/java-6-sun-1.6.0.13/bin/java -Xmx6G -jar /home/kde/bibbackup/bibsonomy-spamframework-client-api-2.0.2-dist.dir/bibsonomy-spamframework-2.0.2.jar > $ENV{'SPAM_HOME'}/log_info");
