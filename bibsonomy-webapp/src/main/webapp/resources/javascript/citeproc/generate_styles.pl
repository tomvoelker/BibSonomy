#!/usr/bin/perl
use File::Find;

my @styleFiles =();

my $jsfile = "styles.js";
unlink($jsfile);

find(sub { push(@styleFiles, $File::Find::name) if /\.csl$/ }, './styles');

open (JSFILE, ">>$jsfile");
foreach my $file (@styleFiles) {
    open (FILE, "<$file") or die "could not open $file\n";
    @input = <FILE>;
    close (FILE);
    $varname = $file;
    $varname =~ s/.csl//;
    $varname =~ s/.*styles\///;
    print "create style: ".$varname;
	
	foreach $_ (@input) {
        s/\"/\\\"/g;
        s/\n//g;
    }
	print JSFILE "var $varname =\"";
	print JSFILE (@input);
	print JSFILE "\";\n";
	print "............done!\n";
}
close(JSFILE);
