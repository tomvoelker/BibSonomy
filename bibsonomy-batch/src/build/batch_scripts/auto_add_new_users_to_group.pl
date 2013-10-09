#!/usr/bin/perl
##############
#
# This script adds new users to the group specified by the command
# line argument (id of the group)
#
# Expected things:
#   MASTER config variables set @see Common.pm
#
# Changes:
#   2013-08-09: (dzo)
#   - initial version
#

use strict;
use Data::Dumper;
use English;
use Common qw(debug get_master check_running);

check_running();

my $group = $ARGV[0];

my $master = get_master();

my $insert_group_members = $master->prepare("INSERT INTO groups SELECT user_name, ?, ?, NULL, NULL, 0 FROM user WHERE user_name NOT IN (SELECT user_name FROM groups WHERE `group` = ?) AND role != 0");
$insert_group_members->execute($group, $group, $group);

$master->commit();

$master->disconnect();
