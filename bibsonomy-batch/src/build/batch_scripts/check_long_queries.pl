#!/usr/bin/perl -w 

use strict;
use DBI();
use Data::Dumper;

# Maximales Alter in Sekunden
my $MAXTIME = 30;

my $msg = "";

my $user = "bibsonomy";
my $password = $ENV{'DB_PASS'}; 
my $database = shift @ARGV;

my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/var/mysql/run/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

my $stm = $dbh->prepare("show full processlist");

$stm->execute();


my %tobekilled = ();
while(my $row=$stm->fetchrow_hashref) {
	#print $row->{"State"} . "\n";
	if (defined $row->{"State"} && ("statistics" eq $row->{"State"}) && ($row->{"Time"} > $MAXTIME)) {
		# push @tobekilled, $row->{"Id"};
		$tobekilled{$row->{"Id"}} = $row->{"Info"};
	}
}

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
	open MAIL, "| mail -s \"Queries gekillt\" bibsonomy\@cs.uni-kassel.de" or die;
	print MAIL $msg;
	close MAIL;
    print STDERR $msg;
}
