UPDATE friends SET tag_name = "sys:network:bibsonomy-follower" WHERE tag_name = "sys:relation:Bibsonomy-Follower";
UPDATE log_friends SET tag_name = "sys:network:bibsonomy-follower" WHERE tag_name = "sys:relation:Bibsonomy-Follower";

INSERT IGNORE INTO friends (user_name, f_user_name, tag_name, friendship_date) (SELECT user_name, f_user_name, "sys:network:bibsonomy-follower", fellowship_date FROM followers);
INSERT IGNORE INTO log_friends (user_name, f_user_name, tag_name, friendship_date, friendship_end_date) (SELECT user_name, f_user_name, "sys:network:bibsonomy-follower", fellowship_date, fellowship_end_date FROM log_followers);