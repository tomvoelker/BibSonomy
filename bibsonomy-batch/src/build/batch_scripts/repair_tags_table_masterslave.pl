#!/usr/bin/perl
###############
#
# Reads the tas table and updates the tag_ctr in the tags table.
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

# temp variables
my %tagcounts = ();
my %old_tagcounts = ();

########################################################
# SLAVE 
my $slave = get_slave();
# query tas table to get the current counts
my $stm_select_tags      = $slave->prepare("SELECT tag_name FROM tas");
# query the tags table to get the possibly outdated counts
my $stm_select_tagcounts = $slave->prepare("SELECT tag_name, tag_ctr FROM tags");

# go over all tas
$stm_select_tags->execute();
$slave->commit;
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
$slave->commit;
debug("read $rowCtr rows from the 'tas' table");

# get old tag counts from tags table
$stm_select_tagcounts->execute();
$slave->commit;
$rowCtr = 0;
while (my @row = $stm_select_tagcounts->fetchrow_array ) {
    $old_tagcounts{$row[0]} = $row[1];
    $rowCtr++;
}
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
    if (!exists $old_tagcounts{$tag} || $count != $old_tagcounts{$tag}) {
	$stm_update_tag->execute($count, $tag);
	$updateCtr++;
    }
}
$master->commit;
debug("updated $updateCtr tags in 'tags' table");

$master->disconnect();
