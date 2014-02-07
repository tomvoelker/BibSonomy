DROP TABLE IF EXISTS `sync`;
CREATE TABLE `sync`(
  `user_name` varchar(30) NOT NULL,
  `service_id` int(10) unsigned NOT NULL,
  `credentials` text NOT NULL default '',
   PRIMARY KEY  (`service_id`, `user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sync_services`;
CREATE TABLE `sync_services` (
  `uri` varchar(255) NOT NULL,
  `service_id` int(10) unsigned NOT NULL,
  `server` tinyint(1) NOT NULL,
  PRIMARY KEY  (`service_id`),
  UNIQUE KEY `uri` (`uri`,`server`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sync_data`;
CREATE TABLE `sync_data`(
  `service_id` int(10) unsigned NOT NULL,
  `user_name` varchar(30) NOT NULL default '',
  `content_type` tinyint(1) unsigned default NULL,
  `last_sync_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `status` varchar(255) default NULL,
   PRIMARY KEY  (`service_id`, `user_name`, `content_type`, `last_sync_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;