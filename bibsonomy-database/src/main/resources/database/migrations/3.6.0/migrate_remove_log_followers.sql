INSERT INTO log_friends (user_name, f_user_name, tag_name, friendship_date) (SELECT user_name, f_user_name, "sys:relation:Bibsonomy-Follower", fellowship_date FROM log_followers);
