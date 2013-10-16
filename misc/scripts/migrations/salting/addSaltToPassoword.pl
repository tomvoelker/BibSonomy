#!/usr/bin/perl
#############
# This scripts generates the password salt for every user in BibSonomy and updates the
# password with the salted version of the password

use DBI();
use strict;
use English;
use Digest::MD5 qw(md5_hex);
use Common qw(get_master);

use constant SALT_LENGTH => 16;
#
# generate "random" salt
#
sub generateSalt {
  my @set = ('0' ..'9', 'a' .. 'f');
  my $str = join '' => map $set[rand @set], 1 .. SALT_LENGTH;
return $str; 
}

sub addSalt {
	my $mytable = $_[0];
	my $mydb = $_[1];
	my $statment = $mydb->prepare("SELECT user_name, user_password FROM " . $mytable . " WHERE user_password != 'inactive'");
	my $updateStatment = $mydb->prepare("UPDATE " . $mytable . " SET user_password = ?, user_password_salt = ? WHERE user_name = ?");
	$statment->execute();
	$mydb->commit();
	#
	# loop over users and create salt and update password
	#
	while (my @row = $statment->fetchrow_array()) {
	  my $username = $row[0];
		my $password = $row[1];
	        my $passwordSalt = generateSalt();
                my $newPassword = md5_hex($password . $passwordSalt);
		$updateStatment->execute($newPassword, $passwordSalt, $username);
		if ($updateStatment != 1) {
		   # print "can't update password for user '" . $username . "' in table '" . $mytable . "'";
		}
	}
	
	$mydb->commit();
}

#
# connect to DB
#
my $db = get_master();

#
# retrieve all users
#
addSalt('pendingUser', $db);
addSalt('user', $db);

$db->disconnect();
