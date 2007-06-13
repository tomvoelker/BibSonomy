#
# Database Access 
# 
# Dominik Benz, benz@cs.uni-kassel.de
# 2007/05/31


# MODULES
# database connection
use DBI;
use strict;

# class LogFile
package LogFile;

#               
# constructor
#
sub new {
	my ($class, $db) = @_;
    my $self = {  	
		_db		=> $db,    	
    };
    bless $self, $class;
    return $self;
}

#
# post a query and return the result
#
sub log {
	my ($self,$host,$query,$query_class,$http_status,$error,$error_type,$error_msg,$elapsed,$num_posts,$content_length,$num_parallel,$hostname) = @_;	
	
	$host = $self->{_db}->dbh->quote($host);
	$query= $self->{_db}->dbh->quote($query);
	$query_class = $self->{_db}->dbh->quote($query_class);
	$error_type = $self->{_db}->dbh->quote($error_type);
	$error_msg = $self->{_db}->dbh->quote($error_msg);
	
	my $statement = 
			"INSERT INTO results " . 
					 "(host,query,query_class,http_status,error,error_type,error_msg,elapsed,num_posts,content_length,num_parallel) " .  
			"VALUES ($host,$query,$query_class,$http_status,$error,$error_type,$error_msg,$elapsed,$num_posts,$content_length,$num_parallel);";
			
	$self->saveToFile($statement, $hostname . ".log");
}

sub saveToFile {
	my ($self, $content, $filename) = @_;
	# save the insert statement in a file
	open( FH, ">> ./log/$filename" )    
	  || die ("can't open file: " . $filename . "\n");
	print FH $content . "\n";
	close(FH);	
}

1;