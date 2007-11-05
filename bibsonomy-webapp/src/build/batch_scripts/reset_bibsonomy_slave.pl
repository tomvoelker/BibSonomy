#!/usr/bin/perl

use DBI;
use strict;

#
# since the master resets its log files once a week, we use this 
# script for the slaves to reset their master log position
#
# NOTE: this is not neccessary, if mysqldump on the master is 
# run with the --master-data=1 option (see man mysqldump)
#
#


my $DB_HOST="localhost";
my $DB_SOCK=$ENV{'DB_SOCK'};
my $DB_USER="root";
my $DB_PASS = $ENV{'DB_ROOT_PASS'};
my $DB="mysql";

my $DB_MASTER_LOG_FILE="gandalf-bin.000001";



my $dbh = DBI->connect("DBI:mysql:database=$DB;host=$DB_HOST;mysql_socket=$DB_SOCK", $DB_USER, $DB_PASS, {RaiseError => 0, AutoCommit => 1,"mysql_enable_utf8" => 1});

$dbh->do("STOP SLAVE;");
$dbh->do("CHANGE MASTER TO MASTER_LOG_FILE='$DB_MASTER_LOG_FILE' , MASTER_LOG_POS=0");
$dbh->do("START SLAVE;");

$dbh->disconnect;
