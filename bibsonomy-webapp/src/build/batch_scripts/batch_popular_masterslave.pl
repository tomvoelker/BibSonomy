#!/usr/bin/perl -w
##############
use DBI();
use strict;
use English;

##################################################################
#
#
# Changes:
#   2008-04-22 (rja)
#   - created master/slave version
#   - changed from "last N rows" to "last N days"
#
##################################################################

if ($#ARGV != 1) {
    print STDERR "usage: $PROGRAM_NAME MASTER_DB SLAVE_DB\n";
    print STDERR "please enter database names as arguments\n";
    exit;
} 


if (am_i_running($ENV{'TMP'}."/$PROGRAM_NAME.pid")) {
  print STDERR "another instance of $PROGRAM_NAME is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

##################################################################
# configuration
##################################################################
my $last_bookmark_days = 20;
my $last_bibtex_days   = 20;
my $last_tag_days      = 10;
my $max_bookmarks      = 100;
my $max_bibtexs        = 100;
my $max_tags           = 100;

# run configuration
# master
my $db_master      = shift @ARGV;
my $db_master_pass = $ENV{'DB_PASS'};
my $db_master_host = "gandalf";
my $db_master_port = 6033;
my $db_master_user = "bibsonomy";
my $db_master_sock = "";
# slave
my $db_slave      = shift @ARGV;
my $db_slave_pass = $ENV{'DB_PASS'};
my $db_slave_host = "odie";
my $db_slave_port = 3306;
my $db_slave_user = "bibsonomy";
my $db_slave_sock = "mysql_socket=/var/mysql/run/mysql.sock";

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
# prepare statements to select rows (slave)
##################################################################
# get last bookmark posts, order them by ctr
my $stm_select_bookmark = $slave->prepare("
SELECT book_url_hash,count(book_url_hash) AS ctr 
  FROM (
    SELECT book_url_hash FROM bookmark 
      WHERE bookmark.group = 0
        AND bookmark.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL $last_bookmark_days DAY)
      ORDER BY date DESC 
  ) AS b 
  GROUP BY book_url_hash 
  ORDER BY ctr DESC 
  LIMIT $max_bookmarks");

# get last bibtex rows, order them by ctr
my $stm_select_bibtex = $slave->prepare("
SELECT simhash1,count(simhash1) AS ctr 
  FROM (
    SELECT simhash1 FROM bibtex 
      WHERE bibtex.group = 0 
        AND bibtex.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL $last_bibtex_days DAY)
      ORDER BY date DESC
    ) AS b 
    GROUP BY simhash1 
    ORDER BY ctr DESC 
    LIMIT $max_bibtexs");

# get last tag rows, order them by ctr
my $stm_select_tags = $slave->prepare("
SELECT tag_lower, COUNT(tag_lower) AS ctr FROM tas 
  WHERE tas.group = 0
    AND tas.date > SUBDATE(CURRENT_TIMESTAMP, INTERVAL $last_tag_days DAY)
  GROUP BY tag_lower
  ORDER BY ctr DESC
  LIMIT $max_tags");

# rows to get from bookmark/bibtex table and to store in temp table
# please add new rows here
my $rows_bookmark = "content_id,book_description,book_extended,book_url_hash,date,user_name,rating";
my $rows_bibtex   = "content_id,journal,volume,chapter,edition,month,day,bookTitle,howPublished,institution,organization,publisher,address,school,series,bibtexKey,date,user_name,url,type,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,simhash0,simhash1,simhash2,simhash3,title,author,editor,year,entrytype,rating";

# get the first post of each resource
my $stm_get_first_bookmark = $slave->prepare("SELECT $rows_bookmark FROM bookmark WHERE book_url_hash = ? AND bookmark.group = 0 ORDER BY date LIMIT 1");
my $stm_get_first_bibtex   = $slave->prepare("SELECT $rows_bibtex   FROM bibtex   WHERE simhash1 = ?      AND bibtex.group = 0   ORDER BY date LIMIT 1");

# build "?" string for insert statements
my $fz_bibtex   = ""; foreach (split (",", $rows_bibtex))   {$fz_bibtex   .= "?,";}
my $fz_bookmark = ""; foreach (split (",", $rows_bookmark)) {$fz_bookmark .= "?,";}

# statements to insert posts in popular tables
my $stm_insert_bookmark = $master->prepare("INSERT INTO temp_bookmark ($rows_bookmark,book_url_ctr,rank) VALUES ($fz_bookmark?,?)");
my $stm_insert_bibtex   = $master->prepare("INSERT INTO temp_bibtex   ($rows_bibtex,ctr,rank)            VALUES ($fz_bibtex?,?)");
my $stm_insert_tags     = $master->prepare("INSERT INTO popular_tags  (tag_lower, tag_count)             VALUES (?,?)");

# delete old values from temp tables
my $stm_delete_bookmark = $master->prepare("DELETE FROM temp_bookmark");
my $stm_delete_bibtex   = $master->prepare("DELETE FROM temp_bibtex");
my $stm_delete_tags     = $master->prepare("DELETE FROM popular_tags");


##################################################################
# execute statements (bookmark)
##################################################################

# get TOP bookmarks
$stm_select_bookmark->execute();
$slave->commit;

# clean table
$stm_delete_bookmark->execute();

# get first occurence of every bookmark
my $rank = 0;
while (my @id = $stm_select_bookmark->fetchrow_array ) {
  $rank++;
  #print "U: $rank ";
  $stm_get_first_bookmark->execute($id[0]);
  #print "e ";
  while (my @row = $stm_get_first_bookmark->fetchrow_array) {
     $stm_insert_bookmark->execute($row[ 0],$row[ 1],$row[ 2],$row[ 3],$row[ 4],$row[5],$row[6],$id[1],$rank);
     #print "i\n";
  }
}
$master->commit;
$stm_select_bookmark->finish();
$stm_get_first_bookmark->finish();

##################################################################
# execute statements (BibTeX)
##################################################################

# get TOP posts
$stm_select_bibtex->execute();
$slave->commit;

# clean table
$stm_delete_bibtex->execute();

# get first occurence of every bookmark
$rank = 0;
while (my @id = $stm_select_bibtex->fetchrow_array ) {
  $rank++;
  #print "B: $rank ";
  $stm_get_first_bibtex->execute($id[0]);
  #print "e ";
  while (my @row = $stm_get_first_bibtex->fetchrow_array) {
     $stm_insert_bibtex->execute($row[ 0],$row[ 1],$row[ 2],$row[ 3],$row[ 4],
                                 $row[ 5],$row[ 6],$row[ 7],$row[ 8],$row[ 9],
                                 $row[10],$row[11],$row[12],$row[13],$row[14],
                                 $row[15],$row[16],$row[17],$row[18],$row[19],
                                 $row[20],$row[21],$row[22],$row[23],$row[24],
                                 $row[25],$row[26],$row[27],$row[28],$row[29],
                                 $row[30],$row[31],$row[32],$row[33],$row[34],
                                 $row[35],$row[36],$row[37],$row[38],
                                 int($id[1]),int($rank));
     #print "i\n";
     #print "i\n";
  }
}
$master->commit;
$stm_select_bibtex->finish();
$stm_get_first_bibtex->finish();


##################################################################
# execute statements (tags)
##################################################################

# get TOP bookmarks
$stm_select_tags->execute();
$slave->commit;

# clean table
$stm_delete_tags->execute();

# get tags
while (my @row = $stm_select_tags->fetchrow_array ) {
    $stm_insert_tags->execute($row[0],$row[1]);
}
$master->commit;
$stm_select_tags->finish();



# disconnect database
$slave->disconnect();
$master->disconnect();



#################################
##################################################################
# subroutines
##################################################################
#################################
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
