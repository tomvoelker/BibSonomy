CREATE TABLE `person_match`(
  `match_id` int(10) unsigned NOT NULL unique,
  `person1_id` varchar(64) NOT NULL,
  `person2_id` varchar(64) NOT NULL,
  `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'set to 1 if merge is denied, 2 if they are merged',
   PRIMARY KEY  (`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `match_reason`(
  `match_id` int(10) unsigned NOT NULL unique,
  `mode` varchar(4) NOT NULL,
  `item1_id` char(32) DEFAULT NULL COMMENT '(interHash)',
  `item2_id` char(32) DEFAULT NULL COMMENT '(interHash)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_denied_match`(
  `match_id` int(10) unsigned NOT NULL,
  `user_name` varchar(30) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `other_dnb_ids`(
  `dnb_person_id` char(18) NOT NULL,
  `other_dnb_person_id` char(18) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `log_pub_person` ADD `new_change_id` int(10) unsigned;