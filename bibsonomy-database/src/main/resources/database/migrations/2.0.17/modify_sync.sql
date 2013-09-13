INSERT INTO `ids` VALUES (16, 0, "synchronization id");

ALTER TABLE sync_data CHANGE COLUMN `status` `status` varchar(8) NOT NULL;
ALTER TABLE sync_data ADD COLUMN `info` varchar(255) default NULL;
ALTER TABLE sync ADD COLUMN `content_type` tinyint(1) unsigned default 0;
ALTER TABLE sync ADD COLUMN `direction` varchar(4) default 'both';