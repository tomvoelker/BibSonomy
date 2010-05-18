#!/usr/bin/perl
##############
#
# This script generates the counters for the "tag" table of BibSonomy.
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
use English;

if ($#ARGV != 0) {
  print "please enter database name as first argument\n";
  exit;
} 

# don't run twice ...
if (am_i_running($ENV{'TMP'}."/batch_tags.pid")) {
  print "another instance of " . $PROGRAM_NAME . " is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

########################################################
# configuration
########################################################
my $database = shift @ARGV;     # same db name on all hosts
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases
# fit to slave
my $slave    = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/var/run/mysqld/mysqld.sock";
# fit to master
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";

# temp variables
my %tag_hash =();
my %tag_count_hash =();
my %tag_user_hash =();
my %tag_user_count_hash = ();

my $min_user_count=10;

########################################################
# SLAVE 
########################################################
# connect to SLAVE
my $dbh = DBI->connect($slave, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
# prepare statements
# get all public tag_names from the tas list
my $stm_select_tag_names = $dbh->prepare("SELECT tag_name,user_name FROM tas t WHERE t.group = 0");
# get old tag counts
my $stm_select_tagcounts_names = $dbh->prepare("SELECT tag_name, tag_ctr_public, show_tag FROM tags t WHERE tag_ctr_public > 0");

# execute statements (Tag Names)
# get TOP bookmarks
$stm_select_tag_names->execute();
$dbh->commit;

# get first occurence of every bookmark
while (my @tag = $stm_select_tag_names->fetchrow_array ) {

    if (exists $tag_hash{$tag[0]}) {
    	$tag_hash{$tag[0]}++;
    } else {
    	$tag_hash{$tag[0]}=1;
    }

    $tag_user_hash{$tag[0]}{$tag[1]}=1;



}
$dbh->commit;

# get old tag counts to be able to compare it with the new ones
$stm_select_tagcounts_names->execute();
$dbh->commit;
while (my @tagcount = $stm_select_tagcounts_names->fetchrow_array ) {
    	$tag_count_hash{$tagcount[0]}=$tagcount[1];
    	$tag_user_count_hash{$tagcount[0]}=$tagcount[2];
}
$dbh->commit;

$dbh->disconnect;

########################################################
# MASTER
########################################################
# connect to master
$dbh = DBI->connect($master, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
# update tag table with the new counts
my $stm_update_tag = $dbh->prepare("UPDATE tags SET tag_ctr_public = ?, show_tag =? WHERE tag_name= ?");

# update tag table
for my $key (sort {$a cmp $b} keys %tag_hash) {
  my $count=$tag_hash{$key};
  # count the number of users per tag
  my $user_count = keys(%{$tag_user_hash{$key}});
  my $show_tag=0;
  if ($user_count>$min_user_count) {$show_tag=1};
  
  # update only if we have a new counter
  if ((not exists $tag_count_hash{$key}) || $tag_count_hash{$key}!=$count ||
       $tag_user_count_hash{$key}!=$show_tag ) {
    $stm_update_tag->execute($count,$show_tag,$key);
  }
}

# check for all key in old which does not showup in new
for my $key (sort {$a cmp $b} keys %tag_count_hash) {
   # update only if we have a new counter
   if (not exists $tag_hash{$key}) {
      $stm_update_tag->execute(0,0,$key);
   }
}


$dbh->commit;

# disconnect database
$dbh->disconnect();



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
