#
# Database Access 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31


# MODULES
# database connection
use XML::XPath;
use XML::XPath::XMLParser;

# class db
package XMLProc;
               
# constructor
sub new {
	my ($class) = @_;
    my $self = {
    	_succ		=> undef,    
    	_error 		=> undef,
    	_errorMsg 	=> undef,
    	_xp			=> undef
    };              
    bless $self, $class;
    return $self;
}

# send get request to URL
sub parse {
	my ($self, $xml) = @_;
	$self->reset;
	eval {				
		my $p = XML::XPath::XMLParser->new(xml => $xml);
		$p->parse; 	# test parse
	};
	if ($@) { # error occurred
		$self->{_succ} = 0;
		$self->{_error} = "PARSE_ERROR";
		$self->{_errorMsg} = $@;		
	}	
	else {
		$self->{_succ} = 1;
		$self->{_xp} = XML::XPath->new(xml => $xml);
	}
};

# successfully parsed xml?
sub is_success {
	my ($self) = @_;
	return $self->{_succ};
}

# number of posts
sub numPosts {
	my ($self) = @_;
	my $nodeset = $self->{_xp}->find("//post");
	return $nodeset->size;
}

# reset parser 
sub reset {
	my ($self) = @_;
	$self->{_succ} = undef;
	$self->{_error} = undef;
	$self->{_errorMsg} = undef;
	$self->{_xp} = undef;	
}

# error
sub error {
	my ($self) = @_;
	return $self->{_error};
}

# error message
sub errorMsg {
	my ($self) = @_;
	return $self->{_errorMsg};
}


1;