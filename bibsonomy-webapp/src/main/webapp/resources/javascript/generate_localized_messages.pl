#!/usr/bin/perl
use strict;

#
# this script parses messages*.properties files and generates
# corresponding javascript files with an array containing the 
# same key > string association.
#
# dbe, 20071204
#

use Encode;

# directory with messages.properties, messages_de.properties, ...
my @files = <../../../resources/messages*.properties>;

# parse properties file and generate javascript file
foreach my $file (@files) {
	print "processing " . $file . "\n";
	$file =~ /.*messages[\_]?([^\.]+)\.properties/;
	my $locale = ($1 ? $1 : 'en');

	# open javascript localized strings file
	open(JS, "> ./localized_strings_$locale.js");
	print "writing localized_strings_$locale.js";
	print JS "var LocalizedStrings = \{ \n";

	open(M, "< $file");

    my $pairs = "";

	# parse properties file and write contents into javascript array
	foreach my $line (<M>) {
		chomp($line);
		#
		# messages_de.properties is in latin1 (because of a strange Java/JSTL bug)
		# but we want UTF-8 for the JavaScript file ... so we convert
		#
		if ($locale eq "de") {
		    $line = encode("utf-8", decode("iso-8859-1", $line));
		}

		my ($key, $value) = split("=", $line);
		# remove leading/trailing whitespace
		$key = trim($key);
		$value = trim($value);
		# escape backslashes
		$value =~ s/\\/\\\\/g;		
		# escape quotation marks & backslashes
		$value =~ s/\"/\\\"/g;
		if (length($key) > 0 and length($value) > 0 and not (substr($key,0,1) eq '#')) {
			$pairs .= '   "'.$key.'" : "'.$value.'",' . "\n";
		}
	}
	
	# remove last comma
	$pairs = substr($pairs, 0, length($pairs) - 2 );
	
	print JS $pairs;
	
	print JS "\n\}\n";
	print "\n done.\n\n";
	close JS;
	close M;
}

sub trim($){
    my $string = shift;
    $string =~ s/^\s+//;
    $string =~ s/\s+$//;
    return $string;
}
