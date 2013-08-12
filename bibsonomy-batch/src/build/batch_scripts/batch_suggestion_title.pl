#!/Users/nilsraabe/perl5/perlbrew/perls/perl-5.16.0/bin/perl

use DBI();
use English;
use Common qw(debug get_slave get_master check_running);
use DBI qw(:utils);
use Encode;

########################################################
# SLAVE 
########################################################

# connect to database as slave
my $slave = get_slave();

binmode FILE, ":utf8";
binmode STDOUT, ":utf8";			

################ GET BOOKMARK TITLE ################
print "Reading bookmark title ...\n";

# prepare statements
# get all public bookmark title + rating from the bookmark table
my $stm_select_bookmark_names = $slave->prepare("SELECT book_description, rating FROM bookmark b WHERE b.group=0");

# execute statements 
$stm_select_bookmark_names->execute();
$slave->commit;

# go over all public title + ratings and store it in a text file
open (FILE, ">bookmark_title.txt") or die $!;
	while (my @bookmark = $stm_select_bookmark_names->fetchrow_array ) {
		if ($bookmark[0] ne "") {
			
			$title = decode_utf8($bookmark[0]);
			$title =~ s/\r\n/ /;
			$title =~ s/\s{1}\s+/ /;
			$title =~ s/\R//g;
			
			$rating = decode_utf8($bookmark[1]);

			print FILE "$title\n$rating\n";
		}
	}
close (FILE);
$slave->commit;
print "Bookmark complete !\n";

################ GET BIBTEX TITLE ################
print "Reading bibtex title ...\n";

# prepare statements
# get all public bibtex title + rating from the bibtex table
my $stm_select_bibtex_names = $slave->prepare("SELECT title, rating FROM bibtex b WHERE b.group=0");

# execute statements
$stm_select_bibtex_names->execute();
$slave->commit;

# go over all public title + ratings and store it in a text file
open (FILE, ">bibtex_title.txt") or die $!;
	while (my @bibtex = $stm_select_bibtex_names->fetchrow_array ) {
		if ($bibtex[0] ne "") {
			
			my $title = decode_utf8($bibtex[0]);
			$title =~ s/\r\n/ /;
			$title =~ s/\s{1}\s+/ /;
			$title =~ s/\R//g;
			
			$rating = decode_utf8($bibtex[1]);

			print FILE "$title\n$rating\n";
		}
	}
close (FILE);

print "Bibtex complete !\n";
$slave->commit;