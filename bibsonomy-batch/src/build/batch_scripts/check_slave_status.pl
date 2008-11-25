#!/usr/bin/perl -w 

use strict;
use DBI();

# Checks if the slave is still running and that it's not too much behind the master
#
# Changes:
# 2008-11-14 (rja)
# - initial version
# 2008-11-20 (rja)
# - adapted to work with nagios instead of cron
# - moved password from environment to ~/etc/check_slave_status.pass
# 2008-11-25 (rja)
# - script now tries to restart the slave, when it detect an unknown error
#   for this, the SUPER privilege is necessary!
#

# maximal time the slave may be behind the master (in seconds)
my $WARNING = 300;   # time in seconds 
my $CRITICAL = 3600; # time in seconds

# a user which has "REPLICATION CLIENT" privilege on the slave:
# GRANT REPLICATION CLIENT ON *.* TO bibsonomy_status@'localhost' identified by '';
# for restarting the slave, the SUPER privilege is also necessary
my $user = "bibsonomy_status";
my $password = `cat /home/kde/bibbackup/etc/check_slave_status.pass`;
chomp ($password);

my $seconds = -1;

# query database
my $dbh = DBI->connect("DBI:mysql:;host=localhost:6033;mysql_socket=/var/mysql/run/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
my $stm = $dbh->prepare("show slave status");
$stm->execute();

while(my $row=$stm->fetchrow_hashref) {
  if (defined $row->{"Seconds_Behind_Master"}) {
    $seconds = $row->{"Seconds_Behind_Master"};
  }
}
$dbh->disconnect();

my $perfdata = "|behind=$seconds;$WARNING;$CRITICAL;";

# evaluate result
if ($seconds > $CRITICAL) {
  print "CRITICAL: $seconds seconds behind master$perfdata";
  exit 2;
} elsif ($seconds > $WARNING) {
  print "WARNING: $seconds seconds behind master$perfdata";
  exit 1;
} elsif ($seconds > -1) {
  print "OK: $seconds seconds behind master$perfdata";
  exit 0;
}

print "CRITICAL: unknown error occured - could not get slave status$perfdata";

# try to restart slave
$dbh = DBI->connect("DBI:mysql:;host=localhost:6033;mysql_socket=/var/mysql/run/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
$stm = $dbh->prepare("start slave");
$stm->execute();
$dbh->disconnect();

exit 2;

