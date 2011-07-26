#!/usr/bin/perl
#
#
#  BibSonomy-Rest-Client - The REST-client.
#
#  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
#                            University of Kassel, Germany
#                            http://www.kde.cs.uni-kassel.de/
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU Lesser General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#

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
