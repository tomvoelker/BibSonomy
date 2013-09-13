ALTER TABLE sync_data ADD `device_id` varchar(32) default '';
ALTER TABLE sync_data ADD `device_info` varchar(255) default NULL;
ALTER TABLE sync_data DROP PRIMARY KEY;
ALTER TABLE sync_data ADD PRIMARY KEY ( `service_id`,`user_name`,`content_type`,`last_sync_date`,`device_id`);

ALTER TABLE sync_services ADD `name` varchar(50) DEFAULT NULL;
ALTER TABLE sync_services MODIFY `ssl_dn` varchar(255) DEFAULT NULL;