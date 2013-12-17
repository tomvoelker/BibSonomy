#!/usr/bin/perl

# Compute the similarity between pairs of users and updates the database;
# Different choices of tagcloud-based similarity measures are implemented,
#    to be set in variable $similarityMethodType
#
# Mi 01 Apr 2009 17:39:35 CEST 
#  by 
#   VITUS
# Changes:
# 2013-03-27 (rja)
# - support for several arguments which are handled in a loop
# 2011-06-28 (rja)
# - using Common.pm

# mysql queries
#
## retrieve all users with at least n tas
# select user_name, count(tas_id) as count from tas where `group` >= 0 and user_name <> 'dblp' group by user_name having count > $n;
## fetch tagcloud of user u
# select user_name, tag_name, count(tas_id) as count from tas where user_name='$u' group by tag_name order by count desc
## fetch global tag frequencies of top n tags
# select tag_name, tag_ctr_public from tags where tag_name <> 'dblp' order by tag_ctr_public desc limit $n
## insert data into result table (user u1, user u2, similarity sim)
# insert into useruser_similarity values ('$u1', '$u2', $sim)


use DBI();
use strict;
use Data::Dumper;
use English;
use DBI qw(:utils);
use Common qw(debug get_slave get_master check_running);

if ($#ARGV < 0) {
  print "USAGE:\n perl $PROGRAM_NAME SIMILARITY_MEASURE...\n";
  exit;
} 

check_running();

debug("\n###### started script \"batch_contextuser_masterslave.pl\" ######");

# define hash with all possible similarity values + their ids (as defined
# in the table useruser_similarity_measures)
my %similarityMethods = ("jaccard" => 0, "cosine" => 1, "tfidf" => 2);

#######################################################
# configuration
#######################################################

# usefull vars
my $min_users_tas = 50; # select active users as those with more than $min_users_tas Tag ASsignments 
my $sim_count_users = 10; # max nr of most similar user2 to be stored per fixed user1

