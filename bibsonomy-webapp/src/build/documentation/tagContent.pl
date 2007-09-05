#!/uva/bin/perl
#
use DBI;
$db="spielwiese";
$host="bugs";
$port="3306";
$userid="bibsonomy";
#$passwd="";
$connectionInfo="DBI:mysql:database=$db;$host:$port";

# make connection to database
$dbh = DBI->connect($connectionInfo,$userid,$passwd);

# prepare and execute query
# $query = "SELECT t.tag_id, b.book_url_hash FROM tags t, bookmark b, tas WHERE tas.content_type=1 AND t.tag_name=tas.tag_name AND b.content_id=tas.content_id AND tas.date<'2004-12-01' ORDER BY b.book_url_hash";
$query = "SELECT t.tag_id, b.book_url_hash FROM tags t, bookmark b, tas WHERE tas.content_type=1 AND t.tag_name=tas.tag_name AND b.content_id=tas.content_id ORDER BY b.book_url_hash";
$sth = $dbh->prepare($query);
$sth->execute();

# assign fields to variables
$sth->bind_columns(undef, \$tag_id, \$url_hash);

# output computer list to the browser
$done = not $sth->fetch();
while (not $done) {
	$last_url_hash = $url_hash;
	my %hash;
	while ($last_url_hash eq $url_hash and not $done) {
		
#		print "$url_hash $tag_id\n";
		if ($hash{$tag_id}) {
			$hash{$tag_id}++;
		} else {
			$hash{$tag_id} = 1;
		}
		
		$done = not $sth->fetch();
		if (not ($last_url_hash eq $url_hash) || $done) {
			foreach $key (keys %hash) {
				# print "$key $last_url_hash $hash{$key}\n";
				my $insertQuery = "INSERT INTO TagContent (tag_id,hash,ctr) VALUES($key, '1$last_url_hash', $hash{$key})";
#				print $insertQuery."\n";
				my $insert = $dbh->prepare($insertQuery);
				$insert->execute();
			}
		}
	}
	print "$last_url_hash done.\n";
};

#%hash = {
#	"test" => "bla",
#	"test2" => "bla2",
#};

# foreach $key (keys %hash) {
#	print $key." => ".$hash{$key}{'tag_id'};
#	print $hash{$key}{'tag_id'}." ".$hash{$key}{'book_url_hash'}." ".$hash{$key}{'ctr'}."\n";
#	$query = "INSERT DELAYED INTO TagBookmark (tag_id,book_url_hash,ctr) VALUES(".$hash{$key}{'tag_id'}.", '".$hash{$key}{'book_url_hash'}."', ".$hash{$key}{'ctr'}.")";
#	print $query;
#	$sth = $dbh->prepare($query);
#	$sth->execute();
#}

$sth->finish();

# prepare and execute query
# $query = "SELECT t.tag_id, b.bib_hash FROM tags t, bibtex b, tas WHERE tas.content_type=2 AND t.tag_name=tas.tag_name AND b.content_id=tas.content_id AND tas.date<'2004-12-01' ORDER BY b.bib_hash";
$query = "SELECT t.tag_id, b.bib_hash FROM tags t, bibtex b, tas WHERE tas.content_type=2 AND t.tag_name=tas.tag_name AND b.content_id=tas.content_id ORDER BY b.bib_hash";
$sth = $dbh->prepare($query);
$sth->execute();

# assign fields to variables
$sth->bind_columns(undef, \$tag_id, \$bib_hash);

# output computer list to the browser
$done = not $sth->fetch();
while (not $done) {
	$last_bib_hash = $bib_hash;
	my %hash;
	while ($last_bib_hash eq $bib_hash and not $done) {
		
#		print "$url_hash $tag_id\n";
		if ($hash{$tag_id}) {
			$hash{$tag_id}++;
		} else {
			$hash{$tag_id} = 1;
		}
		
		$done = not $sth->fetch();
		if (not ($last_bib_hash eq $bib_hash) || $done) {
			foreach $key (keys %hash) {
				# print "$key $last_url_hash $hash{$key}\n";
				my $insertQuery = "INSERT INTO TagContent (tag_id,hash,ctr) VALUES($key, '2$last_bib_hash', $hash{$key})";
#				print $insertQuery."\n";
				my $insert = $dbh->prepare($insertQuery);
				$insert->execute();
				$insert->finish();
			}
		}
	}
	print "$last_bib_hash done.\n";
};

#%hash = {
#	"test" => "bla",
#	"test2" => "bla2",
#};

# foreach $key (keys %hash) {
#	print $key." => ".$hash{$key}{'tag_id'};
#	print $hash{$key}{'tag_id'}." ".$hash{$key}{'book_url_hash'}." ".$hash{$key}{'ctr'}."\n";
#	$query = "INSERT DELAYED INTO TagBookmark (tag_id,book_url_hash,ctr) VALUES(".$hash{$key}{'tag_id'}.", '".$hash{$key}{'book_url_hash'}."', ".$hash{$key}{'ctr'}.")";
#	print $query;
#	$sth = $dbh->prepare($query);
#	$sth->execute();
#}

$sth->finish();

# disconnect from database
$dbh->disconnect; 
