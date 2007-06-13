#
# HTTP Client
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31


# MODULES
# database connection
use MIME::Base64; 
use LWP;
use strict;

# class db
package HTTPClient;
               
# constructor
sub new {
	my ($class, $username, $password) = @_;
    my $self = {
    	_ua			=> LWP::UserAgent->new,  
    	_cred	    => MIME::Base64::encode_base64("$username:$password"),
    	_succ		=> undef,
    	_content 	=> undef,
    	_error		=> undef,
    	_errorMsg 	=> undef,
    	_result		=> undef,
    	_status		=> undef
    };              
    bless $self, $class;
    return $self;
}

# send get request to URL
sub get {
	my ($self, $url) = @_;	
	
	$self->reset;
	my $req = HTTP::Request->new(GET => $url);
	$req->header(Authorization => "Basic $self->{_cred}",
				 Content_Type => 'application/xml');
	my $res;         	
	eval {	
		$res = $self->{_ua}->request($req);
	};		

	# check for errors	
	if ($@) {
		$self->{_succ} = 0;							# general error
		$self->{_error} = "HTTP_CLIENT_ERROR";
		$self->{_errorMsg} = $@;
		$self->{_status} = 0;
	}
	else {
		$self->checkForErrors($res);
	}		
};

# checks a http response for errors
sub checkForErrors {
	my ($self, $res) = @_;
	
	$self->{_status} = $res->code;

	if (!($res->is_success)) {					# server error
		$self->{_succ} = 0;
		$self->{_error} = "SERVER_ERROR";
		$self->{_errorMsg} = $res->status_line;		
	}
	elsif (length($res->content) != $res->content_length) {										
		$self->{_succ} = 0;						# transmission error
		$self->{_error} = "TRANSMISSION_ERROR";
		$self->{_errorMsg} = "Content did not have the size defined in header (expected: "  . $res->content_length . ", received: " . length($res->content) . ")";		
	}
	elsif ($res->header("Client-Aborted")) {
		$self->{_succ} = 0;						# response too large
		$self->{_error} = "MAX_SIZE_ERROR";
		$self->{_errorMsg} = "Response exceeded maximum size.";		
	}
	else { 										# success				
		$self->{_succ} = 1;
		$self->{_content} = $res->content; 
		$self->{_result} = $res;				
	} 		
}

#retrieve content
sub content {
	my ($self) = @_;
	return $self->{_content};
}

#retrieve status
sub status {
	my ($self) = @_;
	return $self->{_status};
}

#retrieve error
sub error {
	my ($self) = @_;
	return $self->{_error};
}

#retrieve result
sub result {
	my ($self) = @_;
	return $self->{_result};
}

#retrieve error message
sub errorMsg {
	my ($self) = @_;
	return $self->{_errorMsg};
}

# success?
sub is_success {
	my ($self) = @_;
	return $self->{_succ};
}

# reset fields
sub reset {
	my ($self) = @_;
	$self->{_succ} = undef;
	$self->{_content} = undef;
	$self->{_error} = undef;
	$self->{_errorMsg} = undef;
	$self->{_result} = undef;
	$self->{_status} = undef;
}

1;