#!/usr/bin/perl -w
##############
use DBI();
use strict;
use English;

##################################################################
# This script fills the popular tables for bibtexs, bookmarks, and
# tags. For each of the given number of days the top items are
# extracted.
#
# Changes:
#   2008-04-22 (rja)
#   - created master/slave version
#   - changed from "last N rows" to "last N days"
#   - added possibility to compute popularity for different
#     number of days
#   2009-02-16 (dbe)
#   - added list of tags to be excluded from popular tags table
#
##################################################################

if ($#ARGV != 1) {
    print STDERR "usage: MASTER_DB SLAVE_DB\n";
    print STDERR "please enter database names as arguments\n";
    exit;
} 


if (am_i_running($ENV{'TMP'}."/batch_popular_masterslave.pid")) {
  print STDERR "another instance of $PROGRAM_NAME is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

##################################################################
# configuration
##################################################################
# these are arrays - enter as many days as blocks you like
my @last_bookmark_days = (7, 30, 120);
my @last_bibtex_days   = (7, 30, 120);
my @last_tag_days      = (10);
# max number of posts for each day block
my $max_bookmarks      = 100;
my $max_bibtexs        = 100;
my $max_tags           = 100;
# tags to be excluded from popular tags (lower case)
my @tags_to_exclude=('imported', 'jabref:nokeywordassigned');

my $DEBUG = 0; # 1 = on, 0 = off

# run configuration
# master
my $db_master      = shift @ARGV;
my $db_master_pass = $ENV{'DB_PASS'};
my $db_master_host = "gandalf";
my $db_master_port = 6033;
my $db_master_user = "batch";
my $db_master_sock = "";
# slave
my $db_slave      = shift @ARGV;
my $db_slave_pass = $ENV{'DB_PASS'};
my $db_slave_host = "localhost";
my $db_slave_port = 3306;
my $db_slave_user = "batch";
my $db_slave_sock = "mysql_socket=/var/run/mysqld/mysqld.sock";

# test configuration
# # master
# my $db_master      = shift @ARGV;
# my $db_master_pass = $ENV{'DB_PASS'};
# my $db_master_host = "gromit";
# my $db_master_port = 3306;
# my $db_master_user = "bibsonomy";
# my $db_master_sock = "";
# # slave
# my $db_slave      = shift @ARGV;
# my $db_slave_pass = $ENV{'DB_PASS'};
# my $db_slave_host = "gromit";
# my $db_slave_port = 3306;
# my $db_slave_user = "bibsonomy";
# my $db_slave_sock = "";

##################################################################
# connect to slave
my $slave  = DBI->connect("DBI:mysql:database=$db_slave;host=$db_slave_host:$db_slave_port;$db_slave_sock", 
			  $db_slave_user, $db_slave_pass, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1, "mysql_auto_reconnect" => 1});

# connect to master
my $master = DBI->connect("DBI:mysql:database=$db_master;host=$db_master_host:$db_master_port;$db_master_sock", 
			  $db_master_user, $db_master_pass, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1, "mysql_auto_reconnect" => 1});


##################################################################
# prepare statements (bookmark)
##################################################################

# get last bookmark posts, order them by ctr
my $stm_select_bookmark = $slave->prepare("
SELECT book_url_hash,count(book_url_hash) AS ctr 
  FROM (
    SELECT book_url_hash, user_name FROM bookmark 
      WHERE bookmark.group = 0
        AND bookmark.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL ? DAY)
      GROUP BY book_url_hash, user_name
    ) AS b 
  GROUP BY book_url_hash
  ORDER BY ctr DESC 
  LIMIT $max_bookmarks");

# rows to get/write
my $rows_bookmark = "content_id,book_description,book_extended,book_url_hash,date,user_name,rating";
# build "?" string for insert statements
my $fz_bookmark = ""; foreach (split (",", $rows_bookmark)) {$fz_bookmark .= "?,";}

# get the first post of each resource
my $stm_get_first_bookmark = $slave->prepare("SELECT $rows_bookmark FROM bookmark WHERE book_url_hash = ? AND bookmark.group = 0 ORDER BY date LIMIT 1");

# insert posts in popular table
my $stm_insert_bookmark = $master->prepare("INSERT INTO temp_bookmark ($rows_bookmark,book_url_ctr,rank,popular_days) VALUES ($fz_bookmark?,?,?)");

# delete old values from temp table
my $stm_delete_bookmark = $master->prepare("DELETE FROM temp_bookmark");

##########################################
# execute statements (bookmark)
##########################################

# clean table
$stm_delete_bookmark->execute();

foreach my $days (@last_bookmark_days) {

    debug("bookmark, $days days: ");

    # get TOP bookmarks
    $stm_select_bookmark->execute($days);
    $slave->commit;

    # get first occurence of every bookmark
    my $rank = 0;
    while (my @id = $stm_select_bookmark->fetchrow_array ) {
	$rank++;
	debug(".");
	$stm_get_first_bookmark->execute($id[0]);
	while (my @row = $stm_get_first_bookmark->fetchrow_array) {
	    $stm_insert_bookmark->execute($row[ 0],$row[ 1],$row[ 2],$row[ 3],$row[ 4],$row[5],$row[6],$id[1],$rank, $days);
	}
    }
    debug("\n");
}
$master->commit;
$stm_select_bookmark->finish();
$stm_get_first_bookmark->finish();



##################################################################
# prepare statements (BibTeX)
##################################################################

