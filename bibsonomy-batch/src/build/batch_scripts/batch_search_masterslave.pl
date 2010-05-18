#!/usr/bin/perl
#
#
# This script generates the "search" table of BibSonomy.
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
#   2008-02-28: (sts)
#   - split search table 
#   2008-03-07: (dbe)
#   - removed spammers
#   - added preprocessing of URLs
#   2008-04-09: (dbe)
#   - extended preprocessing of URLs
#   2009-01-05: (rja)
#   - Added functionality to add missing posts to search table,
#     i.e., to look for content_ids in the bookmark/bibtex table
#     which are "old" but not contained in the respective search
#     tables. This functionality is activated when the script is 
#     called as "add_missing_posts_to_search_table.pl".
#
use DBI();
use TeX::Encode;
use Encode;
use English;
use strict;

my $CONTENT_TYPE_BOOKMARK = 1;
my $CONTENT_TYPE_BIBTEX   = 2;

# set to 1 to enable informative output - should be off when running as cron, because 
# a mail is sent in case of script output
my $enable_output = 0;

# if the script is called as "add_missing_posts_to_search_table.pl", it
# does special (expensive) queries to find "old" content_ids which are missing
# in the search tables
my $add_missing_posts = $PROGRAM_NAME eq "add_missing_posts_to_search_table.pl";
if ($add_missing_posts) {
  print "adding missing posts to search table; enabling output\n";
  $enable_output = 1;
}


if ($#ARGV != 2) {
    print "please enter database name as first, bookmark table name as second and bibtex table name as last argument\n";
    exit;
}

