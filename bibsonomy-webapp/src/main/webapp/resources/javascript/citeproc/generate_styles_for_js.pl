#!/usr/bin/perl

# this perl exports the CSL files for ez ctrl+c | ctrl+v => settings.js
# don't forget to remove the last ","!


use File::Find;

my @styleFiles =();

my $jsfile = "settings.js";
unlink($jsfile);

find(sub { push(@styleFiles, $File::Find::name) if /\.csl$/ }, './styles');

open (JSFILE, ">>$jsfile");
print JSFILE "var CSLLayouts = '[";
foreach my $file (@styleFiles) {
    open (FILE, "<$file") or die "could not open $file\n";
    @input = <FILE>;
    close (FILE);
    $varname = $file;
    $varname =~ s/.csl//;
    $varname =~ s/.*styles\///;
	$varnameuc = uc$varname;
	$varnameuc = $varnameuc =~ s/-/_/gr;
    print "create style: ".$varname;
	
	foreach $_ (@input) {
        s/\"/\\\"/g;
        s/\n//g;
    }
	print JSFILE "{\"source\":\"CSL\", \"displayName\":\"$varname\", \"name\":\"$varnameuc\"}";
	print JSFILE ",";
	print "............done!\n";
}

# don't forget to remove the last "," !!!
print JSFILE "]'";
close(JSFILE);
