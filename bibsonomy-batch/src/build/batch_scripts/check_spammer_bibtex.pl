#!/usr/bin/perl -w 

use strict;
use DBI();

# Checks if already classified spammers have posted  bibtex entries after their classification
# A E-Mail is sent with the users, which need to be manually checked again
# - initial version
# 2010-05-21 (bkr)

########################################################
## configuration
#########################################################
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases

# establish connection to bibsonomy database
my $dbh = DBI->connect("DBI:mysql:database=bibsonomy;host=localhost:6033;mysql_socket=/var/run/mysqld/mysqld.sock", $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

########################################################
## spammer check
#########################################################


#print STDERR "Selecting spammers\n"; 
# select spammers
#my $query = "SELECT p.user_name, p.prediction, algorithm, p.updated_at, date, count(b.user_name) FROM bibtex b JOIN prediction p ON (b.user_name = p.user_name) JOIN user u ON (u.user_name = p.user_name AND u.updated_at = p.updated_at) WHERE algorithm != 'self_deleted' AND prediction > 0 AND date >= NOW() - INTERVAL 31 DAY AND date > p.updated_at GROUP BY user_name ORDER BY date;";

# select all users and necessary information which
# have posted BibTex in the last 24 hours 
# after their last classification as spammer, or sure / unsure user
 
my $query = "SELECT p3.user_name, p3.prediction, p3.algorithm, p3.updated_at, date FROM bibtex b JOIN (select p1.user_name, p1.algorithm, p1.prediction, p1.timestamp, p1.updated_at from prediction p1 JOIN (select user_name, max(timestamp) as timestamp from prediction group by user_name) AS p2 ON p1.user_name = p2.user_name AND p1.timestamp = p2.timestamp) AS p3 ON (p3.user_name = b.user_name) JOIN user u ON (p3.user_name = u.user_name AND p3.updated_at = u.updated_at) WHERE p3.algorithm != 'self_deleted' AND p3.prediction > 0 AND date >= NOW() - INTERVAL 31 DAY AND date > p3.updated_at GROUP BY user_name ORDER BY date;";

my $stm1 = $dbh->prepare($query);
$stm1->execute();

my %users = ();
while(my $row=$stm1->fetchrow_hashref) {
	#print STDERR "$row->{'user_name'}\n";
	if (defined $row->{"user_name"}) {
		# only interested in the classifier decisions
		if ($row->{"algorithm"} ne "admin"){
				#print STDERR "$row->{'algorithm'}\n";
				my $pred = "";
				if ($row->{"prediction"} == 1){
					$pred = "spammer";	
				}elsif ($row->{"prediction"} == 2){
                                        $pred = "nonspammer unsure";
                                }elsif ($row->{"prediction"} == 3){
                                        $pred = "spammer unsure";
                                }
				$users{$row->{"user_name"}}=$pred."\t".$row->{"date"}."\t".$row->{"updated_at"};
		}
  	}
}
$dbh->disconnect();

# evaluate result
my $bibtexUsers = "Username\tPrediction\tLastBibTexDate\tLastUpdate\n";; 
foreach my $user (keys %users){
	$bibtexUsers = $bibtexUsers."\n".$user."\t".$users{$user}."\n";
	$bibtexUsers = $bibtexUsers."http://www.bibsonomy.org/admin/spam?aclUserInfo=".$user."\n";
}

if ($bibtexUsers !~ /LastUpdate$/){
	print "Please check the spam status of the following bibtex users:\n$bibtexUsers\n"; 
}else{
	#keine mail mehr senden
	#print "User spam status in database OK\n"; 
}


exit 2;