# don't run twice
if (am_i_running($ENV{'TMP'}."/batch_search.pid")) {
  print "another instance of " . $PROGRAM_NAME . " is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}
########################################################
# configuration
########################################################
my $database       = shift @ARGV;     # same db name on all hosts
my $bookmark_table = shift @ARGV;     # bookmark table name 
my $bibtex_table   = shift @ARGV;     # bibtex table name 

my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases

# master and slave databases 
my $slave    = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/var/run/mysqld/mysqld.sock";
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";
#my $master  = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/mnt/raid-db/mysql/run/mysqld.sock"; # for the local case

# temporary variables
my @row;                 			# temporary rows from queries
my $ctr = 0;

my @bookmark_data = ();          	# temporary data for search table
my @bibtex_data   = ();            	# temporary data for search table

my $last_bookmark_content_id = 0; 	# default for populating table the first time
my $last_bibtex_content_id   = 0;   # default for populating table the first time

my %bookmark_tag_hash;          	# stores tags of new bookmarks
my %bibtex_tag_hash;            	# stores tags of new bibtex


########################################################
# SLAVE
#########################################################

print "\n\n#################################\nStart script...\n#################################\n\n" if ($enable_output == 1);

print "connect to db-slave and prepare statements...\n" if ($enable_output == 1);
# connect to database
my $dbh = DBI->connect($slave, $user, $password, {RaiseError => 1, "mysql_auto_reconnect" => 1, "mysql_enable_utf8" => 1});

### prepare statements ###

# get last bookmark content_id from bookmark search table
my $stm_select_bookmark_content_id = $dbh->prepare ("SELECT content_id FROM $bookmark_table ORDER BY content_id DESC LIMIT 1");

# get last bibtex content_id from bibtex search table
my $stm_select_bibtex_content_id = $dbh->prepare ("SELECT content_id FROM $bibtex_table ORDER BY content_id DESC LIMIT 1");

# gets all relevant bookmarks from bookmark table
my $stm_select_book;
# the rows for bookmarks to query
my $bookmark_rows = "b.content_id, b.group, b.date, b.user_name, b.book_description, b.book_extended, u.book_url";

if ($add_missing_posts) {
  # we ask for all bookmarks which have no corresponding content_id in the search table 
  # and whose content_id is smaller than the last content_id of the search table - to
  # only get "old" bookmarks, i.e., to not to interfere with the "normal" search table 
  # updates
  $stm_select_book = $dbh->prepare ("
    SELECT $bookmark_rows 
    FROM bookmark b 
      LEFT JOIN $bookmark_table USING (content_id)
      JOIN urls u USING (book_url_hash)
    WHERE b.content_id < ? 
      AND b.group >= 0
      AND $bookmark_table.content_id IS NULL
  ");
} else {
  # this is the regular query 
  $stm_select_book = $dbh->prepare ("
    SELECT $bookmark_rows
    FROM bookmark b JOIN urls u USING (book_url_hash)
    WHERE b.content_id > ? 
      AND b.group >= 0
  ");
}

# gets all relevant bibtex from bibtex table
my $stm_select_bib;
# the rows for bibtex to query
my $bibtex_rows = 
        "b.content_id,   b.group,       b.date,         b.user_name,      b.author, 
         b.editor,       b.title,       b.journal,      b.booktitle,      b.volume, 
         b.number,       b.chapter,     b.edition,      b.month,          b.day, 
         b.howPublished, b.institution, b.organization, b.publisher,      b.address, 
         b.school,       b.series,      b.bibtexKey,    b.url,            b.type, 
         b.description,  b.annote,      b.note,         b.pages,          b.bKey, 
         b.number,       b.crossref,    b.misc,         b.bibtexAbstract, b.year";

if ($add_missing_posts) {
  # see comment for bookmarks
  $stm_select_bib = $dbh->prepare ("
    SELECT $bibtex_rows
    FROM bibtex b
      LEFT JOIN $bibtex_table USING (content_id)
    WHERE b.content_id < ? 
      AND b.group >= 0
      AND $bibtex_table.content_id IS NULL  
  ");
} else {
  $stm_select_bib  = $dbh->prepare ("
    SELECT $bibtex_rows
    FROM bibtex b
    WHERE b.content_id > ? 
      AND b.group >= 0
  ");
}

# gets all relevant data from tas table
my $stm_select_tas;

if ($add_missing_posts) {
  # only missing posts, see comment above

  # for bookmarks only, bibtex below
  $stm_select_tas = $dbh->prepare("
    SELECT t.content_id, t.tag_name
    FROM tas t
      LEFT JOIN $bookmark_table USING (content_id)
    WHERE t.content_type = ?
      AND t.content_id < ? 
      AND t.group >= 0
      AND $bookmark_table.content_id IS NULL
  ");
} else {
  $stm_select_tas = $dbh->prepare("
    SELECT content_id, tag_name
    FROM tas
    WHERE content_type = ? 
      AND content_id > ? 
      AND `group` >= 0
  ");
}




#################################
# get last content_ids
#################################

# bookmark
$stm_select_bookmark_content_id->execute();
if (@row = $stm_select_bookmark_content_id->fetchrow_array) {
    $last_bookmark_content_id = $row[0];
} 
$stm_select_bookmark_content_id->finish();

# bibtex
$stm_select_bibtex_content_id->execute();
if (@row = $stm_select_bibtex_content_id->fetchrow_array) {
    $last_bibtex_content_id = $row[0];
} 
$stm_select_bibtex_content_id->finish();


#################################
# retrieve tas rows
#################################

# bookmarks
$stm_select_tas->execute($CONTENT_TYPE_BOOKMARK, $last_bookmark_content_id);
while (@row = $stm_select_tas->fetchrow_array) {
  $bookmark_tag_hash{$row[0]} .= " $row[1]";
}
$stm_select_tas->finish();



# since we join the corresponding search tables to look for NULL values,
# we need to alter the query depending on the content_type ... 
if ($add_missing_posts) {
  # only missing posts, see comment above
  $stm_select_tas = $dbh->prepare("
    SELECT t.content_id, t.tag_name
    FROM tas t
      LEFT JOIN $bibtex_table USING (content_id)
    WHERE t.content_type = ?
      AND t.content_id < ? 
      AND t.group >= 0
      AND $bibtex_table.content_id IS NULL
  ");
}

# bibtex
$stm_select_tas->execute($CONTENT_TYPE_BIBTEX, $last_bibtex_content_id);
while (@row = $stm_select_tas->fetchrow_array) {
  $bibtex_tag_hash{$row[0]} .= " $row[1]";
}
$stm_select_tas->finish();




#################################
# retrieve all bookmark rows
#################################
$stm_select_book->execute($last_bookmark_content_id);
while (@row = $stm_select_book->fetchrow_array) {
    $ctr++;
    
    # special handling for fields containg URLs
    $row[6] = clean_url($row[6]); # book_url field
    $row[5] = clean_url($row[5]); # book_extended field
    
    my $content = clean_string("$row[4] $row[5] $row[6] $bookmark_tag_hash{$row[0]}");
      
    # save data
    my @array = ($row[0], $content, $row[1], $row[2], $row[3]); 
    push (@bookmark_data, \@array);
    
    print "read bookmark $ctr\n" if ($ctr % 1000 == 0 and $enable_output == 1);
}
$stm_select_book->finish();


#################################
# retrieve bibtex rows
#################################
$stm_select_bib->execute($last_bibtex_content_id);
while (@row = $stm_select_bib->fetchrow_array) {
    $ctr++;
    my $content = "";
    
    # special handling for fields containing URLs
    $row[23] = clean_url($row[23]); # url field
    $row[32] = clean_url($row[32]); # misc field
    
    for (my $i=4; $i<35; $i++) {
	  $content = $content." ".$row[$i];
    }
    $content = clean_bibtex_string("$content $bibtex_tag_hash{$row[0]}");

    # save data
    my @array = ($row[0], $content, "$row[4] $row[5]", $row[1], $row[2], $row[3]); 
    push (@bibtex_data, \@array);
    
    print "read bibtex $ctr\n" if ($ctr % 1000 == 0 and $enable_output == 1);
}
$stm_select_bib->finish();


########################################################
# MASTER
########################################################
print "\nconnect to master-db and fill search tables" if ($enable_output == 1);

# connect to database
my $dbh_master = DBI->connect($master, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

# prepare statements
my $stm_disable_bookmark_keys = $dbh_master->prepare ("ALTER TABLE $bookmark_table DISABLE KEYS");
my $stm_disable_bibtex_keys = $dbh_master->prepare ("ALTER TABLE $bibtex_table DISABLE KEYS");

my $stm_enable_bookmark_keys = $dbh_master->prepare ("ALTER TABLE $bookmark_table ENABLE KEYS");
my $stm_enable_bibtex_keys = $dbh_master->prepare ("ALTER TABLE $bibtex_table ENABLE KEYS");

# insert bookmarks into bookmark search table
my $stm_bookmark_insert = $dbh_master->prepare ("INSERT INTO $bookmark_table (content_id, content, `group`, `date`, user_name) VALUES (?,?,?,?,?)");

# insert bibtex into bibtex search table
my $stm_bibtex_insert = $dbh_master->prepare ("INSERT INTO $bibtex_table (content_id, content, author, `group`, `date`, user_name) VALUES (?,?,?,?,?,?)");


#################################
# insert rows
#################################

# enable this, when building table from scratch
if ($bookmark_table eq "search2_bookmark" && $bibtex_table eq "search2_bibtex") 
{
	print "\nDisable indexes\n" if ($enable_output == 1);
	$stm_disable_bookmark_keys->execute();  
	$stm_disable_bibtex_keys->execute(); 
}

# insert bookmarks
$ctr = 0;
while ($#bookmark_data > -1) {
    my @feld = @{pop(@bookmark_data)};

    $stm_bookmark_insert->execute($feld[0], $feld[1], $feld[2], $feld[3], $feld[4]);
    $ctr++;
    if ($ctr % 1000 == 0) {
	print "write bookmark $ctr\n" if ($enable_output == 1);
	$dbh_master->commit();
    }
}

# insert bibtex
$ctr = 0;
while ($#bibtex_data > -1) {
    my @feld = @{pop(@bibtex_data)};

    $stm_bibtex_insert->execute($feld[0], $feld[1], $feld[2], $feld[3], $feld[4], $feld[5]);
    $ctr++;
    if ($ctr % 1000 == 0) {
	print "write bibtex $ctr\n" if ($enable_output == 1);
	$dbh_master->commit();
    }
}

$dbh_master->commit;

# enable this, when building table from scratch
$stm_enable_bookmark_keys->execute() if ($bookmark_table eq "search2_bookmark");
$stm_enable_bibtex_keys->execute() if ($bibtex_table eq "search2_bibtex");
  
# select bookmark content ids which can be deleted
my $stm_bookmark_get = $dbh->prepare ("SELECT s.content_id FROM $bookmark_table s LEFT JOIN tas b USING (content_id) WHERE b.content_id IS NULL;");

# select bibtex content ids which can be deleted
my $stm_bibtex_get = $dbh->prepare ("SELECT s.content_id FROM $bibtex_table s LEFT JOIN tas b USING (content_id) WHERE b.content_id IS NULL;");

# delete bookmark content_ids
my $stm_bookmark_del = $dbh_master->prepare ("DELETE FROM $bookmark_table WHERE content_id = ?");

# delete bibtex content_ids
my $stm_bibtex_del = $dbh_master->prepare ("DELETE FROM $bibtex_table WHERE content_id = ?");


#################################
# delete old rows
#################################

# delete bookmark entries which do not exist any longer
$stm_bookmark_get->execute();
while (@row = $stm_bookmark_get->fetchrow_array) {
    $stm_bookmark_del->execute($row[0]);
}

# delete bibtex entries which do not exist any longer
$stm_bibtex_get->execute();
while (@row = $stm_bibtex_get->fetchrow_array) {
    $stm_bibtex_del->execute($row[0]);
}

$dbh_master->commit;
$stm_bookmark_get->finish;
$stm_bibtex_get->finish;
$stm_bookmark_del->finish;
$stm_bibtex_del->finish;


#################################
# disconnect database
#################################

$dbh->disconnect();
$dbh_master->disconnect();


#################################
# subroutines
#################################

sub clean_string {
    my $s = shift;
    $s =~ s/[\!\"\§\$\%\&\/\}\{\(\)\=\?\\\[\]\.\,\;\:\-\_\+\*\~\#\|\>\<\'\`\^\°\²\~\s\@\€\µ]+/ /g;
    return $s;
}
sub clean_bibtex_string {
    my $s = shift;
    $s =~ s/[\!\§\$\%\&\/\(\)\=\?\[\]\.\,\;\:\-\_\+\*\~\#\|\>\<\^\°\²\~\s\@\€\µ]+/ /g;
    $s =~ s/[\}\{\'\"\`\\]//g;
    $s = decode('latex', $s);
    return $s;
}

# some simple URL processing
sub clean_url {
    my $s = shift;
    $s =~ s/(^|\W)[\w]+\:\/\///g;             # remove protocol
    $s =~ s/www[0-9]*\.//g;                   # remove www.  www1. www2. ...
    # remove frequent file extensions, index pages
    $s =~ s/(index)*\.(html|htm|shtm|shtml|php|php3|php4|asp|cgi|pl|js|cfm|cfml|txt|text|ppt|pps)//g;
    return $s;
}


# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
  my $LOCKFILE = shift;
  my $PID;

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


sub get_date {
  my ($Sekunden, $Minuten, $Stunden, $Monatstag, $Monat,
      $Jahr, $Wochentag, $Jahrestag, $Sommerzeit) = localtime(time);
  $Monat       += 1;
  $Jahrestag   += 1;
  $Monat        = $Monat     < 10 ? $Monat     = "0".$Monat     : $Monat;
  $Monatstag    = $Monatstag < 10 ? $Monatstag = "0".$Monatstag : $Monatstag;
  $Stunden      = $Stunden   < 10 ? $Stunden   = "0".$Stunden   : $Stunden;
  $Minuten      = $Minuten   < 10 ? $Minuten   = "0".$Minuten   : $Minuten;
  $Sekunden     = $Sekunden  < 10 ? $Sekunden  = "0".$Sekunden  : $Sekunden;
  $Jahr        += 1900;
  return $Jahr."-".$Monat."-".$Monatstag." ".$Stunden.":".$Minuten.":".$Sekunden;
}
