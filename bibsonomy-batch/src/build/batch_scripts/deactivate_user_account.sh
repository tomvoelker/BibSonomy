#!/bin/sh

#
# Prints the SQL statements necessary to delete a user account.
#
# You can use this script to pipe the statements directly into
# mysql.
#
# NOTE: The script does only what I assumed should be done from
#       reading the (somehow complicated) Java source code of 
#       the deleteUser() method and subsequent methods. I might
#       miss a table which should be updated.
# 
# NOTE: The script does not refuse to delete users which are a
#       group! This is a serious bug! Don't use it to delete 
#       groups!
# 
# Changes:
# 2009-09-04 (rja)
# - initial version
#

# user name as first arg
USERNAME=$1
NOW=`date +"%Y-%m-%d %H:%M:%S"`

# test if user name given
if [ ! $USERNAME ]; then
    echo "usage:"
    echo "  $0 USERNAME"
    exit
fi


#
# FIXME: missing: check for group
#


# update user table
cat <<EOF
UPDATE user 
  SET 
    user_password = "inactive",
    role          = 3,
    spammer       = 1,
    updated_by    = "on_delete",
    updated_at    = "$NOW",
    to_classify   = 0
  WHERE 
    user_name   = "$USERNAME"
;
EOF

# remove user from groups
cat <<EOF
DELETE FROM groups WHERE user_name = "$USERNAME";
EOF

# handle prediction/log_prediction tables
cat <<EOF
INSERT 
  INTO log_prediction (user_name, prediction, timestamp, updated_at, algorithm ,mode, confidence)
  VALUES ("$USERNAME", 1, UNIX_TIMESTAMP(NOW()), "$NOW", "self_deleted", NULL, NULL);
REPLACE 
  INTO prediction (user_name, prediction, timestamp, updated_at, algorithm , mode, evaluator, confidence)
  VALUES ("$USERNAME", 1, UNIX_TIMESTAMP(NOW()), "$NOW", "self_deleted", NULL, 0, NULL);
EOF

# handle post / tag tables
for table in tas grouptas bibtex bookmark search_bibtex search_bookmark; do
    cat <<EOF
UPDATE $table 
  SET $table.group = IF (($table.group >= 0), $table.group - 2147483648, $table.group)
  WHERE user_name = "$USERNAME";
EOF
done
