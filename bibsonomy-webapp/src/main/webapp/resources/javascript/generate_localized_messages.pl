#!/usr/bin/perl
use strict;

#
# this script parses messages*.properties files and generates
# corresponding javascript files with an array containing the 
# same key > string association.
#
# dbe, 20071204
#


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

	# parse properties file and write contents into javascript array
	foreach my $line (<M>) {
		chomp($line);
		my @keyValuePair = split("=", $line);
		if (scalar @keyValuePair == 2) {
			print JS '   "'.$keyValuePair[0].'" : "' . $keyValuePair[1] . '",' . "\n";
		}
	}

	print JS "\n\}\n";
	print "\n done.\n\n";
	close JS;
	close M;
}
