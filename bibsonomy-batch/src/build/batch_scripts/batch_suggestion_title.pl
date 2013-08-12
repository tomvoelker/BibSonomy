#!/usr/bin/perl

use DBI();
use English;
use Common qw(debug get_slave check_running);
use DBI qw(:utils);
use Encode;

########################################################
# SLAVE 
########################################################

# connect to database as slave
my $slave = get_slave();

################ GET BOOKMARK TITLE ################
print "Reading bookmark title ...\n";

# prepare statements
# get all public bookmark title + rating from the bookmark table
my $stm_select_bookmark_names = $slave->prepare("SELECT book_description, rating FROM bookmark b WHERE b.group=0");

# execute statements 
$stm_select_bookmark_names->execute();
$slave->commit;

# go over all public title + ratings and store it in a text file
open (FILE, "> " . $ENV{'SUGGESTION_PATH'} . "/bookmark_title.txt") or die $!;
binmode FILE, ":utf8";
while (my @bookmark = $stm_select_bookmark_names->fetchrow_array ) {
	if ($bookmark[0] ne "") {
		
		$title = $bookmark[0];
		$title =~ s/\r\n/ /;
		$title =~ s/\s{1}\s+/ /;
		$title =~ s/\R//g;
			
		$rating = $bookmark[1];

		print FILE "$title\n$rating\n";
	}
}
close (FILE);

print "Bookmark complete !\n";

################ GET PUBLICATION (AKA BIBTEX) TITLE ################
print "Reading publication title ...\n";

# prepare statements
# get all public publication title + rating from the bibtex table
my $stm_select_publication_names = $slave->prepare("SELECT title, rating FROM bibtex b WHERE b.group=0");

# execute statements
$stm_select_publication_names->execute();
$slave->commit;

# go over all public title + ratings and store it in a text file
open (FILE, "> " . $ENV{'SUGGESTION_PATH'} . "/publication_title.txt") or die $!;
binmode FILE, ":utf8";
while (my @publication = $stm_select_publication_names->fetchrow_array ) {
	if ($publication[0] ne "") {
		my $title = $publication[0];
		$title =~ s/\r\n/ /;
		$title =~ s/\s{1}\s+/ /;
		$title =~ s/\R//g;
		
		$rating = $publication[1];

		print FILE "$title\n$rating\n";
	}
}
close (FILE);

print "publication complete !\n";
$slave->disconnect();