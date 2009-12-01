#!/usr/bin/perl
##############
use DBI();
use strict;
require DBD::mysql;
binmode( STDOUT, ":utf8" );
#####################################
# This script is obsolete and substituted by a java tool!
#####################################

exit(1);

#####################################
# database name as argument
#####################################
if ( $#ARGV != 0 ) {
	print "please enter database name as first argument\n";
	exit;
}

if ( am_i_running( $ENV{'TMP'} . "/batch_authors.pid" ) ) {
	print "another instance of batch_authors.pl is running on $ENV{'hostname'}. Aborting this job.\n";
	exit;
}
#####################################
# database connection settings
#####################################
my $db_name        = shift @ARGV;
my $db_user        = "batch";
my $db_pass        = $ENV{'DB_PASS'};
my $db_slave_host  = "localhost";
my $db_slave_port  = "3306";
my $db_master_host = "gandalf";
my $db_master_port = "6033";

my $dbs_slave	= "DBI:mysql:database=$db_name;host=$db_slave_host:$db_slave_port;mysql_socket=/var/mysql/run/mysqld.sock";
my $dbs_master	= "DBI:mysql:database=$db_name;host=$db_master_host:$db_master_port";

my %author_hash       = ();
my %author_count_hash = ();

########################################################
# SLAVE
########################################################
my $dbh = DBI->connect( $dbs_slave, $db_user, $db_pass, { RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1 } );

#################################
# prepare statements
#################################
# get all authors from bibtex table
my $stm_select_authors = $dbh->prepare("SELECT author FROM bibtex");

# get authors and counter from author table
my $stm_select_author_counts = $dbh->prepare("SELECT author_like_in_bibtex_tab, ctr FROM author WHERE ctr > 0");

#################################
# execute statements (author names and counter)
#################################
$stm_select_authors->execute();
$dbh->commit();

#######################################################################
# get author names and store it with a related counter into a hash
######################################################################
while ( my @author = $stm_select_authors->fetchrow_array ) {

	my @sep_authors = split( /and/, $author[0] );

	foreach my $sep_author (@sep_authors) {
		$sep_author = join( " ", split " ", $sep_author );

		if ( exists $author_hash{$sep_author} ) {
			$author_hash{$sep_author}++;
		}
		else {
			$author_hash{$sep_author} = 1;
		}
	}
}

####################################################
# get author names and counts from the author table
####################################################
$stm_select_author_counts->execute();
$dbh->commit();

while ( my @author_count = $stm_select_author_counts->fetchrow_array ) {
	$author_count_hash{ $author_count[0] } = $author_count[1];
}

$dbh->commit();
$dbh->disconnect();

########################################################
# MASTER
########################################################
$dbh =
  DBI->connect( $dbs_master, $db_user, $db_pass,
	{ RaiseError => 1, AutoCommit => 0, "mysql_enable_utf8" => 1 } );

#################################
# prepare statements
#################################
my $stm_update_authors = $dbh->prepare("UPDATE author SET ctr = ? WHERE author_like_in_bibtex_tab = ?");
my $stm_insert_authors = $dbh->prepare("INSERT INTO author (ctr, author_like_in_bibtex_tab) VALUES (?, ?)");

####################################################
# update authors and counter in the author table
####################################################
for my $key ( sort { $a cmp $b } keys %author_hash ) {
	my $count = $author_hash{$key};

	#####################################################
	# - insert author and counter if author doesn't exist
	# - update author and counter if counter has changed
	# - do nothing if the author and counter are up2date
	#####################################################

	my $val_key = $key;
	$val_key =~ s/\s+//gi;

	# DEVEL/DEBUG MODE:
	# print $key . "\n";

	if ( length($val_key) != 0 ) {
		if ( not exists $author_count_hash{$key} ) {
			$stm_insert_authors->execute( $count, $key );

			# DEVEL/DEBUG MODE:
			# print "insert: $key : $author_hash{$key}\n";
		}
		elsif ( $author_count_hash{$key} != $count && length($val_key) > 1 ) {
			$stm_update_authors->execute( $count, $key );

			# DEVEL/DEBUG MODE:
			# print "update1: $key : $author_hash{$key}\n";
		}
	}
}

$dbh->commit();
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
	my $PID      = "";
	if ( open( FILE, "<$LOCKFILE" ) ) {
		while (<FILE>) {
			$PID = $_;
		}
		close(FILE);
		chomp($PID);

		if ( kill( 0, $PID ) ) {
			return 1;
		}
	}
	open( FILE, ">$LOCKFILE" );
	print FILE $$;
	close(FILE);
	return 0;
}
