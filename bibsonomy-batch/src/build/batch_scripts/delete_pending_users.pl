#!/usr/bin/perl
#
# This script deletes users which did non register within 24 hours
# from the "pendingUser" table
# 
# Environment variables:
#   DB_PASS
#
# Changes:
#   2011-06-28: (rja)
#   - using Common.pm now
#   2011-06-27: (rja)
#   - adopted for joe->gandalf switch (socket, port, etc.)
#   2010-07-26: (rja)
#   - initial version
#
use DBI();
use English;
use strict;
use Common qw(debug get_slave get_master check_running);

# don't run twice
check_running();

# connect to database
my $master = get_master();
my $stm_delete_pending_users = $master->prepare("DELETE FROM `pendingUser` WHERE (NOW() > DATE_ADD(reg_date, INTERVAL 24 HOUR))");
$stm_delete_pending_users->execute();
$master->commit();
$master->disconnect();
