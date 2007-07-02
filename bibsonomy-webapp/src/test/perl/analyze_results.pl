#
# Script to benchmark-test the bibsonomy2-API 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31

# modules / requires
use strict;
require "DB.pl";

# create database handler, http client, XML processor, stop watch      
my $db = eval { new DB(); }  or die ($@);


 
# standard format for query answers:
#
#+-------+------+-------+-------+-------+------+
#| posts | np   | min   | max   | avg   | dev  |
#+-------+------+-------+-------+-------+------+
#|     1 |    1 |  0.16 |  1.27 |  0.75 | 0.32 | 
#|     1 |    3 |  1.29 |  3.55 |  2.53 | 0.70 | 
#|     1 |    5 |  1.01 |  5.56 |  3.81 | 1.29 | 
#|     1 |   10 |  1.15 |  10.5 |  7.45 | 2.49 | 
#|     1 |   15 |  1.27 | 13.05 | 10.41 | 2.74 | 
#|     1 |   22 |  1.09 | 15.13 | 11.73 | 3.14 | 


my $statement;
my $sth;
my %arr;

print "<h1>Results of Bibsonomy2-Apitest</h1>";

# SYS_LOAD statistics 
print "<h3>Min / Max / Avg / StdDev SYS_LOAD</h3>";
$statement = "select num_posts as posts, num_parallel as np, min(sys_load) as min, max(sys_load) as max, round(avg(sys_load),2) as avg, round(stddev(sys_load),2) as dev from results group by num_parallel, num_posts order by posts, num_parallel";
$sth = $db->query($statement);
%arr = createArrayFromResults($sth);
printToHTMLTable(%arr);

# RAM_FREE statistics 
print "<h3>Min / Max / Avg / StdDev RAM_FREE</h3>";
print "Percentage of free RAM<br/>";
$statement = "select num_posts as posts, num_parallel as np, round(min(ram_free / 8179812),2) as min, round(max(ram_free / 8179812),2) as max, round(avg(ram_free / 8179812),2) as avg, round(stddev(ram_free / 8179812),2) as dev from results group by num_parallel, num_posts order by posts, num_parallel;";
$sth = $db->query($statement);
%arr = createArrayFromResults($sth);
printToHTMLTable(%arr);

# ELAPSED 
print "<h3>Min / Max / Avg / StdDev ELAPSED</h3>";
print "in seconds<br/>";
$statement = "select num_posts as posts, num_parallel as np, round(min(elapsed),3) as min, round(max(elapsed),3) as max, round(avg(elapsed),3) as avg, round(stddev(elapsed),3) as dev from results group by num_parallel, num_posts order by posts, num_parallel;";
$sth = $db->query($statement);
%arr = createArrayFromResults($sth);
printToHTMLTable(%arr);

# Number of requests answered per second
print "<h3>Min / Max / Avg / StdDev number of Requests answered per second</h3>";
print "Percentage of free RAM<br/>";
$statement = "select distinct R.posts, R.np, round(min(R.req_per_sec),0) as min, round(max(R.req_per_sec),0) as max, round(avg(R.req_per_sec),2) as avg, round(stddev(R.req_per_sec),2) as dev from (select timestamp, num_parallel as np, num_posts as posts, count(*) as req_per_sec from results group by timestamp, num_parallel, num_posts) R group by R.np, R.posts order by R.posts, R.np;";
$sth = $db->query($statement);
%arr = createArrayFromResults($sth);
printToHTMLTable(%arr);

# Number of requests answered per second
print "<h3>Nr. of Server errors (HTTP status 500)</h3>";
$statement = "select num_posts as posts, num_parallel as np, '', '', count(*), '' from results where http_status=500 group by num_parallel, num_posts order by posts, num_parallel";
$sth = $db->query($statement);
%arr = createArrayFromResults($sth);
printToHTMLTable(%arr);




sub printToHTMLTable{
	my (%arr) = @_;
	# array containing numbers of posts
	my @numPosts    = sort { $a <=> $b } keys(%arr) ;		            
	# array containing numbers of parallel testing clients
	my @numParallel = sort { $a <=> $b } keys %{$arr{@numPosts[0]}} ;
	
	print "<table style=\"border:1px solid black;border-collapse:collapse;width:80%\">\n";
	print "<tr style=\"border:1px solid black;\">";
	print "<th style=\"border:1px solid black;background-color:yellow\">&nbsp;</th>\n";
	foreach my $posts (@numPosts) {
		print "<th style=\"border:1px solid black;background-color:yellow\">$posts</th>\n";
	}
	
	foreach my $parallel (@numParallel) {
		print "<tr style=\"border:1px solid black\">\n";
		print "<td style=\"border:1px solid black;text-align:center;background-color:yellow\">$parallel</td>\n";
		foreach my $posts (@numPosts) {
			print "<td style=\"border:1px solid black;text-align:center;\">" . $arr{$posts}{$parallel} . "</td>\n";
		} 
		print "<tr>";		
	}		
	print "</table><br/><br/><br/>\n";	
}

sub createArrayFromResults{
	my ($sth) = @_;
	my %arr;
	# fill a two-dimensional array with the values
	while (my @row = $sth->fetchrow_array ) {
		my $posts = $row[0];
		my $numParallel = $row[1];
		my $valString = $row[2] . " | " . $row[3] . "<br/><b>" . $row[4] . "</b><br/>" . $row[5];
		$arr{$posts}{$numParallel} =	$valString;
	}	
	return %arr;
}