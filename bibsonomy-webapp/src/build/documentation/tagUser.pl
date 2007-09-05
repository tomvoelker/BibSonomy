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
#$query = "select tag_id, tmp.user_name, tmp.ctr FROM (select user_name,tag_name,count(*) ctr from tas WHERE tas.date<'2004-12-01' group by tag_name,user_name order by tag_name) tmp JOIN tags ON tmp.tag_name=tags.tag_name";
$query = "select tag_id, tmp.user_name, tmp.ctr FROM (select user_name,tag_name,count(*) ctr from tas group by tag_name,user_name order by tag_name) tmp JOIN tags ON tmp.tag_name=tags.tag_name";
$sth = $dbh->prepare($query);
$sth->execute();

# assign fields to variables
$sth->bind_columns(undef, \$tag_id, \$user_name, \$ctr);

# output computer list to the browser
$i = 0;
$values = "";
while ($sth->fetch()) {
	if($i > 0) {
		$values .= ",";
	}
	$values .= "($tag_id,'$user_name',$ctr)";
	if ($i % 1000 == 0) {
		print $values."\n";
		my $insertQuery = "INSERT INTO TagUser (tag_id,user_name,ctr) VALUES$values";
		my $insert = $dbh->prepare($insertQuery);
		$insert->execute();
		$insert->finish();
		$values = "";
	}
};
if (not $values eq "") {
	my $insertQuery = "INSERT INTO TagUser (tag_id,user_name,ctr) VALUES$values";
	my $insert = $dbh->prepare($insertQuery);
	$insert->execute();
	$insert->finish();
	$values = "";
}

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
