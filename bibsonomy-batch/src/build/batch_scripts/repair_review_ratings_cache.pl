#!/usr/bin/perl
###############
# 
# Environment variables: 
#   see Common.pm
#
# Changes:
#   2011-10-24 (dzo)
#   - initial version
#
use DBI();
use strict;
use English;
use Common qw(debug set_debug get_slave get_master check_running);

# don't run twice ...
check_running();

my $master = get_master();
# lock both tables
#$master->prepare("LOCK TABLES review_ratings_cache WRITE");
#$master->prepare("LOCK TABLES discussion WRITE");

# delete old data
my $stm_delete_old_ratings = $master->prepare("DELETE FROM review_ratings_cache");

# recalc the avg 
my $stm_insert_new_ratings = $master->prepare("INSERT INTO review_ratings_cache (interhash, rating_arithmetic_mean, number_of_ratings) SELECT interHash, avg(rating) as rating_arithmetic_mean, count(DISTINCT(user_name)) as number_of_ratings FROM discussion where type=1 AND `group` >= 0 GROUP BY interHash");

# execute
$stm_delete_old_ratings->execute();
$stm_insert_new_ratings->execute();

# unlock tables
#$master->prepare("UNLOCK TABLES");
$master->commit;
$master->disconnect();
