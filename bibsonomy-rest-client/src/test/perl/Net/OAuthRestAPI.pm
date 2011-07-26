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

package Net::OAuthRestAPI;

use strict;
use base qw(Net::OAuth::Simple);


sub new {
    my $class  = shift;
    my %tokens = @_;
    return $class->SUPER::new( tokens => \%tokens, 
			       protocol_version => '1.0',
			       urls   => {
				   authorization_url => 'http://folke.biblicious.org/oauth/authorize',
				   request_token_url => 'http://folke.biblicious.org/oauth/requestToken',
				   access_token_url  => 'http://folke.biblicious.org/oauth/accessToken',
			       });
}

sub view_restricted_resource {
    my $self = shift;
    my $url  = shift;
    return $self->make_restricted_request($url, 'GET');
}

sub update_restricted_resource {
    my $self         = shift;
    my $url          = shift;
    my %extra_params = @_;
    return $self->make_restricted_request($url, 'POST', %extra_params);    
}
1;
