#!/usr/bin/perl -w 

use strict;
use DBI();

# Checks if transaction of updating user groups failed, so that inconsistencies 
# considering flagged users can be found
#
# Changes:
# 2009-11-02 (bkr)
# - initial version
# 2009-11-25 (bkr)
# - split queries

########################################################
## configuration
#########################################################
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases

# query database to get inconsistent spammers

#print STDERR "Selecting tas users\n"; 
my $dbh = DBI->connect("DBI:mysql:database=bibsonomy;host=localhost:3306;mysql_socket=/var/run/mysqld/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

# select all users from tas which have public posts
my $stm = $dbh->prepare("SELECT user_name FROM tas WHERE `group`=0 GROUP BY user_name");
$stm->execute();
my %tas_users = ();
while(my $row=$stm->fetchrow_hashref) {
  if (defined $row->{"user_name"}) {
    $tas_users{$row->{"user_name"}}=1;
  }
}

#print STDERR "Selecting spammers\n"; 
# select spammers
my $stm1 = $dbh->prepare("SELECT user_name FROM user WHERE spammer=1;");
$stm1->execute();

my %users = ();
while(my $row=$stm1->fetchrow_hashref) {
  if (defined $row->{"user_name"}) {
    $users{$row->{"user_name"}}=1;
  }
}
$dbh->disconnect();

# evaluate result 
my @incompleteUsers = ();
foreach my $user (keys %users){
	if ($tas_users{$user}){
		push(@incompleteUsers, .$user);
	}
}

if ($incompleteUsers) {
    print "I have found some users that are flagged as spammer but have unflagged posts.\n";
    print "Please check the spam status of the following users:\n";
    foreach my $user (@incompleteUsers) {
	print "http://www.bibsonomy.org/admin/spam?aclUserInfo=" . $user . "\n";
    }
}


exit 2;

