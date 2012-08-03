#!/usr/bin/perl
##############
#
# This script fetches the new public non-spammer bookmarks of the last hour
# from the slave datebase, creates a text file in the format
#
#   HASH|#|URL
#
# named by the current date/time and copies this file onto the webthumb 
# machine, which takes shapshots of each URL.
#  
# Changes:
#   2012-08-03: (dbe)
#   - initial version
#


#######################################################
# libraries
#######################################################
use strict;
use POSIX;
use Common qw(debug get_slave check_running);

#
# don't run two instances of this script simultaneously
#
check_running();

#######################################################
# configuration
#######################################################
my $webthumb_destination='webthumb@webthumb.cs.uni-kassel.de:~/incoming/';
my $delim = '|#|';


#######################################################
# main program
#######################################################

#
# connect to slave
#
my $slave = get_slave();

#
# prepare statements to get bookmarks of last hour 
#
my $stm_select_recent_urls = $slave->prepare("SELECT U.book_url_hash, U.book_url FROM bookmark B, urls U WHERE B.date >= DATE_SUB(NOW(),INTERVAL 1 HOUR) AND B.book_url_hash=U.book_url_hash AND B.group=0");
$stm_select_recent_urls->{"mysql_use_result"} = 1;

#
# execute query, write results to file 
#
$stm_select_recent_urls->execute();
my $filename = $ENV{'TMP'} . get_filename();
open OUT, "> $filename"; 
while (my @row = $stm_select_recent_urls->fetchrow_array ) {
    my $hash = $row[0];
    my $url = $row[1];
    print OUT $hash . $delim . $url . "\n";
}
close(OUT);
$slave->disconnect;

#
# scp the file to webthumb
#
system("scp $filename $webthumb_destination");


#######################################################
# SUBROUTINES
#######################################################

#
# get filename (consisting of current date/time)
#
sub get_filename() {
	return strftime("%Y-%m-%d_%H-%M", localtime(time))
}
