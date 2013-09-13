CREATE TABLE `log_inboxMail` (
  `message_id` int(10) unsigned NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `intraHash` varchar(32) NOT NULL default '',
  `sender_user` varchar(30) NOT NULL,
  `receiver_user` varchar(30) NOT NULL,
  `date` datetime default NULL,
  `content_type` tinyint(1) unsigned,
  `log_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;