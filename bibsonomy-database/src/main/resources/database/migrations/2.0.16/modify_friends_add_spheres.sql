ALTER TABLE friends ADD COLUMN f_network_user_id int(10);
ALTER TABLE friends ADD COLUMN tag_name varchar(255) default "sys:network:bibsonomy-friend";
ALTER TABLE log_friends ADD COLUMN tag_name varchar(255) default "sys:network:bibsonomy-friend";
ALTER TABLE log_friends ADD COLUMN f_network_user_id int(10);