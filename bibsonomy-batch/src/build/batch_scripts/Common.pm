package Common;

require Exporter;
@ISA = qw(Exporter);
@EXPORT_OK = qw(check_running debug set_debug get_slave get_master);

use warnings;
use strict;
use English;
use DBI;

##################################################################
# This module contains common subroutines used by our batch 
# scripts.
#
# Changes:
# 2011-06-27 (rja)
# - initial version
# - copied methods am_i_running(), and debug()
# - added methods check_running(), set_debug()
# - added variable $DEBUG
##################################################################

my $DEBUG = 0; # 1 = on, 0 = off
my @ENV_VARS = ("DB", "HOST", "PORT", "SOCK", "USER", "PASS");



#
# returns a connection using the environment variables 
# SLAVE_HOST, SLAVE_PORT, SLAVE_SOCK, SLAVE_USER, SLAVE_PASS, SLAVE_DB
#
sub get_slave {
    return get_connection("SLAVE");
}

#
# returns a connection using the environment variables 
# MASTER_HOST, MASTER_PORT, MASTER_SOCK, MASTER_USER, MASTER_PASS, MASTER_DB
#
sub get_master {
    return get_connection("MASTER");
}

#
# returns a connection using the configured environment variables
# 
# arguments:
#   host - name of the host prefix for the environment variables
#          (either "MASTER" or "SLAVE")
#
sub get_connection {
    my $host = shift;
    return DBI->connect(
	"DBI:mysql:" . 
	"database=" . $ENV{$host . "_DB"} . ";" . 
	"host=" . $ENV{$host . "_HOST"} . ":" . $ENV{$host . "_PORT"} . ";" .
	"mysql_socket=" . $ENV{$host . "_SOCK"}, 
	$ENV{$host . "_USER"},
	$ENV{$host . "_PASS"}, 
	{
	    RaiseError => 1, 
	    AutoCommit => 0, 
	    "mysql_enable_utf8" => 1, 
	    "mysql_auto_reconnect" => 1
	}
	);
}


sub check_running {
    my $name = ${PROGRAM_NAME};
    $name =~ s|.*/||;
    if (am_i_running($ENV{'TMP'}."/${name}.pid")) {
	print STDERR "another instance of $PROGRAM_NAME is running on $ENV{'hostname'}. Aborting this job.\n";
	exit;
    }
}


# INPUT: location of lockfile
# OUTPUT: 1, if a lockfile exists and a program with the pid inside 
#            the lockfile is running
#         0, if no lockfile exists or the program with the pid inside 
#            the lockfile is NOT running; resets the pid in the pid 
#            to the current pid
sub am_i_running {
  my $LOCKFILE = shift;
  my $PIDD;

  if (open (FILE, "<$LOCKFILE")) {
    while (<FILE>) {
      $PIDD = $_;
    }
    close (FILE);
    chomp($PIDD);

    if (kill(0,$PIDD)) {
      return 1;
    }
  }
  open (FILE, ">$LOCKFILE") or die "Could not open lockfile $LOCKFILE: $!\n";
  print FILE $$;
  close (FILE);
  return 0;
}

# 1 = on, 0 = off
sub set_debug {
    $DEBUG = shift;
}

# logs statements if debugging is enabled
sub debug {
    my $msg = shift;
    if ($DEBUG) {
	my $date = `date +"%Y-%m-%d %H:%M:%S"`;
	print STDERR "$date: $msg\n";
    }
}

1;
