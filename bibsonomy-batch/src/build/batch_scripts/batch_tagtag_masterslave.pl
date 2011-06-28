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
#   MASTER_PASS, MASTER_HOST, etc.
#
# Changes:
#   2011-06-28 (rja)
#   - using Common.pm now
#   2011-06-27 (rja)
#   - all database configuration variables are now read via 
#     environment variables (e.g., MASTER_HOST, MASTER_USER, ...)
#   2008-01-23: (rja)
#   - initial version
#
use DBI();
use strict;
use Data::Dumper;
use English;
use DBI qw(:utils);
use Common qw(debug get_slave get_master check_running);

# don't run twice ...
check_running();


my %tagtag_ctr_hash=();

#######################################################
# SLAVE
#######################################################
# connect
my $slave = get_slave(); #, "transaction-isolation" => "READ-UNCOMMITTED"});
# prepare statements
# get get all public tag_names ordered by post
my $stm_select_tag_names = $slave->prepare("SELECT tag_name,content_id,`group` FROM tas order by content_id ");
$stm_select_tag_names->{"mysql_use_result"} = 1;
my $stm_select_tagtag = $slave->prepare("SELECT t1 collate utf8_bin , t2 collate utf8_bin , ctr_public  FROM tagtag");
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
$slave->commit;



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

$slave->commit;


$slave->disconnect;

######################################################
# MASTER
######################################################
# connect
my $master = get_master();
# set character set to utf8
my $stm_set_char = $master->prepare("SET character_set_connection='utf8'");
$stm_set_char->execute();

# prepare
my $stm_update_tag = $master->prepare("UPDATE tagtag SET ctr_public = ? where t1 = ? collate utf8_bin and t2 = ? collate utf8_bin");
my $stm_instert_tagtag = $master->prepare("INSERT INTO tagtag (t1,t2,ctr_public) values (?, ?, ?) ");


while (my ($key, $value) = each(%tagtag_ctr_hash)) {
my @line = split(/\|#\|/,$key);
#print "Update tagatg:".$key." ".$value."\n";
   my $number = $stm_update_tag->execute($value,$line[0],$line[1]);
   if ($number eq "0E0") {
#print "    and now an insert for the last line\n";
      $stm_instert_tagtag->execute($line[0],$line[1],$value);
   }  
}

$master->commit;

# disconnect database
$master->disconnect();



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

