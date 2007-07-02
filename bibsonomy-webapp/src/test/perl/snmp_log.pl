#
# Log script for some benchmark parameters on gromit
#
# Dominik Benz, benz@cs.uni-kassel.de, 2007/06/19
#

use strict;
use POSIX;

my $statement = '';
my $timestamp = strftime("%Y-%m-%d %H:%M:%S", localtime);

# loop infinitely
while (1)  {	
	
	# 1 min load average 
	my $val = `snmpget -v3 -u cacti -l authPriv -A c4ct1_snmp -X c4ct1_snmp gromit .1.3.6.1.4.1.2021.10.1.3.1`;
	$val =~ /.*\ (.*)$/g;
	my $loadAvg = $1;
	
	# user CPU time
	$val = `snmpget -v3 -u cacti -l authPriv -A c4ct1_snmp -X c4ct1_snmp gromit .1.3.6.1.4.1.2021.11.50.0`;
	$val =~ /.*\ (.*)$/g;
	my $userCpuTime = $1;

	# system CPU time
	$val = `snmpget -v3 -u cacti -l authPriv -A c4ct1_snmp -X c4ct1_snmp gromit .1.3.6.1.4.1.2021.11.52.0`;
	$val =~ /.*\ (.*)$/g;
	my $sysCpuTime = $1;

	# total RAM free 
	$val = `snmpget -v3 -u cacti -l authPriv -A c4ct1_snmp -X c4ct1_snmp gromit .1.3.6.1.4.1.2021.4.6.0`;
	$val =~ /.*\ (.*)$/g;
	my $totalRamFree= $1;
	
	# total SWAP free
	$val = `snmpget -v3 -u cacti -l authPriv -A c4ct1_snmp -X c4ct1_snmp gromit .1.3.6.1.4.1.2021.4.4.0`;
	$val =~ /.*\ (.*)$/g;
	my $totalSwapFree = $1;
	
	# log the values once each second
	if (!($timestamp eq strftime("%Y-%m-%d %H:%M:%S", localtime))) {
		$timestamp = strftime("%Y-%m-%d %H:%M:%S", localtime);
		$statement = "UPDATE results SET sys_load=$loadAvg, ram_free=$totalRamFree, swap_free=$totalSwapFree WHERE timestamp='$timestamp';\n";
		open( FH, ">> ./log/snmp_log_gromit.sql" )    
		  || die ("can't open file: ./log/snmp_log_gromit.sql \n");
		print FH $statement;
		close(FH);
	}
}
