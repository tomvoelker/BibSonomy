-- ------------------------------------------------------------------------ 
-- this file lists all create table (and other statements) which are 
-- neccessary to set up a BibSonomy system
-- NOTES:
--  - please enter create statements for tables in alphabetical order
--    of the table names 
--  - add additional statements for tables (i.e. inserting default rows)
--    after the corresponding create statement
--  - don't forget to add the corresponding "drop table" statements in
--    the file tables_new_drop.sql (also in alph. order)
-- ------------------------------------------------------------------------


CREATE TABLE `DBLPFailures` (
  `date_of_create` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `snippet` text default NULL,
  `user_name` varchar(255) default NULL,
  `failure_type` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- failure_types: warning, duplicate, incomplete, incomplete_author_editor

CREATE TABLE `DBLP` (
  `lastupdate` datetime NOT NULL default '1815-12-10 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into DBLP values(DEFAULT);

CREATE TABLE `ContentModifiedTags` (
  `in_use` tinyint(1) NOT NULL default '0',
  `tag_id` int(10) NOT NULL,
  PRIMARY KEY  (`in_use`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `MostSimTagsByContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `sim_tag_id` int(10) unsigned NOT NULL,
  `sim` double NOT NULL,
  PRIMARY KEY  (`sim_tag_id`,`tag_id`),
  UNIQUE KEY `tag_id` (`tag_id`,`sim_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `TagContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `hash` char(33) NOT NULL,
  `ctr` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`tag_id`,`hash`),
  UNIQUE KEY `hash` (`hash`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `TagUser` (
  `tag_id` int(10) unsigned NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `ctr` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`user_name`,`tag_id`),
  UNIQUE KEY `tag_id` (`tag_id`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `TmpMostSimTagsByContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `sim_tag_id` int(10) unsigned NOT NULL,
  `sim` double NOT NULL,
  PRIMARY KEY  (`tag_id`,`sim_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `answers` (
  `questionID` int(10) unsigned NOT NULL default '0',
  `questionTyp` int(10) unsigned NOT NULL default '0',
  `objektID1` int(10) unsigned NOT NULL default '0',
  `objektID2` int(10) unsigned NOT NULL default '0',
  `tasID1` int(10) unsigned NOT NULL default '0',
  `tasID2` int(10) unsigned NOT NULL default '0',
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  `cycleID` int(10) unsigned NOT NULL default '0',
  `answer` int(10) unsigned NOT NULL default '0',
  `answertyp` varchar(30) NOT NULL default ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bibhash` (
  `hash` char(32) NOT NULL default '',
  `ctr` int(10) unsigned NOT NULL default '1',
  `type` tinyint(3) NOT NULL default '0',
  PRIMARY KEY (`type`,`hash`),
  KEY `type_ctr_idx` (`type`,`ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `bibtex` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `journal` text,
  `volume` varchar(255) default NULL,
  `chapter` varchar(255) default NULL,
  `edition` varchar(255) default NULL,
  `month` varchar(45) default NULL,
  `day` varchar(45) default NULL,
  `booktitle` text,
  `howPublished` varchar(255) default NULL,
  `institution` varchar(255) default NULL,
  `organization` varchar(255) default NULL,
  `publisher` varchar(255) default NULL,
  `address` varchar(255) default NULL,
  `school` varchar(255) default NULL,
  `series` varchar(255) default NULL,
  `bibtexKey` varchar(255) default NULL,
  `group` int(10) default '0',
  `date` datetime default '1815-12-10 00:00:00',
  `user_name` varchar(255) default NULL,
  `url` text,
  `type` varchar(255) default NULL,
  `description` text,
  `annote` varchar(255) default NULL,
  `note` text,
  `pages` varchar(15) default NULL,
  `bKey` varchar(255) default NULL,
  `number` varchar(45) default NULL,
  `crossref` varchar(255) default NULL,
  `misc` text,
  `bibtexAbstract` text,
  `simhash0` char(32) NOT NULL default '',
  `simhash1` char(32) NOT NULL default '',
  `simhash2` char(32) NOT NULL default '',
  `simhash3` char(32) NOT NULL default '',
  `entrytype` varchar(30) default NULL,
  `title` text,
  `author` text,
  `editor` text,
  `year` varchar(45) default NULL,
  `privnote` text,
  `scraperid` int NOT NULL default '-1',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`),
  UNIQUE KEY `unique_user_name_simhash2` (`user_name`,`simhash2`),
  KEY `date_idx` (`date`),
  KEY `group_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `simhash0_group_date_content_id_idx` (`simhash0`,`group`,`date`,`content_id`),
  KEY `simhash1_group_date_content_id_idx` (`simhash1`,`group`,`date`,`content_id`),
  KEY `simhash2_group_date_content_id_idx` (`simhash2`,`group`,`date`,`content_id`),  
  KEY `user_name_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `user_name_simhash0_idx` (`user_name`,`simhash0`),
  KEY `user_name_simhash1_idx` (`user_name`,`simhash1`),
  KEY `user_name_simhash2_idx` (`user_name`,`simhash2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bibtexurls` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `url` varchar(255) NOT NULL default '',
  `text` text,
  `group` int(10) default '0',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`content_id`, `url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bookmark` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `book_url_hash` char(32) NOT NULL default '',
  `book_description` text NOT NULL,
  `book_extended` text,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  `to_bib` tinyint(3) NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`),
  KEY `group_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `user_name_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `book_url_hash_group_date_content_id_idx` (`book_url_hash`,`group`,`date`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `collector` (
  `user_name` varchar(30) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`user_name`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `document` (
  `hash` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) default '',
  `user_name` varchar(255) default '',
  `date` datetime default '0000-00-00 00:00:00',
  PRIMARY KEY  (`hash`,`content_id`),
  KEY `content_id_idx` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `extended_fields_data` (
  `key_id` int(10) unsigned NOT NULL,
  `value` text NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`content_id`,`key_id`),
  KEY `key_id` (`key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `extended_fields_map` (
  `group` int(10) NOT NULL,
  `key_id` int(10) unsigned NOT NULL,
  `key` varchar(30) NOT NULL,
  `description` varchar(255) default NULL,
  `order` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`key_id`),
  UNIQUE KEY `group_order` (`group`,`order`),
  KEY `group_keyid` (`group`,`key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `friends` (
  `friends_id` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL default '',
  `f_user_name` varchar(30) NOT NULL default '',
  `friendship_date` datetime NOT NULL default '1815-12-10 00:00:00',
  PRIMARY KEY  (`friends_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `groupids` (
  `group_name` varchar(30) NOT NULL default '',
  `group` int(10) NOT NULL default '0',
  `privlevel` tinyint(3) unsigned default '1',
  `sharedDocuments` tinyint(1) default '0',
  PRIMARY KEY  (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `groupids` VALUES ('public',0,1),('private',1,1),('friends',2,1);
--  INSERT INTO `groupids` VALUES ('public',1073741824,1),('private',1073741825,1),('friends',1073741826,1);
INSERT INTO `groupids` VALUES ('public',-2147483648,1),('private',-2147483647,1),('friends',-2147483646,1);

CREATE TABLE `groups` (
  `user_name` varchar(30) NOT NULL default '',
  `group` int(10) default '0',
  `defaultgroup` int(10) default '0',
  `start_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_status` int(10) NOT NULL default '7'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `highwirelist` (
  `list` longtext NOT NULL,
  `lastupdate` timestamp NOT NULL default CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

INSERT INTO `highwirelist` (list) VALUES ("foo");

CREATE TABLE `ids` (
  `name` tinyint(3) unsigned NOT NULL,
  `value` int(10) unsigned NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `ids` VALUES (0,0,"content_id"),
                         (1,0,"tas id"),
                         (2,0,"relation id"),
                         (3,0,"question id"),
                         (4,1,"cycle id"),
                         (5,0,"extended_fields_id"),
                         (7,0,"scraper_metadata_id");

CREATE TABLE `log_bibtex` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `journal` varchar(255) default NULL,
  `volume` varchar(255) default NULL,
  `chapter` varchar(255) default NULL,
  `edition` varchar(255) default NULL,
  `month` varchar(45) default NULL,
  `day` varchar(45) default NULL,
  `bookTitle` text,
  `howPublished` varchar(255) default NULL,
  `institution` varchar(255) default NULL,
  `organization` varchar(255) default NULL,
  `publisher` varchar(255) default NULL,
  `address` varchar(255) default NULL,
  `school` varchar(255) default NULL,
  `series` varchar(255) default NULL,
  `bibtexKey` varchar(255) default NULL,
  `group` int(10) default '0',
  `date` datetime default '1815-12-10 00:00:00',
  `user_name` varchar(255) default NULL,
  `url` text,
  `type` varchar(255) default NULL,
  `description` text,
  `annote` varchar(255) default NULL,
  `note` text,
  `pages` varchar(15) default NULL,
  `bKey` varchar(255) default NULL,
  `number` varchar(45) default NULL,
  `crossref` varchar(255) default NULL,
  `misc` text,
  `bibtexAbstract` text,
  `entrytype` varchar(30) default NULL,
  `title` text,
  `author` text,
  `editor` text,
  `year` varchar(45) default NULL,
  `scraperid` int NOT NULL default '-1',
  `simhash0` char(32) NOT NULL default '',
  `simhash1` char(32) NOT NULL default '',
  `simhash2` char(32) NOT NULL default '',
  `simhash3` char(32) NOT NULL default '',
  `new_content_id` int(10) unsigned NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_bookmark` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `book_url_hash` char(32) NOT NULL default '',
  `book_description` text NOT NULL default '',
  `book_extended` text,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  `new_content_id` int(10) unsigned NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_collector` (
  `user_name` varchar(30) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `add_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `del_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  KEY `user_name_content_id_idx` (`user_name`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_friends` (
  `friends_id` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL default '',
  `f_user_name` varchar(30) NOT NULL default '',
  `friendship_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `friendship_end_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`friends_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `log_groups` (
  `user_name` varchar(30) NOT NULL default '',
  `group` int(10) default '0',
  `defaultgroup` int(10) default '0',
  `start_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `end_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_status` int(10) NOT NULL default '7'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `log_tagtagrelations` (
  `relationID` int(10) unsigned NOT NULL,
  `lower` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`relationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `log_tas` (
  `tas_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `content_type` tinyint(3) unsigned default NULL,
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`tas_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `log_user` (
  `user_name` varchar(30) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_password` char(32) NOT NULL,
  `user_homepage` varchar(255) NOT NULL,
  `user_realname` varchar(255) NOT NULL,
  `spammer` tinyint(1) NOT NULL default '0',
  `openurl` varchar(255) default NULL,
  `reg_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `ip_address` varchar(255) default NULL,
  `tmp_password` char(32) default NULL,
  `tmp_request_date` datetime NOT NULL default '1815-12-10 00:00:00' ,
  `tagbox_style` tinyint(4) default '0',
  `tagbox_sort` tinyint(4) default '0',
  `tagbox_minfreq` tinyint(4) default '0',
  `tagbox_tooltip` tinyint(4) default '0',
  `list_itemcount` smallint(6) default '10',
  `spammer_suggest` tinyint(1) NOT NULL default '1',
  `birthday` date default NULL, 
  `gender` char(1) default NULL,  
  `place` varchar(255) default NULL, 
  `profession` varchar(255) default NULL,  
  `interests` varchar(255) default NULL, 
  `hobbies` varchar(255) default NULL,
  `profilegroup` tinyint(1) default '1',
  `updated_by` varchar(30) default NULL,
  `updated_at` datetime default '1815-12-10 00:00:00',
  `api_key` varchar(32) default NULL,
  `lang` char(2) default 'en',
  `role` tinyint(3) NOT NULL,
  `prediction` int(10) default '9',
  `algorithm` varchar(255),
  `count` int(10) default '0',
  `timestamp` mediumtext not null,
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `picked_concepts` (
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `user_name` varchar(30) NOT NULL,
  `date_of_create` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`user_name`,`upper`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rankings` (
  `id` int(11) NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `alpha` double default NULL,
  `beta` double default NULL,
  `gamma` double default NULL,
  `basepref` double default NULL,
  `dim` int(11) default NULL,
  `item` varchar(255) default NULL,
  `itemtype` tinyint(1) unsigned default NULL,
  `itempref` double default NULL,
  `delta` double default NULL,
  `iter` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `dim_item` (`dim`,`item`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `scraperMetaData` (
  `id` int unsigned NOT NULL,
  `metaResult` text,
  `scraper` varchar(255) NOT NULL,
  `url` text,
  `scrape_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `search` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `content` text NOT NULL,
  `author` text NOT NULL,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `content_type` tinyint(1) unsigned default NULL,
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`content_id`),
  KEY `group_content_type_date_content_id_idx` (`group`,`content_type`,`date`,`content_id`),
  KEY `user_name_group_content_type_date_content_id_idx` (`user_name`,`content_type`,`group`,`date`,`content_id`),
  KEY `user_name_content_type_date_content_id_idx` (`user_name`,`content_type`,`date`,`content_id`),
  KEY `date_idx` (`date`),
  FULLTEXT KEY `content_fidx` (`content`),
  FULLTEXT KEY `author_fidx` (`author`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `spammer_tags` (
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `spammer` tinyint(1) NOT NULL default '1',
  UNIQUE KEY `tag_name` (`tag_name`),
  KEY `tag_name_idx` (`tag_name`),
  KEY `tag_name_spammer` (`tag_name`,`spammer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tags` (
  `tag_id` int(10) unsigned NOT NULL auto_increment,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_stem` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_ctr` int(10) unsigned NOT NULL default '1',
  `tag_ctr_public` int(10) unsigned NOT NULL default '0',
  `waiting_content_sim` float NOT NULL default '0',
  PRIMARY KEY  (`tag_id`),
  KEY `tag_ctr_idx` (`tag_ctr`),
  KEY `tag_ctr_public_idx` (`tag_ctr_public`),
  UNIQUE KEY `tag_name_idx` (`tag_name`),
  KEY `tag_name_ctr_idx` (`tag_name`,`tag_ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tagtag` (
  `t1` varchar(255) default NULL,
  `t2` varchar(255) default NULL,
  `ctr` int(10) NOT NULL default '1',
  `ctr_public` int(10) unsigned NOT NULL default '0',
  KEY `t1_ctr_idx` (`t1`,`ctr`),
  KEY `t1_t2_idx` (`t1`(10),`t2`(10)),
  KEY `t1_ctr_public_idx` (`t1`,`ctr_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tagtag_batch` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `tags` text,
  `toinc` tinyint(1) default NULL,
  `isactive` tinyint(1) default '0',
  `id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tagtag_temp` (
  `t1` varchar(255) default NULL,
  `t2` varchar(255) default NULL,
  `incdec` tinyint(1) default NULL,
  `id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tagtagrelations` (
  `relationID` int(10) unsigned NOT NULL auto_increment,
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_name` varchar(30) NOT NULL default '',
  `lower` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  PRIMARY KEY  (`relationID`),
  UNIQUE KEY `user_name` (`user_name`,`lower`(150),`upper`(150))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tas` (
  `tas_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_lower` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `content_type` tinyint(1) unsigned default NULL,
  `user_name` varchar(30) NOT NULL default '',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `group` int(10)  default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`tas_id`),
  KEY `content_id_idx` (`content_id`),
  KEY `tag_name_user_name_idx` (`tag_name`,`user_name`),
  KEY `user_name_tag_name_content_type_group_date_content_id_idx` (`user_name`,`tag_name`,`content_type`,`group`,`date`,`content_id`),
  KEY `user_name_tag_lower_content_type_group_date_content_id_idx` (`user_name`,`tag_lower`,`content_type`,`group`,`date`,`content_id`),
  KEY `user_name_tag_name_content_type_date_content_id_idx` (`user_name`,`tag_name`,`content_type`,`date`,`content_id`),
  KEY `content_type_group_tag_name_date_content_id_idx` (`content_type`,`group`,`tag_name`,`date`,`content_id`),
  KEY `content_type_group_tag_lower_date_content_id_idx` (`content_type`,`group`,`tag_lower`,`date`,`content_id`),
  KEY `group_tag_name_idx` (`group`,`tag_name`),
  KEY `date_idx` (`date`),
  KEY `group_date_idx` (`group`,`date`),
  KEY `user_name_tag_name_idx` (`user_name`,`tag_name`),
  KEY `tag_lower_idx` (`tag_lower`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `temp_bibtex` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `journal` varchar(255) default NULL,
  `volume` varchar(255) default NULL,
  `chapter` varchar(255) default NULL,
  `edition` varchar(255) default NULL,
  `month` varchar(45) default NULL,
  `day` varchar(45) default NULL,
  `bookTitle` text,
  `howPublished` varchar(255) default NULL,
  `institution` varchar(255) default NULL,
  `organization` varchar(255) default NULL,
  `publisher` varchar(255) default NULL,
  `address` varchar(255) default NULL,
  `school` varchar(255) default NULL,
  `series` varchar(255) default NULL,
  `bibtexKey` varchar(255) default NULL,
  `date` datetime default '1815-12-10 00:00:00',
  `user_name` varchar(255) default NULL,
  `url` text,
  `type` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `annote` varchar(255) default NULL,
  `note` text,
  `pages` varchar(15) default NULL,
  `bKey` varchar(255) default NULL,
  `number` varchar(45) default NULL,
  `crossref` varchar(255) default NULL,
  `misc` text,
  `bibtexAbstract` text,
  `entrytype` varchar(30) default NULL,
  `title` text,
  `author` text,
  `editor` text,
  `year` varchar(45) default NULL,
  `simhash0` char(32) NOT NULL default '',
  `simhash1` char(32) NOT NULL default '',
  `simhash2` char(32) NOT NULL default '',
  `simhash3` char(32) NOT NULL default '',
  `ctr` int(10) unsigned NOT NULL default '1',
  `rank` int(10) unsigned NOT NULL default '1',
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `temp_bookmark` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `book_description` varchar(255) NOT NULL default '',
  `book_extended` text,
  `book_url_hash` char(32) NOT NULL default '',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  `book_url_ctr` int(10) unsigned NOT NULL default '1',
  `rank` int(10) unsigned NOT NULL default '1',
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `urls` (
  `book_url_hash` char(32) NOT NULL default '',
  `book_url` text NOT NULL,
  `book_url_ctr` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`book_url_hash`),
  KEY `book_url_idx` (`book_url`(255)),
  KEY `book_url_ctr_idx` (`book_url_ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `user_name` varchar(30) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_password` char(32) NOT NULL,
  `user_homepage` varchar(255) NOT NULL,
  `user_realname` varchar(255) NOT NULL,
  `spammer` tinyint(1) NOT NULL default '0',
  `openurl` varchar(255) default NULL,
  `reg_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `ip_address` varchar(255) default NULL,
  `tmp_password` char(32) default NULL,
  `tmp_request_date` datetime NOT NULL default '1815-12-10 00:00:00' ,
  `tagbox_style` tinyint(4) default '0',
  `tagbox_sort` tinyint(4) default '0',
  `tagbox_minfreq` tinyint(4) default '0',
  `tagbox_tooltip` tinyint(4) default '0',
  `list_itemcount` smallint(6) default '10',
  `spammer_suggest` tinyint(1) NOT NULL default '1',
  `birthday` date default NULL, 
  `gender` char(1) default NULL,  
  `place` varchar(255) default NULL, 
  `profession` varchar(255) default NULL,  
  `interests` varchar(255) default NULL, 
  `hobbies` varchar(255) default NULL,
  `profilegroup` tinyint(1) default '1',
  `updated_by` varchar(30) default NULL,
  `updated_at` datetime default '1815-12-10 00:00:00',
  `api_key` varchar(32) default NULL,
  `lang` char(2) default 'en',
  `role` tinyint(3) NOT NULL,
  `prediction` int(10) default '9',
  `algorithm` varchar(255),
  `count` int(10) default '0',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `weights` (
  `id` int(11) default NULL,
  `weight` double default NULL,
  `dim` int(11) default NULL,
  `item` varchar(255) default NULL,
  `itemtype` tinyint(1) unsigned default NULL,
  KEY `id` (`id`),
  KEY `dim` (`dim`),
  CONSTRAINT `weights_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rankings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

