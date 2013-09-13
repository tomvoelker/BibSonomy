CREATE TABLE `log_document` (
  `hash` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) default '',
  `user_name` varchar(255) default '',
  `date` datetime default '0000-00-00 00:00:00',
  `md5hash` char(32) NOT NULL default '00000000000000000000000000000000',
  `log_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  KEY `content_id_idx` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;