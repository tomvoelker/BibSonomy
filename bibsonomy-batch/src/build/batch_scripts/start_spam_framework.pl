#!/usr/bin/perl
##############
# start spam framework
#
use strict; 

if (am_i_running($ENV{'TMP'}."/start_spam_framework.pid")) {
  #print "another instance of start_spam_framework.pl is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

# export spam framework directory
#system ("export SPAM_HOME=/home/kde/bibbackup/bibsonomy-spam-framework");
$ENV{"SPAM_HOME"} = "/home/kde/bibbackup/bibsonomy-spamframework-client-api-2.0.2-dist.dir";

# start spam framework
system ("/usr/lib/jvm/java-6-sun-1.6.0.13/bin/java -Xmx6G -jar /home/kde/bibbackup/bibsonomy-spamframework-client-api-2.0.2-dist.dir/bibsonomy-spamframework-2.0.2.jar > $ENV{'SPAM_HOME'}/log_info");

#################################
# subroutines
#################################
# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
  my $LOCKFILE = shift;
  my $PID ="";
  if (open (FILE, "<$LOCKFILE")) {
    while (<FILE>) {
      $PID = $_;
    }
    close (FILE);
    chomp($PID);

    if (kill(0,$PID)) {
      return 1;
    }
  }
  open (FILE, ">$LOCKFILE");
  print FILE $$;
  close (FILE);
  return 0;
}
