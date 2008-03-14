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

my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/mysql.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

my $stm = $dbh->prepare("show processlist");

$stm->execute();


my @tobekilled = ();
while(my $row=$stm->fetchrow_hashref) {
	if (defined $row->{"State"} && ("statistics" eq $row->{"State"}) && ($row->{"Time"} > $MAXTIME)) {
		push @tobekilled, $row->{"Id"};
	}
}

if (@tobekilled) {
	$msg .= "Versuche Jobs zu killen: " . (join ",", @tobekilled) . "\n";
	for my $id (@tobekilled) {
		$stm = $dbh->prepare("kill $id");
		$msg .= "kill $id\n";
		$stm->execute;
	}
}

$dbh->disconnect();

if ($msg) {
#	open MAIL, "| mail -s \"Queries gekillt\" bibsonomy\@cs.uni-kassel.de" or die;
#	print MAIL $msg;
#	close MAIL;
    print STDERR $msg;
}


