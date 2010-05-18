#!/usr/bin/perl
##############
#
# This script generates the counters for the "tagtag_similarity" table of BibSonomy.
# This special version reads from the SLAVE and writes to the MASTER.
#
# Expected things:
#   There exists a user "batch" on both databases which has the same
#   password on both databases.
#   On the SLAVE: GRANT SELECT ON bibsonomy.* TO 'batch'@'%' IDENTIFIED BY '';
#   On the MASTER: GRANT INSERT,UPDATE,SELECT ON bibsonomy.* TO 'batch'@'%' IDENTIFIED BY '';
# Command line arguments:
#   database - name of the database (same for SLAVE and MASTER)
# Environment variables:
#   DB_PASS_BATCH
#
# Changes:
#   2008-04-30: (aho)
#   - initial version
#
use DBI();
use strict;
use Data::Dumper;
use English;

use DBI qw(:utils);
 
if ($#ARGV != 0) {
  print "please enter database name as first argument\n";
  exit;
} 

# don't run twice
if (am_i_running($ENV{'TMP'}."/batch_contexttag.pid")) {
  print "another instance of " . $PROGRAM_NAME . " is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

#######################################################
# configuration
#######################################################
my $database = shift @ARGV;     # same db name on all hosts
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases
# fit to slave
my $slave    = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/var/run/mysqld/mysqld.sock";
#my $slave    = "DBI:mysql:database=$database;host=127.0.0.1:3306";
# fit to master
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";
#my $master   = "DBI:mysql:database=$database;host=127.0.0.1:3306";

my $toptags_count=10;

my %tagtag_ctr_hash=();
my %list_of_tags=();
my @t1_array_of_tags=();
my @rel_tags_of_t1=();

#######################################################
# SLAVE
#######################################################
# connect
my $dbh = DBI->connect($slave, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});#, "transaction-isolation" => "READ-UNCOMMITTED"});
# prepare statements
# get all public tag_names ordered by post
my $stm_select_tagtag =$dbh->prepare("SELECT t1 collate utf8_bin , t2 collate utf8_bin , ctr_public  FROM tagtag force index (t1_ctr_public_idx) where ctr_public>0 order by t1 ");
$stm_select_tagtag->{"mysql_use_result"} = 1;
# get top 10000 tags of the system
my $stm_select_toptag =$dbh->prepare("select lower(tag_name),sum(tag_ctr_public) as ctr from tags group by lower(tag_name) having ctr > ?");
$stm_select_toptag->{"mysql_use_result"} = 1;

#######################################
# get the top tags

$stm_select_toptag->execute($toptags_count);

my %toptag = ();

while (my @row = $stm_select_toptag->fetchrow_array ) {
       
        if (exists $toptag{lc($row[0])}) {
  		$toptag{lc($row[0])} += $row[1];
	} else {
		$toptag{lc($row[0])} = $row[1];
	}
}

########################################
#Now we have a new tagtag counter hash table which we will use to compute the similarity
$stm_select_tagtag->execute();

my $sim_count_tags = 10;

my %resources;
my %tags;
my %tags_reverse;
my %res_reverse;

my $tagCount= -1;
my $resourceCount = -1;

my @keys = ();
   $keys[0] = ();
my %values = ();
my @norm = ();


my $resourceId;
my $tid;

my $tag;
my $res;
my $count;


while (my @tt = $stm_select_tagtag->fetchrow_array ) {

	$tag  = lc($tt[0]);
	$res   = lc($tt[1]);
	$count = $tt[2];

#print STDERR "$tt[0] $tt[1] $tt[2]\n";

        if ($count<=0) {next; }

	if (!(exists $toptag{$tag} && exists $toptag{$res})) {next;}

	# store the tags
	if (exists $tags{$tag}) {
		$tid = $tags{$tag};
	}
	else {
		$tagCount++;
		$tags{$tag} = $tagCount;
		$tags_reverse{$tagCount}=$tag;
		$tid = $tagCount;
	}

	# store the resource
	if (exists $resources{$res}) {
		$resourceId = $resources{$res};
	}
	else {
		$resourceCount++;
		$resources{$res} = $resourceCount;
		$res_reverse{$resourceCount}=$res;
		$resourceId = $resourceCount;
	}


	# This is necessary as it could happen that the same pair showup twice due to another order of the database and lc. 
	if (exists $values{$tid}{$resourceId}){
		my $oldcount=$values{$tid}{$resourceId};
		$norm[$tid] -= $oldcount * $oldcount;
		
		$count += $oldcount;
		
		$values{$tid}{$resourceId} = $count ;
		$norm[$tid] += $count * $count;
				
#		print STDERR "Value $tags_reverse{$tid} $res_reverse{$resourceId} count: $count exists!\n";
	} else {
		push (@{$keys[$tid]}, $resourceId);
		$values{$tid}{$resourceId} = $count ;
		$norm[$tid] += $count * $count;
	}
}