#######################################################
# loop over all similarity measures given as argument
#######################################################
my %TagGlobalIDF; # this will be used in the tfidf subroutine as global variable
foreach my $similarityMethodType (@ARGV) {

    # possible values: jaccard (0), cosine (1), tfidf (2)
    if (not exists($similarityMethods{$similarityMethodType})) {
	print "invalid similarity type; supported are 'jaccard', 'cosine' or 'tfidf'.\n";
	exit;
    }
    my $similarityId = $similarityMethods{$similarityMethodType};
    debug("Going to use $similarityMethodType similarity measure.\n");

    #######################################################
    # SLAVE
    #######################################################
    # connect
    debug("Database SLAVE connection in starting");

    my $slave = get_slave();#, "transaction-isolation" => "READ-UNCOMMITTED"});
    # prepare statements
    # get all non-spammer users
    #my $stm_select_tagtag =$slave->prepare("SELECT t1 collate utf8_bin , t2 collate utf8_bin , ctr_public  FROM tagtag force index (t1_ctr_public_idx) where ctr_public>0 order by t1 ");

    my $stm_select_users = $slave->prepare("select user_name collate utf8_bin, count(tas_id) as count from tas where `group` >= 0 and user_name <> 'dblp' group by user_name having count > ?"); 
    $stm_select_users->{"mysql_use_result"} = 1;

    # get user's tag cloud
    #my $stm_select_toptag =$slave->prepare("select lower(tag_name),sum(tag_ctr_public) as ctr from tags group by lower(tag_name) having ctr > ?");

    my $stm_select_user_tagcloud = $slave->prepare("select user_name collate utf8_bin, tag_name collate utf8_bin, count(tas_id) as count from tas where user_name=? group by tag_name");
    $stm_select_user_tagcloud->{"mysql_use_result"} = 1;

    # get tag occurrences
    my $stm_select_tagocc = $slave->prepare("select tag_name, tag_ctr_public from tags where tag_name <> 'dblp' and tag_ctr_public > 0"); # order by tag_ctr_public desc");
    $stm_select_tagocc->{"mysql_use_result"} = 1;


    #######################################
    # get non spammer users

    debug("Getting non-spam Users from database having at least $min_users_tas TAS ...");
    $stm_select_users->execute($min_users_tas);

    my @Users = ();

    while (my @row = $stm_select_users->fetchrow_array ) {       
	push @Users, lc($row[0]);
    }

    debug("Got ",1+$#Users);

    #######################################
    # get User's tagclouds 

    my %TagClouds=();
    debug("Going to fetch Users tagclouds ...");

    { 
	my ($ucount,$tascount)=(0,0);
	for my $u (@Users) {
	    $stm_select_user_tagcloud->execute($u);
	    while (my @row = $stm_select_user_tagcloud->fetchrow_array ) {
		$TagClouds{$u}{lc($row[1])}=$row[2];
		++$tascount;
	    }
	    ++$ucount;
	    debug("Got $ucount Users and $tascount TAS, till now") unless($ucount%100);
	}
	debug("Finally got $ucount Users and $tascount TAS.");
    }
    
    @Users =(); # we do not need it anymore: free memory.


    #######################################
    # get tag overall occurrences and convert into IDF = -log2(freq)

    %TagGlobalIDF = (); # this is used in the tfidf subroutine as global variable
    my $TotalTagCount=0;
    
    $stm_select_tagocc->execute();
    debug("Getting the number of occurrences for ALL tags");
    
    {
	my $tcount = 0;
	while (my @row = $stm_select_tagocc->fetchrow_array ) {
	    $TotalTagCount += ($TagGlobalIDF{$row[0]}=$row[1]);
	    $tcount++;
	}
	debug("Got $tcount different tags; the overall number of tags is $TotalTagCount (must be larger of above TAS nr).");
    }
    
    while ( my ($tag, $occ) = each %TagGlobalIDF) {
	$TagGlobalIDF{$tag} = &ALog2($TotalTagCount/$occ);
    }
    

    # we do not need DB queries anymore
    $slave->commit;

    $slave->disconnect;
    debug("Database queries ended.\n");

    my %UserSimilarity = ();

    #######################################
    # calculate User's similarity 
    {
	my $ucount;
	debug("Calculating Users similarity");
	for my $u1 (keys %TagClouds) {
	    for my $u2 (keys %TagClouds) {
		if(defined $UserSimilarity{$u2}{$u1}) {
		    $UserSimilarity{$u1}{$u2} = $UserSimilarity{$u2}{$u1}; # symmetry
		    next;
		} else {
		    $UserSimilarity{$u1}{$u2} = &MeasureSimilarity(\%{$TagClouds{$u1}}, \%{$TagClouds{$u2}},  $similarityMethodType);
		}
	    }
	    ++$ucount;
	    debug("Done for $ucount Users till now") unless($ucount%100);
	}
	debug("User similarity calculated for $ucount Users.");
    }
    
    %TagClouds=(); # free memory
    
    ######################################################
    # MASTER
    ######################################################
    # connect
    debug("Database MASTER connection is starting");
    
    my $master = get_master();
    # prepare
    #

    my $stm_delete_old_entries =  $master->prepare("delete from useruser_similarity2 where measure_id = ?");
    
    my $stm_insert_contextuser = $master->prepare("INSERT INTO useruser_similarity2 values (?, ?, ?, ?)");
    
    my $stm_rename_tabs = $master->prepare("rename table useruser_similarity to useruser_similarity_old,  useruser_similarity2 to useruser_similarity,  useruser_similarity_old to useruser_similarity2");
    
    # delete all entries of the current table
    
    $stm_delete_old_entries->execute($similarityId);
    debug("Cleaning \"useruser_similarity2\" table");
    $master->commit;
    
    {
	my $commit_count = 0;
	my $commit_per_user;
	debug("Storing User similarities in database");
	
	for my $u1 (keys %UserSimilarity ) {
	    $commit_per_user=0;
	    for my $u2 (sort {$UserSimilarity{$u1}{$b} <=> $UserSimilarity{$u1}{$a}} keys %{$UserSimilarity{$u1}}) {
		$stm_insert_contextuser->execute($u1,$u2,$UserSimilarity{$u1}{$u2},$similarityId);
		if (++$commit_count % 1000 == 0) {
		    $master->commit;
		    debug("Stored $commit_count similarities till now");
		}
		last if(++$commit_per_user > $sim_count_users); #restrict to only the most similar
	    }
	}
    }
    $master->commit;
    debug("Built new \"useruser_similarity2\" table");
    

    # rename old and new

    $stm_rename_tabs->execute();

    $master->commit;

    # disconnect database
    $master->disconnect();

}

debug("###### end of script \"batch_contextuser_masterslave.pl\" ######\n");


#################################
# subroutines
#################################


sub MeasureSimilarity {

	my ($tc1, $tc2, $similarityMethodType) = @_; # be careful: these are references to hashes: $$tc{TAG}=nr_of_TAS_in_user_personomy_containing_that_TAG

	return 1.0 if ($tc1 == $tc2); # obvious ...

	if ($similarityMethodType eq "jaccard") {
		return &Jaccard($tc1,$tc2);
	} elsif($similarityMethodType eq "tfidf") {
		return &TfIdf($tc1,$tc2);
	} 

	# default
	return &Cosine($tc1,$tc2);
}

sub Jaccard {
# given two tagclouds A and B returns: |A intersection B| / |A union B| 
	my ($tc1, $tc2) = @_;
	my ($tag,$occ1,$occ2);
	my ($union,$intersection)=(0,0);
	my ($maxtagocc,$mintagocc);

	while( ($tag,$occ1)=each %$tc1) {
		$maxtagocc = $occ1;
		$mintagocc = 0;
		if(defined $$tc2{$tag}) {
			$occ2 = $$tc2{$tag};
			$maxtagocc = (($occ1>$occ2)? $occ1: $occ2);
			$mintagocc = (($occ1<$occ2)? $occ1: $occ2);
		}
		$union += $maxtagocc;
		$intersection += $mintagocc;
	}
	# complete the union with those tags non present in user1's tagcloud
	while( ($tag,$occ2)=each %$tc2) {
		$union += $occ2 unless (defined $$tc1{$tag});
	}
	return $intersection/$union; # double...
}

sub Cosine {
# given two tagclouds A and B returns: A*B / |A|*|B| with L2 norm
	my ($tc1, $tc2) = @_;
	my $cosine=0;
	my ($norm1,$norm2)=(0,0);
	my ($occ1,$occ2);
	my $tag;

	while( ($tag,$occ1)=each %$tc1) {
		$occ2=0;
		if(defined $$tc2{$tag}) { $occ2=$$tc2{$tag}; }
		$cosine += $occ1*$occ2;
		$norm1 += $occ1*$occ1;
	}
	while( ($tag,$occ2)=each %$tc2) {
		$norm2 += $occ2*$occ2;
	}
	return $cosine/sqrt($norm1*$norm2);
}

sub TfIdf {
# calculates the cosine similarity between two users' tagclouds
# with tag vectors containing the TF*IDF value of tags
# the TF term is the frequency of that tag inside a user personomy
# the IDF term is the base 2 logarithm of the reciprocal value of that tag global frequency 

	# uses %TagGlobalIDF as global variable
	my ($tc1, $tc2) = @_;
	my ($u2tottags,$u1tottags)=(0,0); # total nr of tas in user
	my ($tag,$occ);
	my ($occ1,$occ2); # tag occurrence
	my ($tf1,$tf2); # tag term-frequency
	my $cosine=0;
	my ($norm1,$norm2)=(0,0);
	my $idf;

	while( ($tag,$occ)=each %$tc1) { $u1tottags += $occ; }
	while( ($tag,$occ)=each %$tc2) { $u2tottags += $occ; }

	while( ($tag,$occ1)=each %$tc1) {
		$occ2=0;
		if(defined $$tc2{$tag}) { $occ2=$$tc2{$tag}; }
		$idf = $TagGlobalIDF{$tag};
		$tf1 = $occ1/$u1tottags;
		$tf2 = $occ2/$u2tottags;
		$cosine += $tf1*$tf2*$idf*$idf;
		$norm1 += $tf1*$tf1*$idf*$idf;
	}
	while( ($tag,$occ2)=each %$tc2) {
		$tf2 = $occ2/$u2tottags;
		$idf = $TagGlobalIDF{$tag};
		$norm2 += $tf2*$tf2*$idf*$idf;
	}
	if ($norm1*$norm2 == 0) {
		return 0;
	}
	return $cosine/sqrt($norm1*$norm2);	
}

sub ALog2 {
# approximate fast calculation of the base 2 logarithm
	my $n = pop;
	my $log2=0;
	my $sgn=1;

	return -9999999 if($n<=0); # should throw an exception, though.
	if($n<1) {
		$n=1.0/$n;
		$sgn =-1;
	}
	$n = int($n);
	++$log2 while($n>>=1);
	return $sgn*$log2;
}


