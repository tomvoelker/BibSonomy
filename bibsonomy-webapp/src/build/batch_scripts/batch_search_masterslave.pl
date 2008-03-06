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
#
use DBI();
use TeX::Encode;
use Encode;
use English;
use strict;

my $CONTENT_TYPE_BOOKMARK = 1;
my $CONTENT_TYPE_BIBTEX = 2;

if ($#ARGV != 1) {
    print "please enter database name as first, and table name as second argument\n";
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
my $database = shift @ARGV;     # same db name on all hosts
my $table    = shift @ARGV;     # same table name on all hosts
my $user     = "batch";         # same user name on all databases
my $password = $ENV{'DB_PASS'}; # same password on all databases
# fit to slave
my $slave    = "DBI:mysql:database=$database;host=localhost:3306;mysql_socket=/var/mysql/run/mysqld.sock";
# fit to master
my $master   = "DBI:mysql:database=$database;host=gandalf:6033";


# temporary variables
my @row;                 # temporary rows from queries
my @data = ();           # temporary data for search table
my $last_content_id = 0; # default for populating table the first time
my $ctr = 0;
my %tag_hash;            # stores tags


########################################################
# SLAVE
#########################################################
# connect to database
my $dbh = DBI->connect($slave, $user, $password, {RaiseError => 1, "mysql_auto_reconnect" => 1, "mysql_enable_utf8" => 1});
# prepare statements
# get last content_id from search table
my $stm_select_content_id = $dbh->prepare ("SELECT content_id FROM $table ORDER BY content_id DESC LIMIT 1");

# get all relevant bookmarks from bookmark table
my $stm_select_book = $dbh->prepare ("
  SELECT b.content_id, b.group, b.date, b.user_name, b.book_description, b.book_extended, u.book_url    
    FROM bookmark b JOIN urls u USING (book_url_hash)
    WHERE b.content_id > ?");

# get all relevant bibtex from bibtex table
my $stm_select_bib  = $dbh->prepare ("
  SELECT b.content_id,   b.group,       b.date,         b.user_name,      b.author, 
         b.editor,       b.title,       b.journal,      b.booktitle,      b.volume, 
         b.number,       b.chapter,     b.edition,      b.month,          b.day, 
         b.howPublished, b.institution, b.organization, b.publisher,      b.address, 
         b.school,       b.series,      b.bibtexKey,    b.url,            b.type, 
         b.description,  b.annote,      b.note,         b.pages,          b.bKey, 
         b.number,       b.crossref,    b.misc,         b.bibtexAbstract, b.year
   FROM bibtex b
   WHERE b.content_id > ?");

# get all relevant data from tas table
my $stm_select_tas = $dbh->prepare ("
  SELECT content_id, tag_name
    FROM tas
    WHERE content_id > ?");



#################################
# get last content_id
#################################

$stm_select_content_id->execute();
if (@row = $stm_select_content_id->fetchrow_array) {
    $last_content_id = $row[0];
} 
$stm_select_content_id->finish();



#################################
# retrieve tas rows
#################################

$stm_select_tas->execute($last_content_id);
while (@row = $stm_select_tas->fetchrow_array) {
    $tag_hash{$row[0]} .= " $row[1]";
}
$stm_select_tas->finish();



#################################
# retrieve all bookmark rows
#################################
$stm_select_book->execute($last_content_id);
while (@row = $stm_select_book->fetchrow_array) {
    $ctr++;
    my $content = clean_string("$row[4] $row[5] $row[6] $tag_hash{$row[0]}");
      
    # save data
    my @array = ($row[0], $content, "", $row[1], $row[2], $CONTENT_TYPE_BOOKMARK, $row[3]); 
    push (@data, \@array);
    
    print "read url $ctr\n" if ($ctr % 10000 == 0);
}
$stm_select_book->finish();




#################################
# retrieve bibtex rows
#################################
$stm_select_bib->execute($last_content_id);
while (@row = $stm_select_bib->fetchrow_array) {
    $ctr++;
    my $content = "";
    for (my $i=4; $i<35; $i++) {
	  $content = $content." ".$row[$i];
    }
    $content = clean_bibtex_string("$content $tag_hash{$row[0]}");

    # save data
    my @array = ($row[0], $content, "$row[4] $row[5]", $row[1], $row[2], $CONTENT_TYPE_BIBTEX, $row[3]); 
    push (@data, \@array);
    
    print "read bib $ctr\n" if ($ctr % 10000 == 0);
}
$stm_select_bib->finish();


########################################################
# MASTER
########################################################
# connect to database
my $dbh_master = DBI->connect($master, $user, $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
# prepare statements
my $stm_disable_keys = $dbh_master->prepare ("ALTER TABLE search2 DISABLE KEYS");
my $stm_enable_keys = $dbh_master->prepare ("ALTER TABLE search2 ENABLE KEYS");
# insert into search table
my $stm_insert = $dbh_master->prepare ("INSERT INTO $table (content_id, content, author, `group`, `date`, content_type, user_name) VALUES (?,?,?,?,?,?,?)");
# delete content_ids
my $stm_del = $dbh_master->prepare ("DELETE FROM $table WHERE content_id = ?");


#################################
# insert rows
#################################
$stm_disable_keys->execute() if ($table eq "search2");  # enable this, when building table from scratch

$ctr = 0;
while ($#data > -1) {
    my @feld = @{pop(@data)};

    $stm_insert->execute($feld[0], $feld[1], $feld[2], $feld[3], $feld[4], $feld[5], $feld[6]);
    $ctr++;
    if ($ctr % 10000 == 0) {
	print "write $ctr\n";
	$dbh_master->commit();
    }
}

$dbh_master->commit;
$stm_enable_keys->execute() if ($table eq "search2");   # enable this, when building table from scratch



# select content ids which can be deleted
my $stm_get = $dbh->prepare ("SELECT s.content_id FROM $table s LEFT JOIN tas b USING (content_id) WHERE b.content_id IS NULL;");

#################################
# delete old rows
#################################
# delete entries which do not exist any longer
$stm_get->execute();

while (@row = $stm_get->fetchrow_array) {
    $stm_del->execute($row[0]);
}
$dbh_master->commit;
$stm_get->finish;
$stm_del->finish;





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
