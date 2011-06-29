#!/usr/bin/perl
###############
#
# Reads the tas table and updates the tag_ctr in the tags table.
# Since we do not do this in a transaction, users can add or 
# delete tags after we have counted and before we update the 
# tags.
# 
# This has some consequences:
# 1.) If users add posts, the counts we set in the tags table 
#     would be too low. Low counts are bad, since they might 
#     block deletion of posts (negative count values are not 
#     allowed).
# 2.) If users delete posts, the counts we set would be too 
#     high. 
# 
# Additionally, the script does not
# - delete (or set to zero) counters of tags that don't occur in 
#   the tas table
# 
# Environment variables: 
#   see Common.pm
#
# Changes:
#   2011-06-29 (rja)
#   - initial version
#
use DBI();
use strict;
use English;
use Common qw(debug set_debug get_slave get_master check_running);

# don't run twice ...
check_running();

set_debug(1);

# the tag -> count mapping from the tas table
my %tagcounts = ();

debug("updating of tag counters started");

########################################################
# SLAVE 
my $slave = get_slave();
# query tas table to get the current counts
my $stm_select_tags      = $slave->prepare("SELECT tag_name FROM tas");
# query the tags table to get the possibly outdated counts
my $stm_select_tagcounts = $slave->prepare("SELECT tag_name, tag_ctr FROM tags");

# go over all tas
$stm_select_tags->execute();
my $rowCtr = 0;
while (my @row = $stm_select_tags->fetchrow_array ) {
    my $tag = $row[0];
    # count for each tag in how many posts it occurs
    if (exists $tagcounts{$tag}) {
    	$tagcounts{$tag}++;
    } else {
    	$tagcounts{$tag} = 1;
    }
    $rowCtr++;
}
debug("read $rowCtr rows from the 'tas' table");

# get old tag counts from tags table
$stm_select_tagcounts->execute();
$rowCtr = 0;
while (my @row = $stm_select_tagcounts->fetchrow_array ) {
    my $tag = $row[0];
    my $count = $row[1];
    # check, if the tag exists in the tas table
    if (exists $tagcounts{$tag}) {
	# no update necessary if the count is correct
	delete $tagcounts{$tag} if ($tagcounts{$tag} == $count);
    } else {
	# tag does not occur in tas table -> set count to 0
	$tagcounts{$tag} = 0 if ($count > 0);
    }
    $rowCtr++;
}
# we commit here such that we get both tables in one transaction!
$slave->commit;
debug("read $rowCtr rows from the 'tags' table");

$slave->disconnect;

########################################################
# MASTER
my $master = get_master();
# update the tag table with the new counts
my $stm_update_tag = $master->prepare("UPDATE tags SET tag_ctr = ? WHERE tag_name = ?");

# update the tag table
my $updateCtr = 0;
while (my ($tag, $count) = each %tagcounts) {
    $stm_update_tag->execute($count, $tag);
    $updateCtr++;
}
$master->commit;
debug("updated $updateCtr tags in 'tags' table");

$master->disconnect();
