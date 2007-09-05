#!/usr/bin/perl -w

# Get BibTeX entries for a given LaTeX document from BibSonomy.

use LWP::Simple;
use strict;

if (@ARGV < 2) {
  die "Usage: $0 <username> <aux file>\n";
}

my $user = shift;
my $aux = shift;
my $allBib = "";
my $biburl = "http://www.bibsonomy.org/bib/search/";

$user = "+user:$user";

my $bibtexAll = "";
my %wantToSee = ();
open AUX, "<$aux" or die "Usage: $0 <username> <aux file>\n";
while(<AUX>) {
  chomp;
  if (/\\citation{(.*?)}/) {

    for my $key (split /,/, $1) {
      $wantToSee{$key} = 1;
    }
  }
}
close AUX;

for my $key (sort keys %wantToSee) {
  my $bibtex = get("$biburl$key$user");
  
  if ($bibtex =~ /^\s*$/s) {
    print STDERR "No result for key $key\n";
  }
  
  $bibtexAll .= $bibtex;
  
}

print $bibtexAll;
