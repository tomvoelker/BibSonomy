#!/usr/bin/perl
##############
#
# This script generates the counters for the "tagtag" table of BibSonomy.
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
#   2008-01-23: (rja)
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
if (am_i_running($ENV{'TMP'}."/batch_tagtag.pid")) {
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
# fit to master
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";

my %tagtag_ctr_hash=();

#######################################################
# SLAVE
#######################################################
# connect
my $dbh = DBI->connect($slave, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});#, "transaction-isolation" => "READ-UNCOMMITTED"});
# prepare statements
# get get all public tag_names ordered by post
my $stm_select_tag_names = $dbh->prepare("SELECT tag_name,content_id,`group` FROM tas order by content_id ");
$stm_select_tag_names->{"mysql_use_result"} = 1;
my $stm_select_tagtag =$dbh->prepare("SELECT t1 collate utf8_bin , t2 collate utf8_bin , ctr_public  FROM tagtag");
$stm_select_tagtag->{"mysql_use_result"} = 1;


#################################
# execute statements (Tag Names)
#################################
# get post to compute tag coocurence 
$stm_select_tag_names->execute();

# collect the tags of a post
my $current_content_id=0;
my @tags_of_a_post=();
while (my @tas = $stm_select_tag_names->fetchrow_array ) {
    next unless ($tas[2]==0);

    #   print "$tas[0] $tas[1] $tas[2]\n";

    if ($current_content_id!=$tas[1]) {
        # start with a new post
        # change counter for the tags of this post
        update_hash();
        $current_content_id=$tas[1];
        @tags_of_a_post=();
        push(@tags_of_a_post,$tas[0]);  
    } else {
    # collect all tags of a post
        push(@tags_of_a_post,$tas[0]);  
    }
}

#update the tagtag hash counters for the last post
update_hash();

########################################
#Now we have a new tagtag counter hash table which we have to compare with the old one
$dbh->commit;



$stm_select_tagtag->execute();

while (my @tt = $stm_select_tagtag->fetchrow_array ) {

#      print "$tt[0] $tt[1] $tt[2]\n";
      # check for a new tagtag counter value in the tas 
      if (exists $tagtag_ctr_hash{$tt[0]."|#|".$tt[1]}) {
          if ($tagtag_ctr_hash{$tt[0]."|#|".$tt[1]} == $tt[2]) {
            # no change of the value us needed
            # remove it from the hash
            delete $tagtag_ctr_hash{$tt[0]."|#|".$tt[1]};
               }
       } else { 
         # all remaining hashes in $tagtag_ctr_hash are new or have new value
         # What happens with values which are not in $tagtag_ctr_hash but have currently a tagtag value >0
         if ($tt[2]>0) {
        $tagtag_ctr_hash{$tt[0]."|#|".$tt[1]} = 0;
         }
       }
}

$dbh->commit;


$dbh->disconnect;

######################################################
# MASTER
######################################################
# connect
$dbh = DBI->connect($master, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
# prepare
my $stm_update_tag = $dbh->prepare("UPDATE tagtag SET ctr_public = ? where t1 = ? collate utf8_bin and t2 = ? collate utf8_bin");
my $stm_instert_tagtag = $dbh->prepare("INSERT INTO tagtag (t1,t2,ctr_public) values (?, ?, ?) ");


while (my ($key, $value) = each(%tagtag_ctr_hash)) {
my @line = split(/\|#\|/,$key);
   my $number = $stm_update_tag->execute($value,$line[0],$line[1]);
   if ($number eq "0E0") {
      $stm_instert_tagtag->execute($line[0],$line[1],$value);
   }  
}

$dbh->commit;

# disconnect database
$dbh->disconnect();



#########################################################
# subroutines
#########################################################
sub update_hash() {

            foreach my $tag1 (@tags_of_a_post) {
                foreach my $tag2 (@tags_of_a_post) {
                    if ($tag1 ne $tag2) {
                        $tagtag_ctr_hash{$tag1."|#|".$tag2}++;
                    }
                } 
            }


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
