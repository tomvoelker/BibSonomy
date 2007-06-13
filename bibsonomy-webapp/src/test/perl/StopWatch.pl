#
# StopWatch
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31

# exact timing
use Time::HiRes::Value;

# class StopWatch
package StopWatch;
               
# constructor
sub new {
	my ($class, $username, $password) = @_;
    my $self = {
    	_start 	=> undef
    };              
    bless $self, $class;
    return $self;
}

# start timing
sub start {	
	my ($self) = @_;
	$self->{_start} = Time::HiRes::Value->now();		
};

# stop timing and return elapsed time
sub stopRetrieveTime {
	my ($self) = @_;
	return Time::HiRes::Value->now() - $self->{_start};		
}

1;