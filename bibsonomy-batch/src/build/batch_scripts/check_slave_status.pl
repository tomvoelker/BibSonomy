#!/usr/bin/perl -w 

use strict;
use DBI();

# Checks if the slave is still running and that it's not too much behind the master
#
# Changes:
# 2008-11-14 (rja)
# - initial version

# maximal time the slave may be behind the master (in seconds)
my $MAXTIME = 30;

# a user which has "REPLICATION CLIENT" privilege on the slave:
# GRANT REPLICATION CLIENT ON *.* TO bibsonomy_status@'localhost' identified by '';
my $user = "bibsonomy_status";
my $password = $ENV{'DB_PASS'}; 

my $dbh = DBI->connect("DBI:mysql:;host=localhost:6033;mysql_socket=/var/mysql/run/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

my $stm = $dbh->prepare("show slave status");

$stm->execute();

my $error = 1;

while(my $row=$stm->fetchrow_hashref) {
  if (defined $row->{"Seconds_Behind_Master"}) {
    my $seconds = $row->{"Seconds_Behind_Master"};
    if ($seconds > $MAXTIME) {
      # TODO: include slave name
      print STDERR "Slave is " . $seconds . " seconds behind master!\n";
    } else {
      $error = 0;
    }
  } else {
    print STDERR "Could not get slave status.\n";
  }
}

if ($error) {
  print STDERR "An unknown error occurred!\n";
}


$dbh->disconnect();


