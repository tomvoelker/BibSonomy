#!/usr/bin/perl
##############
use DBI();

$MAX_LAST_ROWS = 15000;
$MAX_ABS_ROWS = 100;

if ($#ARGV != 0) {
  print "please enter database name as first argument\n";
  exit;
} 

if (am_i_running($ENV{'TMP'}."/batch_popular.pid")) {
  print "another instance of batch_popular.pl is running on $ENV{'hostname'}. Aborting this job.\n";
  exit;
}

$database = shift @ARGV;
$password = $ENV{'DB_PASS'};


# connect to database
$dbh = DBI->connect("DBI:mysql:database=$database;host=localhost:6033;mysql_socket=/home/bibsonomy/mysql-var/mysql.sock", "bibsonomy", $password, {RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1});
#################################
# prepare statements
#################################
# get last 10000 posts, order them by ctr
$stm_select_bookmark = $dbh->prepare("SELECT book_url_hash,count(book_url_hash) AS ctr FROM (SELECT book_url_hash FROM bookmark WHERE bookmark.group = 0 ORDER BY date DESC LIMIT $MAX_LAST_ROWS) AS b GROUP BY book_url_hash ORDER BY ctr DESC LIMIT $MAX_ABS_ROWS");
$stm_select_bibtex = $dbh->prepare("SELECT simhash1,count(simhash1) AS ctr FROM (SELECT simhash1 FROM bibtex WHERE bibtex.group = 0 ORDER BY date DESC LIMIT $MAX_LAST_ROWS) AS b GROUP BY simhash1 ORDER BY ctr DESC LIMIT $MAX_ABS_ROWS");
# to get the first posting of that content_id
$book_rows = "content_id,book_description,book_extended,book_url_hash,date,user_name,rating";
$bib_rows  = "content_id,journal,volume,chapter,edition,month,day,bookTitle,howPublished,institution,organization,publisher,address,school,series,bibtexKey,date,user_name,url,type,description,annote,note,pages,bKey,number,crossref,misc,bibtexAbstract,simhash0,simhash1,simhash2,simhash3,title,author,editor,year,entrytype,rating";
$fz = ""; foreach (split (",", $bib_rows)) {$fz = $fz."?,";}
$stm_get_bookmark = $dbh->prepare("SELECT $book_rows FROM bookmark WHERE book_url_hash = ? AND bookmark.group = 0 ORDER BY date LIMIT 1");
$stm_get_bibtex = $dbh->prepare("SELECT $bib_rows FROM bibtex WHERE simhash1 = ? AND bibtex.group = 0 ORDER BY date LIMIT 1");
$stm_insert_bookmark = $dbh->prepare("INSERT INTO temp_bookmark ($book_rows,book_url_ctr,rank) VALUES (?,?,?,?,?,?,?,?,?)");
$stm_insert_bibtex = $dbh->prepare("INSERT INTO temp_bibtex ($bib_rows,ctr,rank) VALUES ($fz?,?)");
# delete old values from bookmark table
$stm_delete_bookmark = $dbh->prepare("DELETE FROM temp_bookmark");
$stm_delete_bibtex = $dbh->prepare("DELETE FROM temp_bibtex");

#################################
# execute statements (Bookmark)
#################################
# get TOP bookmarks
$stm_select_bookmark->execute();
$dbh->commit;
# clean table
$stm_delete_bookmark->execute();

# get first occurence of every bookmark
$rank = 0;
while (@id = $stm_select_bookmark->fetchrow_array ) {
  $rank++;
  #print "U: $rank ";
  $stm_get_bookmark->execute($id[0]);
  #print "e ";
  while (@row = $stm_get_bookmark->fetchrow_array) {
     $stm_insert_bookmark->execute($row[ 0],$row[ 1],$row[ 2],$row[ 3],$row[ 4],$row[5],$row[6],$id[1],$rank);
                                   
     #print "i\n";
  }
}
$dbh->commit;

#################################
# execute statements (BibTeX)
#################################
# get TOP bookmarks
$stm_select_bibtex->execute();
$dbh->commit;
# clean table
$stm_delete_bibtex->execute();

# get first occurence of every bookmark
$rank = 0;
while (@id = $stm_select_bibtex->fetchrow_array ) {
  $rank++;
  #print "B: $rank ";
  $stm_get_bibtex->execute($id[0]);
  #print "e ";
  while (@row = $stm_get_bibtex->fetchrow_array) {
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
  $LOCKFILE = shift;

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
