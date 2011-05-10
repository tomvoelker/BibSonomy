#!/usr/bin/perl
###############################################################################
# queries BibSonomy's REST-API using OAuth
###############################################################################

use warnings;
use strict;
use Net::OAuthRestAPI;

#------------------------------------------------------------------------------
sub get_tokens {
#------------------------------------------------------------------------------
    my %tokens = (
	consumer_key    => 'perl-test-client',
	consumer_secret => 'secret'
	);

    return %tokens;
}

# Get the tokens from the command line, a config file or wherever 
my %tokens  = get_tokens(); 
my $app     = Net::OAuthRestAPI->new(%tokens);

# Check to see we have a consumer key and secret
unless ($app->consumer_key && $app->consumer_secret) {
    die "You must go get a consumer key and secret from App\n";
} 

# If the app is authorized (i.e has an access token and secret)
# Then look at a restricted resourse

if ($app->authorized) {
    my $response = $app->view_restricted_resource;
    print $response->content."\n";
    exit;
}

# Otherwise the user needs to go get an access token and secret
print "Go to ".$app->get_authorization_url."\n";
print "Then hit return after\n";
<STDIN>;
my ($access_token, $access_token_secret) = $app->request_access_token;

my $response = $app->view_restricted_resource("http://folke.biblicious.org/api/users/folke");
if ($response->is_success) {
    print $response->decoded_content;
}
else {
    print STDERR $response->status_line, "\n";
}

# Now save those values
