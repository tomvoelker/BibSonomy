#!/usr/bin/perl
#
# This script deletes users which did non register within 24 hours
# from the "pendingUser" table
# 
# Environment variables:
#   DB_PASS
#
# Changes:
#   2010-07-26: (rja)
#   - initial version
#
use DBI();
use English;
use strict;

# don't run twice
if (am_i_running($ENV{'TMP'}."/$PROGRAM_NAME.pid")) {
  print "another instance of " . $PROGRAM_NAME . " is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}
# configuration
my $user     = "bibsonomy";   
my $password = $ENV{'DB_PASS'};
my $database = "bibsonomy";     
my $host     = "DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/run/mysql.sock";

# connect to database
my $dbh = DBI->connect($host, $user, $password, {RaiseError => 1, "mysql_auto_reconnect" => 1, "mysql_enable_utf8" => 1});
my $stm_delete_pending_users = $dbh->prepare ("DELETE FROM `pendingUser` WHERE (NOW() > DATE_ADD(reg_date, INTERVAL 24 HOUR))");
$stm_delete_pending_users->execute();
$dbh->disconnect();

# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
    my $LOCKFILE = shift;
    my $PID;

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
