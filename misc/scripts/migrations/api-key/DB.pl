#
# Database Access 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31


# MODULES
# database connection
use DBI;
use strict;

# class db
package DB;

#               
# constructor
#
sub new {
	my ($class) = @_;
    my $self = {
    	_cstr      => "DBI:mysql:database=bibsonomy_finaltest;host=localhost:3306;mysql_socket=/mnt/raid-db/mysql/run/mysqld.sock",		# connection URL
    	_dbUser    => "bibsonomy",						# database username
    	_dbPass    => "****", 						# database password
        _dbh  	   => undef,						    # DBI object
        _sth       => undef,							# statement handle
        _res       => undef								# result
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
sub query {
	my ($self, $statement) = @_;
	$self->{_sth} = $self->{_dbh}->prepare($statement)
  		or die "Can't prepare $statement: " . $self->{_dbh}->errstr . "\n";
  		
	$self->{_rv} = $self->{_sth}->execute
  		or die "can't execute the query: " . $self->{_sth}->errstr . "\n";
  	return $self->{_sth};
}

sub dbh {
	my ($self) = @_;
	return $self->{_dbh};
}

1;
