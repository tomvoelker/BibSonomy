#!/usr/bin/perl
##############
use DBI();
use strict;

my $MAX_LAST_ROWS = 1000;
my $MAX_ABS_ROWS = 100;

if ($#ARGV != 0) {
  print "please enter database name as first argument\n";
  exit;
} 

if (am_i_running($ENV{'TMP'}."/batch_tags.pid")) {
  print "another instance of batch_tags.pl is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

my $database = shift @ARGV;
my $password = $ENV{'DB_PASS'};


# connect to database
#my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/mnt/raid1/mysql/mysqld.sock", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/mysql.sock", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
#################################
# prepare statements
#################################
# get get all public tag_names from the tas list
my $stm_select_tag_names = $dbh->prepare("SELECT tag_name FROM tas t WHERE t.group = 0");
# get get old tag counts
my $stm_select_tagcounts_names = $dbh->prepare("SELECT tag_name, tag_ctr_public FROM tags t WHERE tag_ctr_public > 0");

# update tag table with the new counts
my $stm_update_tag = $dbh->prepare("UPDATE tags SET tag_ctr_public = ? where tag_name= ?");
my %tag_hash =();
my %tag_count_hash =();

#################################
# execute statements (Tag Names)
#################################
# get TOP bookmarks
$stm_select_tag_names->execute();
$dbh->commit;

# get first occurence of every bookmark
while (my @tag = $stm_select_tag_names->fetchrow_array ) {

#	print "$tag[0]\n";

    if (exists $tag_hash{$tag[0]}) {
    	$tag_hash{$tag[0]}++;
    } else {
    	$tag_hash{$tag[0]}=1;
    }

}
$dbh->commit;

# get old tag counts to be able to compare it with the new once
$stm_select_tagcounts_names->execute();
$dbh->commit;
while (my @tagcount = $stm_select_tagcounts_names->fetchrow_array ) {
    	$tag_count_hash{$tagcount[0]}=$tagcount[1];
}
$dbh->commit;


for my $key (sort {$a cmp $b} keys %tag_hash) {
  my $count=$tag_hash{$key};
#update only if we have a new counter
   if ((not exists $tag_count_hash{$key}) || $tag_count_hash{$key}!=$count) {
   	   $stm_update_tag->execute($count,$key);
#  print "update1: $key : $tag_hash{$key}\n";
   }
}

# check for all key in old which does not showup in new
for my $key (sort {$a cmp $b} keys %tag_count_hash) {
#update only if we have a new counter
   if (not exists $tag_hash{$key}) {
   	   $stm_update_tag->execute(0,$key);
#  print "update1: $key : $tag_count_hash{$key} to 0\n";
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
