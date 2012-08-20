#!/usr/bin/perl
#
#	This script replaces occurences of $PROJECT_HOME$, $PROJECT_NAME$ and $PROJECT_DOMAIN$
#	with the project relevant data provided trough the 'project.properties' file and zips it for
#
#	mve, 17-08-12 initial version
#

use strict;
use Encode;
use File::Find;
use File::Path;
use File::Copy;

my $projectHome;
my $projectDomain;
my $projectName;
my @ofRelevantFiletype = ();
my @extSrc = ();
my $path = "project.properties";
my $tmpFolder = "tmp";

sub copyFolder {
    my ($srcDir, $dstDir) = @_;
    opendir my($swap), $srcDir;
    for my $item (readdir $swap) {
		next if $item =~ /^\.[\.]*/;
		my $src = "$srcDir/$item";
		my $dst = "$dstDir/$item";
		if (-d $src) {
			mkdir $dst;
			copyFolder($src, $dst);
		} else {
			copy($src, $dst);
		}
    }
    closedir $swap;
    return;
}

sub findRelevantFiles {
	my $file=$File::Find::name;
	push(@extSrc, $file) if ($file=~/ROOT_[A-Za-z0-9\.\@_\$]+$/);

	push(@ofRelevantFiletype, $file) if ($file=~/\.(js|dtd|properties|rdf|json|xul)$/);
#		if($file =~/manifest\.json|install\.rdf/) {
#			push(@extManifest, $file);
#		}
}

open(DATEI, $path) or die ("file at '$path' not found");

foreach my $line (<DATEI>) {
	if($line=~/project\.(home|name)/) {
		if($1=~/home/) {
			$line=~s/^\s+|(project.home|[ =]*)|\s+$//g;
			$projectHome=$line;
			$projectDomain=$projectHome;
			$projectDomain=~s/^\s+|(^(http:\/\/(www.){0,1})|\/[a-z]*)|\s+$//g;
		} else {
			$line=~s/^\s+|(project.name|[ =]*)|\s+$//gs;
			$projectName=$line;
		}
	} 
}
close(DATEI);

rmtree($tmpFolder);
mkdir($tmpFolder);
copyFolder("src", "tmp");

find(\&findRelevantFiles, "$tmpFolder/");

sub renameFolder {
#$index = 0;
#$version;

	foreach my $oldDir (@extSrc) {
# ADD VERSION TO FILENAME
#		$file=$extManifest[$index];
#		open(FILE, "<$file") or die("could not open $file");
#		foreach $line (<FILE>) {
#			if($line=~/[\":]version/g) {
#				print "$oldDir\n";
#				if($line=~/[0-9]/g) {
#					$version="-$1"; 
#				}
#				break;
#			}
#		}
#		close(FILE);
		my $newDir=$oldDir;
		$newDir=~s/\$PROJECT_HOME\$/$projectHome/g;
		$newDir=~s/\$PROJECT_NAME\$/$projectName/g;
		$newDir=~s/\$PROJECT_DOMAIN\$/$projectDomain/g;
		$newDir=~s/ROOT_//g;
		my $suffix=$1 if($newDir=~/[a-zA-Z0-9]+$/);
		my $filename=$1 if($newDir=~/([\@\.a-zA-Z0-9]+)$/);
		$newDir=~s/\.[a-zA-Z0-9]+$/\//;
		rename($oldDir, $newDir);
		system("(cd $newDir; zip -rq $filename .)");
		print "(cd $newDir; zip -rq $filename .)\n";
	}
}
		
foreach my $file (@ofRelevantFiletype) {
	open(COPY, "+>>$file~") or die("could not create $file~");
	open(FILE, "<$file");
	my $line = 1;
	print "-- FILE: $file\n";
	foreach (<FILE>) {
		if(/\$(PROJECT_)/) {
			print "<< LN$line: \t$_";
			s/\$PROJECT_HOME\$/$projectHome/g;
			s/\$PROJECT_NAME\$/$projectName/g;
			s/\$PROJECT_DOMAIN\$/$projectDomain/g;
			print ">> \t\t$_";
		}
		print COPY;
		$line++;
	}
	close(FILE);
	close(COPY);
	unlink($file);
	rename("$file~", $file);
}

renameFolder();
