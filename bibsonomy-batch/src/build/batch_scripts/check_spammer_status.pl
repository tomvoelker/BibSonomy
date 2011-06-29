#!/usr/bin/perl -w 

use strict;
use warnings;
use English;
use DBI();
use Common qw(debug get_slave get_master check_running);

# Checks if transaction of updating user groups failed, so that inconsistencies 
# considering flagged users can be found
#
# Changes:
# 2011-06-29 (rja)
# - using Common.pm now
# 2009-11-02 (bkr)
# - initial version
# 2009-11-25 (bkr)
# - split queries

my $dbh = get_slave();

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
		push(@incompleteUsers, $user);
	}
}

if ($#incompleteUsers > 0) {
    print "I have found some users that are flagged as spammer but have unflagged posts.\n";
    print "Please check the spam status of the following users:\n";
    foreach my $user (@incompleteUsers) {
	print "http://www.bibsonomy.org/admin/spam?aclUserInfo=" . $user . "\n";
    }
}


exit 2;

