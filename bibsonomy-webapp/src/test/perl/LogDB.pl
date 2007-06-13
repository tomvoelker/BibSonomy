#
# Database Access 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31


# MODULES
# database connection
use DBI;
use strict;

# class Logdb
package LogDB;

#               
# constructor
#
sub new {
	my ($class) = @_;
    my $self = {
    	_cstr      => "DBI:mysql:apitest",			# connection URL
    	_dbUser    => "root",						# database username
    	_dbPass    => "", 							# database password
        _dbh  	   => undef,						# DBI object
        _sth       => undef,						# statement handle
        _res       => undef							# result
    };
    bless $self, $class;
    $self->connect;
    return $self;
}

#
# connect to dababase
#
sub connect {
	my ($self) = @_;
	$self->{_dbh} = DBI->connect( $self->{_cstr}, $self->{_dbUser}, $self->{_dbPass} );		
	$self->{_dbh} || &error( "DBI connect failed : ", $self->{_dbh}->errstr );
};

#
# post a query and return the result
#
sub log {
	my ($self,$host,$query,$query_class,$http_status,$error,$error_type,$error_msg,$elapsed,$num_posts,$content_length,$num_parallel) = @_;	
	
	$host = $self->{_dbh}->quote($host);
	$query= $self->{_dbh}->quote($query);
	$query_class = $self->{_dbh}->quote($query_class);
	$error_type = $self->{_dbh}->quote($error_type);
	$error_msg = $self->{_dbh}->quote($error_msg);
	
	my $statement = 
			"INSERT INTO results " . 
					 "(host,query,query_class,http_status,error,error_type,error_msg,elapsed,num_posts,content_length,num_parallel) " .  
			"VALUES ($host,$query,$query_class,$http_status,$error,$error_type,$error_msg,$elapsed,$num_posts,$content_length,$num_parallel)";
			
	$self->{_sth} = $self->{_dbh}->prepare($statement)
  		or die "Can't prepare $statement: " . $self->{_dbh}->errstr . "\n";
  		
	$self->{_rv} = $self->{_sth}->execute
  		or die "can't execute the query: " . $self->{_sth}->errstr . "\n";
  	return $self->{_sth};
}

1;