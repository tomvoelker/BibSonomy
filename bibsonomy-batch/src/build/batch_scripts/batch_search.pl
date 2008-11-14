#!/usr/bin/perl
############################################################
use DBI();
use TeX::Encode;
use Encode;
use strict;

my $CONTENT_TYPE_BOOKMARK=1;
my $CONTENT_TYPE_BIBTEX=2;

if ($#ARGV != 1) {
    print "please enter database name as first, and table name as second argument\n";
    exit;
}
if (am_i_running($ENV{'TMP'}."/batch_search.pid")) {
  print "another instance of batch_search.pl is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}
 
my $database = shift @ARGV;
my $table    = shift @ARGV;
my $password = $ENV{'DB_PASS'};


# connect to database (read uncommited added by aho and mysql_use_result
my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/mysql.sock", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1, "transaction-isolation" => "READ-UNCOMMITTED"});
#my $dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/mysql.sock", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});

#my $dbh = DBI->connect("DBI:mysql:database=$database;host=gandalf", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});




#################################
# prepare statements
#################################

# get last content_id from search table
my $stm_select_content_id = $dbh->prepare ("SELECT content_id FROM $table ORDER BY content_id DESC LIMIT 1");

my $stm_disable_keys = $dbh->prepare ("ALTER TABLE search2 DISABLE KEYS");
my $stm_enable_keys = $dbh->prepare ("ALTER TABLE search2 ENABLE KEYS");

# get all relevant bookmarks from bookmark table
my $stm_select_book = $dbh->prepare ("
  SELECT b.content_id, b.group, b.date, b.user_name, b.book_description, b.book_extended, u.book_url    
    FROM bookmark b JOIN urls u USING (book_url_hash)
    WHERE b.content_id > ?");
   $stm_select_book->{"mysql_use_result"} = 1;

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
   $stm_select_bib->{"mysql_use_result"} = 1;

# get all relevant data from tas table
my $stm_select_tas = $dbh->prepare ("
  SELECT content_id, tag_name
    FROM tas
    WHERE content_id > ?");
   $stm_select_tas->{"mysql_use_result"} = 1;

# insert into search table
my $stm_insert = $dbh->prepare ("INSERT INTO $table (content_id, content, author, `group`, `date`, content_type, user_name) VALUES (?,?,?,?,?,?,?)");

# select content ids which can be deleted
my $stm_get = $dbh->prepare ("SELECT s.content_id FROM $table s LEFT JOIN tas b USING (content_id) WHERE b.content_id IS NULL;");

# delete content_ids
my $stm_del = $dbh->prepare ("DELETE FROM $table WHERE content_id = ?");



my @row;                 # temporary rows from queries
my @data = ();           # temporary data for search table
my $last_content_id = 0; # default for populating table the first time
my $ctr = 0;


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
my %tag_hash;

$stm_select_tas->execute($last_content_id);
while (@row = $stm_select_tas->fetchrow_array) {
    $tag_hash{$row[0]} .= " $row[1]";
}
$stm_select_book->finish();



#################################
# retrieve all bookmark rows
#################################
my $content;
$stm_select_book->execute($last_content_id);

while (@row = $stm_select_book->fetchrow_array) {
    $ctr++;
    $content = clean_string("$row[4] $row[5] $row[6] $tag_hash{$row[0]}");
      
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
    $content = "";
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




#################################
# insert rows
#################################
if ($database eq "search2") {
  $stm_disable_keys->execute();     # enable this, when building table from scratch
}

$ctr = 0;
while ($#data > -1) {
    my @feld = @{pop(@data)};

    $stm_insert->execute($feld[0], $feld[1], $feld[2], $feld[3], $feld[4], $feld[5], $feld[6]);
    $ctr++;
    if ($ctr % 10000 == 0) {
	print "write $ctr\n";
	$dbh->commit();
    }
}

$dbh->commit;

if ($database eq "search2") {
  $stm_enable_keys->execute();      # enable this, when building table from scratch
}





#################################
# delete old rows
#################################
#my $stm_clean = $dbh->prepare ("DELETE FROM $table WHERE content_id NOT IN (SELECT content_id FROM tas GROUP BY content_id)");

# delete entries which do not exist any longer
$stm_get->execute();

while (@row = $stm_get->fetchrow_array) {
    $stm_del->execute($row[0]);
}
$dbh->commit;
$stm_get->finish;
$stm_del->finish;





#################################
# disconnect database
#################################

$dbh->disconnect();









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
