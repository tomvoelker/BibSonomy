#
# Script to benchmark-test the bibsonomy2-API 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31

# configuration - HTTP authentication, api-url
my $username = "dbenz";						
my $password = "a9999a44a48879d28bd34fd32bdfa0c1";
my $apiUrl = "http://www.biblicious.org/api/";

# modules / requires
use strict;
use Socket;
use Sys::Hostname;
require "DB.pl";
require "LogFile.pl";
require "HTTPClient.pl";   
require "XMLProc.pl";
require "StopWatch.pl";

# create database handler, http client, XML processor, stop watch      
my $db = eval { new DB(); }  or die ($@);
my $cl = eval { new HTTPClient($username, $password); }  or die ($@);
my $xp = eval { new XMLProc(); }  or die ($@);
my $sw = eval { new StopWatch(); }  or die ($@);
my $log = eval { new LogFile($db); }  or die ($@); 

# parameters
my $hostname = hostname();
my $host = inet_ntoa(scalar(gethostbyname($hostname)) || 'localhost');
my $num_parallel = ($ARGV[0] ? $ARGV[0] : 1);


# start test runs
# testGetBibtexByTag();
# testGetBibtexByUser();
# testGetBibtexByHash();

# benchmark test
testGetBibtexForUserDBLP(1, 1000);
testGetBibtexForUserDBLP(20, 1000);
testGetBibtexForUserDBLP(100, 1000);
testGetBibtexForUserDBLP(500, 1000);
testGetBibtexForUserDBLP(1000, 1000);
testGetBibtexForUserDBLP(5000, 500);
testGetBibtexForUserDBLP(10000, 500);


##############################################################################
##############################################################################


#
# user DBLP
# benchmark test for user dblp; retrieve $numRuns times $numPosts posts
#
sub testGetBibtexForUserDBLP {
	my ($numPosts, $numRuns) = @_;
		
	my $i = 0;
	for ($i = 0; $i < $numRuns; $i++) {		
		my $start = int(rand(2000)); # choose start randomly to avoid caching
		my $end = $start + $numPosts; 					
	  	my $query = "users/dblp/posts?resourcetype=bibtex&start=$start&end=$end";
	  	process($query, "getBibtexByUser", $numPosts);
	}
}



#
# getBibtexByTag
# retrieves 20 BibTeX-Posts for for the 10000 most popular tags
#
sub testGetBibtexByTag {
	my $statement = "SELECT tag_name FROM tags ORDER BY tag_ctr DESC LIMIT 10000";
	my $sth = $db->query($statement);
	while (my @row = $sth->fetchrow_array ) {	
	  	my $tag = $row[0];
	  	my $query = "posts?resourcetype=bibtex&tags=$tag&start=0&end=20";
	  	process($query, "getBibtexByTag");
	}
}

#
# getBibtexByUser
# retrieves all BibTeX-Posts for all users
#
sub testGetBibtexByUser {
	my $statement = "SELECT user_name FROM user";
	my $sth = $db->query($statement);
	while (my @row = $sth->fetchrow_array ) {	
	  	my $username = $row[0];
	  	my $query = "users/$username/posts?resourcetype=bibtex&start=0&end=1000000";
	  	process($query, "getBibtexByUser");
	}
}

#
# getBibtexByHash
# retrieves 100.000 random BibTeX-Posts identified by their hashes
#
sub testGetBibtexByHash {
	my $statement = "SELECT DISTINCT simhash1 FROM bibtex ORDER BY rand() LIMIT 100000";
	my $sth = $db->query($statement);
	while (my @row = $sth->fetchrow_array ) {	
	  	my $hash = $row[0];
	  	my $query = "posts?resourcetype=bibtex&resource=$hash";
	  	process($query, "getBibtexByHash");
	}
}


#
# process a URL
#
sub process {
	my ($query, $query_class, $numPosts) = @_;
	
	print "processing request " . $query . "\n";
	
  	$sw->start;
	$cl->get($apiUrl . $query);
	my $elapsed = $sw->stopRetrieveTime;  
		
	if ($cl->is_success) {			
		
		$log->log($host,$query,$query_class,$cl->status,0,'','',$elapsed,$numPosts,$cl->result->content_length,$num_parallel,$hostname);
		
#
#       we won't parse the result for now, as we're solely interested in the web server performance
#		
#		$xp->parse($cl->content);
#		if ($xp->is_success) {
#			$log->log($host,$query,$query_class,$cl->status,0,'','',$elapsed,$xp->numPosts,$cl->result->content_length,$num_parallel,$hostname);
#			# print "query successful: " . $xp->numPosts . " posts, time: $elapsed\n\n";
#		}
#		else {
#			# handle xml processing error
#			# print $xp->errorMsg
#			$log->log($host,$query,$query_class,$cl->status,1,$xp->error,$xp->errorMsg,$elapsed,0,$cl->result->content_length,$num_parallel,$hostname);
#		};		
	} 
	else {
		# handle http client error
		$log->log($host,$query,$query_class,$cl->status,1,$cl->error,$cl->errorMsg,$elapsed,$numPosts,0,$num_parallel,$hostname);
	}		
}