$dbh->commit;

# compute norm

for (@norm) {$_=sqrt($_);}

# after this, we should have related tags profile vectors 
# in vector{1}, vector{2}, .. vector{10000} 

################ read all tags from the current tagtag_similarity table and drop all which are not longer in the tagtag table


$dbh->disconnect;


################# compute tagtag sim  values ###############

######################################################
# MASTER
######################################################
# connect
$dbh = DBI->connect($master, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
# prepare
#

my $stm_delete_old_entries =  $dbh->prepare("delete from tagtag_similarity2");

my $stm_instert_contexttag = $dbh->prepare("INSERT INTO tagtag_similarity2 (t1,t2,sim) values (?, ?, ?)"); # ON DUPLICATE KEY UPDATE sim=?");

my $stm_rename_tabs = $dbh->prepare("rename table tagtag_similarity to tagtag_similarity_old,  tagtag_similarity2 to tagtag_similarity,  tagtag_similarity_old to tagtag_similarity2");

# delete all entries of the current table

$stm_delete_old_entries->execute();

$dbh->commit;


my $tid1;
my $tid2;
my $sim;
my $commit_count = 0;


for ($tid1 = 0; $tid1 <= $tagCount; $tid1++) {
	my %sim_to_tid1 = ();
	my $count_curr_sim = 0;
	for ($tid2 = 0; $tid2 <= $tagCount; $tid2++) {
#		print STDERR "work on: ".$tags_reverse{$tid1}."|#|".$tags_reverse{$tid2}."\n";
		if ($tid1 != $tid2) {
			$sim = dot($tid1, $tid2) ;
			if ($sim > 0) {
				$sim /= ( $norm[$tid1] * $norm[$tid2] );
#				print STDERR $tags_reverse{$tid1}."|#|".$tags_reverse{$tid2}."|#| ".$sim." normtid1: $norm[$tid1], normtid2: $norm[$tid2]\n";
#				print $tid1 . "|#|" . $sim . "|#|" . $tid2 . "\n";
				$sim_to_tid1{$tid2}=$sim;
			}
		}
	}
	# computed the similarity for one tag to all other
	# select the top 100 and put it into the database
	
	for ((sort {$sim_to_tid1{$b} <=> $sim_to_tid1{$a}} keys %sim_to_tid1)) {
		$sim = $sim_to_tid1{$_};
#		print $tags_reverse{$tid1}."|#|".$tags_reverse{$_}."|#|".$sim."\n";
		$stm_instert_contexttag->execute($tags_reverse{$tid1},$tags_reverse{$_},$sim); #,$sim);
		$commit_count++;
		if ($commit_count % 1000 == 0) {$dbh->commit;}
		$count_curr_sim++;
		if ($count_curr_sim == $sim_count_tags) {last;}
	}
}

$dbh->commit;

# rename old and new

$stm_rename_tabs->execute();

$dbh->commit;

# disconnect database
$dbh->disconnect();

#################################
# subroutines
#################################
# INPUT: two tags which points to a vector
# OUTPUT: The dot product of this two vectors. 
#

sub dot {
	my $t1 = shift;
	my $t2 = shift;
	my $sum = 0;
	# print "t1 $t1 t2 $t2\n";
#	print STDERR "Res: ";
	foreach (@{$keys[$t1]}) {
		if (exists $values{$t2}{$_}) {
#		print STDERR $res_reverse{$_}.", ";
			$sum += $values{$t1}{$_} * $values{$t2}{$_};
		}
	}
#	print STDERR "\n";
	return $sum;
}	


#################################
# subroutines
#################################
# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
  my $LOCKFILE = shift;
  my $PID ="";
  if (open (FILE, "<$LOCKFILE")) {
    while (<FILE>) {
      $PID = $_;
    }
    close (FILE);
    chomp($PID);

    if (kill(0,$PID)) {
      return 1;
    }
  }
  open (FILE, ">$LOCKFILE");
  print FILE $$;
  close (FILE);
  return 0;
}
