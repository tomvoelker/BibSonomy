#!/usr/bin/perl -w 

#
# checks for long running queries and kills them
# 
# arguments:
#   host - either MASTER or SLAVE
# 
# Changes:
# 2011-07-28 (rja)
# - using Common.pm now
#

use strict;
use DBI();
use Data::Dumper;
use Common qw(debug check_running);

# don't run twice
check_running();

# Maximales Alter in Sekunden
my $MAXTIME = 30;

my $msg = "";

my $dbh = Common::get_connection(shift @ARGV);

my $stm = $dbh->prepare("show full processlist");

$stm->execute();


my %tobekilled = ();
while(my $row=$stm->fetchrow_hashref) {
    debug($row->{"State"});
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
#    print STDERR $msg;
}
