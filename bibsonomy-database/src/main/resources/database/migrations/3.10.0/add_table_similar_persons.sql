CREATE TABLE `similar_persons`(
  `match_id` int(10) unsigned NOT NULL unique,
  `person1_id` varchar(64) NOT NULL,
  `person2_id` varchar(64) NOT NULL,
  `sim` float NOT NULL default '0',
  `mode` varchar(4) NOT NULL,
  `item1_id` char(32) DEFAULT NULL COMMENT '(interHash)',
  `item2_id` char(32) DEFAULT NULL COMMENT '(interHash)',
  `denied` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'set to 1 if merge is denied',
   PRIMARY KEY  (`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;