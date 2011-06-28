#!/usr/bin/perl
##############
#
# This script generates the counters for the "tagtag" table of BibSonomy
# for a SINGLE TAG only! This means, the global related tag counts for 
# one tag can be recomputed!
# 
# IMPORTANT:
# The script updates only the counts where the given tag is the first
# tag - such that the "related tags" of that tag can be correctly
# computed.
# The current (2009-06-29) reason for that is that we don't have a 
# t2_t1 index on the tagtag table and thus can't efficiently query 
# the rows where the tag appears as second tag.
# 
# This special version reads from the SLAVE and writes to the MASTER.
#
# Expected things:
#   There exists a user "batch" on both databases which has the same
#   password on both databases.
#   On the SLAVE: GRANT SELECT ON bibsonomy.* TO 'batch'@'%' IDENTIFIED BY '';
#   On the MASTER: GRANT INSERT,UPDATE,SELECT ON bibsonomy.* TO 'batch'@'%' IDENTIFIED BY '';
# Command line arguments:
#   database - name of the database (same for SLAVE and MASTER)
#   tag - the tag whose related tags should be updated
# Environment variables:
#   DB_PASS_BATCH
#
# Changes:
#   2011-06-28 (rja):
#   - using Common.pm now
#   2009-06-29 (rja):
#   - copied from batch_tagtag_masterslave.pl 
#   - changed to update only the related tags of a single tag
#   2008-01-23: (rja)
#   - initial version
#
use DBI();
use strict;
use Data::Dumper;
use English;
use Common qw(debug get_slave get_master check_running);
use DBI qw(:utils);
 
if ($#ARGV != 0) {
  print STDERR "please enter the tag name as second argument\n";
  exit;
} 

# don't run twice
check_running();

my $tag = shift @ARGV; 
my %tagtag_ctr_hash = ();

#######################################################
# SLAVE
#######################################################
# connect
my $slave = get_slave(); #, "transaction-isolation" => "READ-UNCOMMITTED"});
# prepare statements
# get get all public tag_names related to $tag ordered by post
my $stm_select_tag_names = $slave->prepare("SELECT t1.tag_name,t1.content_id,t1.group FROM tas t1 JOIN tas t2 USING (content_id) WHERE t2.tag_name = ? ORDER BY t1.content_id ");
$stm_select_tag_names->{"mysql_use_result"} = 1;
my $stm_select_tagtag =$slave->prepare("SELECT t1 collate utf8_bin , t2 collate utf8_bin , ctr_public  FROM tagtag WHERE t1 = ?");
$stm_select_tagtag->{"mysql_use_result"} = 1;


#################################
# execute statements (Tag Names)
#################################
# get posts to compute tag coocurence 
$stm_select_tag_names->execute($tag);

# collect the tags of a post
my $current_content_id=0;
my @tags_of_a_post=();
while (my @tas = $stm_select_tag_names->fetchrow_array ) {
    next unless ($tas[2]==0);

##       print "$tas[0] $tas[1] $tas[2]\n";

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

$stm_select_tagtag->execute($tag);

while (my @tt = $stm_select_tagtag->fetchrow_array ) {

##      print "$tt[0] $tt[1] $tt[2]\n";
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
# prepare
my $stm_update_tag = $master->prepare("UPDATE tagtag SET ctr_public = ? WHERE t1 = ? COLLATE utf8_bin AND t2 = ? COLLATE utf8_bin");
my $stm_instert_tagtag = $master->prepare("INSERT INTO tagtag (t1,t2,ctr_public) VALUES (?, ?, ?) ");


while (my ($key, $value) = each(%tagtag_ctr_hash)) {
    my ($tag1, $tag2) = split(/\|#\|/,$key);
    my $number = $stm_update_tag->execute($value,$tag1,$tag2);
##    my $number;
##    print "$number = stm_update_tag->execute($value,$tag1,$tag2);\n";
    
    if ($number eq "0E0") {
##	print "stm_instert_tagtag->execute($tag1,$tag2,$value);\n";
	$stm_instert_tagtag->execute($tag1,$tag2,$value);
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
	    # only count co-occurrences with given $tag
	    if ($tag1 ne $tag2 and $tag1 eq $tag) {
		$tagtag_ctr_hash{$tag1."|#|".$tag2}++;
	    }
	} 
    }
}