# get last bibtex rows, order them by ctr
my $stm_select_bibtex = $slave->prepare("
SELECT simhash1,count(simhash1) AS ctr 
  FROM (
    SELECT simhash1, user_name FROM bibtex 
      WHERE bibtex.group = 0 
        AND bibtex.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL ? DAY)
      GROUP BY simhash1, user_name
    ) AS b 
    GROUP BY simhash1 
    ORDER BY ctr DESC 
    LIMIT $max_bibtexs");

# rows to get/write
my $rows_bibtex   = "content_id,journal,volume,chapter,edition,month,day,bookTitle,howPublished,institution,organization,publisher,address,school,series,bibtexKey,date,user_name,url,type,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,simhash0,simhash1,simhash2,simhash3,title,author,editor,year,entrytype,rating";
# build "?" string for insert statements
my $fz_bibtex   = ""; foreach (split (",", $rows_bibtex))   {$fz_bibtex   .= "?,";}

# get the first post of each resource
my $stm_get_first_bibtex   = $slave->prepare("SELECT $rows_bibtex FROM bibtex   WHERE simhash1 = ? AND bibtex.group = 0 ORDER BY date LIMIT 1");

# statements to insert posts in popular table
my $stm_insert_bibtex   = $master->prepare("INSERT INTO temp_bibtex ($rows_bibtex,ctr,rank,popular_days) VALUES ($fz_bibtex?,?,?)");

# delete old values from temp tables
my $stm_delete_bibtex   = $master->prepare("DELETE FROM temp_bibtex");


##########################################
# execute statements (BibTeX)
##########################################


# clean table
$stm_delete_bibtex->execute();

foreach my $days (@last_bibtex_days) {

    debug("bibtex, $days days: ");

    # get TOP posts
    $stm_select_bibtex->execute($days);
    $slave->commit;

    # get first occurence of every bookmark
    my $rank = 0;
    while (my @id = $stm_select_bibtex->fetchrow_array ) {
	$rank++;
	debug(".");
	$stm_get_first_bibtex->execute($id[0]);
	while (my @row = $stm_get_first_bibtex->fetchrow_array) {
	    $stm_insert_bibtex->execute($row[ 0],$row[ 1],$row[ 2],$row[ 3],$row[ 4],
					$row[ 5],$row[ 6],$row[ 7],$row[ 8],$row[ 9],
					$row[10],$row[11],$row[12],$row[13],$row[14],
					$row[15],$row[16],$row[17],$row[18],$row[19],
					$row[20],$row[21],$row[22],$row[23],$row[24],
					$row[25],$row[26],$row[27],$row[28],$row[29],
					$row[30],$row[31],$row[32],$row[33],$row[34],
					$row[35],$row[36],$row[37],$row[38],
					int($id[1]),int($rank), $days);
	}
    }
    debug ("\n");
}
$master->commit;
$stm_select_bibtex->finish();
$stm_get_first_bibtex->finish();




##################################################################
# prepare statements (tags)
##################################################################

# insert tags
my $stm_insert_tags     = $master->prepare("INSERT INTO popular_tags  (tag_lower, tag_ctr, content_type, popular_days) VALUES (?,?,?,?)");

# delete old values from temp tables
my $stm_delete_tags     = $master->prepare("DELETE FROM popular_tags");

##########################################
# execute statements (tags)
# for each content type we calculate the
# most popular tags
##########################################

# clean table
$stm_delete_tags->execute();

# all, bookmark, bibtex
my @content_types = (0,1,2);

# loop over different content types
foreach my $content_type (@content_types) {
    debug("tags, content_type = $content_type\n");
    my $condition = "";
    if ($content_type == 0) {
	# all content types
	$condition = "";
    } else {
	# bookmark, bibtex, ...
	$condition = " AND content_type = $content_type ";
    }

    # exclude certain tags
    foreach my $exclude_tag (@tags_to_exclude) {
	$condition .= " AND tag_lower != '$exclude_tag' ";
    }

    # get last tag rows, order them by ctr
    my $stm_select_tags = $slave->prepare("
    SELECT tag_lower, COUNT(tag_lower) AS ctr 
      FROM (
        SELECT tag_lower, user_name FROM tas 
          WHERE tas.group = 0 " 
          . $condition .
          " AND tas.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL ? DAY)
          GROUP BY tag_lower, user_name
        ) AS b
      GROUP BY tag_lower
      ORDER BY ctr DESC
      LIMIT $max_tags");

    # insert rows
    foreach my $days (@last_tag_days) {
	debug ("$days days: ");
	
	# get TOP bookmarks
	$stm_select_tags->execute($days);
	$slave->commit;
	
	# get tags
	while (my @row = $stm_select_tags->fetchrow_array ) {
	    debug (".");
	    $stm_insert_tags->execute($row[0],$row[1], $content_type, $days);
	}
    }
    
    debug ("\n");

    $master->commit;
    $stm_select_tags->finish();
}




##################################################################
# clean up
##################################################################

# disconnect database
$slave->disconnect();
$master->disconnect();



##################################################################
# subroutines
##################################################################
# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
  my $LOCKFILE = shift;
  my $PIDD;

  if (open (FILE, "<$LOCKFILE")) {
    while (<FILE>) {
      $PIDD = $_;
    }
    close (FILE);
    chomp($PIDD);

    if (kill(0,$PIDD)) {
      return 1;
    }
  }
  open (FILE, ">$LOCKFILE");
  print FILE $$;
  close (FILE);
  return 0;
}

# logs statements if debugging is enabled
sub debug {
    my $msg = shift;
    if ($DEBUG) {
	print STDERR $msg;
    }
}
