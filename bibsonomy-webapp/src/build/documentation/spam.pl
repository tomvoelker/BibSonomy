#!/usr/bin/perl

use strict;

#my $url = "http://www.bibsonomy.org/url/8d804d77e6ad17d3bd6376f3b9545704";
my @urls = ("http://www.bibsonomy.org/url/3471ab0837f129753d452498ae76de01");
my $output="";


foreach my $url (@urls) {
    open(FILE, "wget -q -O - $url|") or die "could not crawl $url\n"; 
    while (<FILE>) {
        if ($_ =~ m/by <a href=\"\/user\/(.+?)\">(.+?)<\/a>/) {
            $output=$output.$1."\n";
            my $command = "wget -q -O /dev/null  --header 'Cookie: _currUser=hotho%20565c59ea7d7f5000adfba30358080c22' 'http://www.bibsonomy.org/admin.jsp?user=".$1."&action=flag_spammer'\n";
#           print $command;
           system($command) == 0  or die "Fehler bei system: $?"
        }
    } 
    close(FILE);
}
if ($output ne "") { 
    print "Following User are automatically marked as spammer:\n";
    print $output;
}
