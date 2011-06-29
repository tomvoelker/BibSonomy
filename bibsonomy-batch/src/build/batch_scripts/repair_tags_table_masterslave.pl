#!/usr/bin/perl
##############
#
# Reads the tas table and updates the tag_ctr in the tags table, if
# necessary.
# 
# Environment variables: see Common.pm
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
# prepare statements
my $stm_select_tags = $slave->prepare("SELECT tag_name FROM tas t");
# get old tag counts
my $stm_select_tagcounts = $slave->prepare("SELECT tag_name, tag_ctr FROM tags t");

# execute statements
$stm_select_tags->execute();
$slave->commit;

# go over all tags
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

debug("read $rowCtr rows from 'tas' table");

# get old tag counts
$stm_select_tagcounts->execute();
$slave->commit;
$rowCtr = 0;
while (my @row = $stm_select_tagcounts->fetchrow_array ) {
    my $tag = $row[0];
    my $ctr = $row[1];
    $old_tagcounts{$tag} = $ctr;
    $rowCtr++;
}
$slave->commit;

debug("read $rowCtr rows from 'tags' table");

$slave->disconnect;

#exit 1;

########################################################
# MASTER
########################################################
# connect to master
my $master = get_master();
# update tag table with the new counts
my $stm_update_tag = $master->prepare("UPDATE tags SET tag_ctr = ? WHERE tag_name = ?");

# update tag table
my $updateCtr = 0;
while (my ($tag, $count) = each %tagcounts) {
    if (!exists $old_tagcounts{$tag} || $count != $old_tagcounts{$tag}) {
#	debug("tag '$tag' must be updated (old = $old_tagcounts{$tag} != $count = new)");
	$stm_update_tag->execute($count,$tag);
	$updateCtr++;
    }
}

debug("updated $updateCtr tags");

$master->commit;

# disconnect database
$master->disconnect();


