#!/usr/bin/perl
#############
use Digest::MD5 qw(md5_hex);
require "DB.pl";

#
# zufaelligen Hash erzeugen
#
sub generateKey {
  my $noise = 'ps wwaxuf ; netstat -i ; vmstat ;'; # Rauschen
  my @junk = times();
  my $rand = `$noise` . $$ . $PPID . join('',%ENV) . "@junk" . `ps axww`;
  $rand = md5_hex($rand);
  return $rand;
}

#
# connect to DB
#
my $db = eval { new DB(); }  or die ($@);


#
# retrieve all non-spammer users which do not yet have an API key
#
my $userStmt = "SELECT user_name FROM user WHERE spammer=0 AND api_key IS NULL";
my $userSth = $db->query($userStmt);

my $apiKeyStmt;
my $apiKey = '';
my $username = '';

#
# loop over users and create API keys
#
while (my @row = $userSth->fetchrow()) {
  my $username = $row[0];
  $apiKey = generateKey();
  $statement = "UPDATE user SET api_key='" . $apiKey . "' WHERE user_name='" . $username . "'";
  my $sth = $db->query($statement);
  if ($sth->rows ==  1) {
    print "API key for user " . $username . ": " . $apiKey . "\n";
  }
  else {
    print "No match found for statement: \n" . $statement . "\n ... probably the username doesn't exist.\n";
  }
}
