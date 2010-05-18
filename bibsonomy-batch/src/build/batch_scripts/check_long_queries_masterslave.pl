#!/usr/bin/perl -w 


#
# This script checks for queries which are hung in the state 'statistics'
#
# versions
# - 2007/??(??
#   * initial version
# - 2009(07/12 
#   * adapted to master / slave setting (dbe)
# - 2009/11/16
#   * modified behaviour: only slave is checked (dbe)
#
# modules
#
use strict;
use DBI();
use Data::Dumper;

#
# configuration
#
# Maximales Alter in Sekunden
my $MAXTIME = 30;
# DB settings
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases
my $database = shift @ARGV;


#
# variables
#
# error msg
my $msg = "";
# db connection handle + statement
my $dbh;
my $stm;

#
# DB connections
#
# fit to slave
my $slave    = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/var/mysql/run/mysqld.sock";
# fit to master
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";
#
# add here all dbconecttions to be checked
#
#my @dbConnections = ($slave, $master);
my @dbConnections = ($slave);

foreach my $dbConn (@dbConnections) {

	# connect to DB
	$dbh = DBI->connect($dbConn, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
	# show processlist
	$stm = $dbh->prepare("show full processlist");
	$stm->execute();

	# check for long-running queries	
	my %tobekilled = ();
	while(my $row=$stm->fetchrow_hashref) {
		if (defined $row->{"State"} && ("statistics" eq $row->{"State"}) && ($row->{"Time"} > $MAXTIME)) {
			# push @tobekilled, $row->{"Id"};
			$tobekilled{$row->{"Id"}} = $row->{"Info"};
		}
	}

	# try to kill them, if any
	if (%tobekilled) {
		$msg .= "Versuche Jobs zu killen: " . (join ",", keys(%tobekilled)) . "\n";
		while ((my $id, my $query) = each(%tobekilled)) {
			$stm = $dbh->prepare("kill $id");
			$msg .= "kill $id\n";
		 	$msg .= "query: $query\n\n";
			$stm->execute;
		}
	}

	$dbh->disconnect();

	if ($msg) {
		open MAIL, "| mail -s \"Queries gekillt (odie)\" bibsonomy\@cs.uni-kassel.de" or die;
		print MAIL $msg;
		close MAIL;
	    print STDERR $msg;
	}

}
