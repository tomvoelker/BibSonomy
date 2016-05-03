#!/usr/bin/perl

# this perl exports CSL styles for ez ctrl+c | ctrl+v => CSL ENUM

use File::Find;

my @styleFiles =();

my $jsfile = "cslstyles.java";
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
	$varname = uc$varname;
	$varname = $varname =~ s/-/_/gr;
    print "create style: ".$varname;
	
	foreach $_ (@input) {
        s/\"/\\\"/g;
        s/\n//g;
    }
	print JSFILE "$varname(\"";
	print JSFILE (@input);
	print JSFILE "\"),\n";
	print "............done!\n";
}
close(JSFILE);
