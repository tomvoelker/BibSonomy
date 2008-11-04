--
-- BibSonomy Test Database
--
-- This SQL script creates a BibSonomy database with all tables
-- and fills in some test data.
--
-- $Id$
-- 
-- 
-- Database: `bibsonomy`
-- 

-- DROP DATABASE IF EXISTS `bibsonomy_unit_test`;
-- CREATE DATABASE `bibsonomy_unit_test`;
-- USE `bibsonomy_unit_test`;

-- --------------------------------------------------------

-- 
-- Structure for table `classifier_settings`
-- 

DROP TABLE IF EXISTS `classifier_settings`;
CREATE TABLE `classifier_settings` (
  `ID` tinyint(4) NOT NULL auto_increment,
  `key` varchar(255) default NULL,
  `value` varchar(255) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `classifier_settings`
--

INSERT INTO `classifier_settings` (`key`, `value`) VALUES ('algorithm', 'weka.classifiers.lazy.IBk');
INSERT INTO `classifier_settings` (`key`, `value`) VALUES ('mode', 'D');


-- --------------------------------------------------------

-- 
-- Structure for table `ContentModifiedTags`
-- 

DROP TABLE IF EXISTS `ContentModifiedTags`;
CREATE TABLE `ContentModifiedTags` (
  `in_use` tinyint(1) NOT NULL default '0',
  `tag_id` int(10) NOT NULL,
  PRIMARY KEY  (`in_use`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `ContentModifiedTags`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `DBLP`
-- 

DROP TABLE IF EXISTS `DBLP`;
CREATE TABLE `DBLP` (
  `lastupdate` datetime NOT NULL default '1815-12-10 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `DBLP`
-- 

INSERT INTO `DBLP` VALUES ('1815-12-10 00:00:00');

-- --------------------------------------------------------

-- 
-- Structure for table `DBLPFailures`
-- 

DROP TABLE IF EXISTS `DBLPFailures`;
CREATE TABLE `DBLPFailures` (
  `date_of_create` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `snippet` text,
  `user_name` varchar(255) default NULL,
  `failure_type` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `DBLPFailures`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `MostSimTagsByContent`
-- 

DROP TABLE IF EXISTS `MostSimTagsByContent`;
CREATE TABLE `MostSimTagsByContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `sim_tag_id` int(10) unsigned NOT NULL,
  `sim` double NOT NULL,
  PRIMARY KEY  (`sim_tag_id`,`tag_id`),
  UNIQUE KEY `tag_id` (`tag_id`,`sim_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `MostSimTagsByContent`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `TagContent`
-- 

DROP TABLE IF EXISTS `TagContent`;
CREATE TABLE `TagContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `hash` char(33) NOT NULL,
  `ctr` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`tag_id`,`hash`),
  UNIQUE KEY `hash` (`hash`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `TagContent`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `TagUser`
-- 

DROP TABLE IF EXISTS `TagUser`;
CREATE TABLE `TagUser` (
  `tag_id` int(10) unsigned NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `ctr` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`user_name`,`tag_id`),
  UNIQUE KEY `tag_id` (`tag_id`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `TagUser`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `TmpMostSimTagsByContent`
-- 

DROP TABLE IF EXISTS `TmpMostSimTagsByContent`;
CREATE TABLE `TmpMostSimTagsByContent` (
  `tag_id` int(10) unsigned NOT NULL,
  `sim_tag_id` int(10) unsigned NOT NULL,
  `sim` double NOT NULL,
  PRIMARY KEY  (`tag_id`,`sim_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `TmpMostSimTagsByContent`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `answers`
-- 

DROP TABLE IF EXISTS `answers`;
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

-- 
-- Data for table `answers`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `bibhash`
-- 

DROP TABLE IF EXISTS `bibhash`;
CREATE TABLE `bibhash` (
  `hash` char(32) NOT NULL default '',
  `ctr` int(10) unsigned NOT NULL default '1',
  `type` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`type`,`hash`),
  KEY `type_ctr_idx` (`type`,`ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `bibhash`
-- 

INSERT INTO `bibhash` (`hash`, `ctr`, `type`) VALUES
('36a19ee7b7923b062a99a6065fe07792', 1, 0),
('8711751127efb070ee910a5d145a168b', 1, 0),
('92e8d9c7588eced69419b911b31580ee', 1, 0),
('9abf98937435f05aec3d58b214a2ac58', 1, 0),
('b386bdfc8ac7b76ca96e6784736c4b95', 1, 0),
('96c7cf1a752564f8ae0b6540e131af73', 1, 1),
('ac6aa3ccb181e61801cefbc1401d409a', 1, 1),
('e2fb0763068b21639c3e36101f64aefe', 1, 1),
('d9eea4aa159d70ecfabafa0c91bbc9f0', 2, 1),
('1b298f199d487bc527a62326573892b8', 1, 2),
('522833042311cc30b8775772335424a7', 1, 2),
('65e49a5791c3dae2356d26fb9040fe29', 1, 2),
('b71d5283dc7f4f59f306810e73e9bc9a', 1, 2),
('b77ddd8087ad8856d77c740c8dc2864a', 1, 2);


-- --------------------------------------------------------

-- 
-- Structure for table `bibtex`
-- 

DROP TABLE IF EXISTS `bibtex`;
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
  `scraperid` int(11) NOT NULL default '-1',
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

-- 
-- Data for table `bibtex`
-- 

INSERT INTO `bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `booktitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `group`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `entrytype`, `title`, `author`, `editor`, `year`, `privnote`, `scraperid`, `change_date`, `rating`) VALUES
(10, 'test journal', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '9abf98937435f05aec3d58b214a2ac58', 'd9eea4aa159d70ecfabafa0c91bbc9f0', 'b77ddd8087ad8856d77c740c8dc2864a', '', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:24:55', 0),
(11, 'test journal', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test spam booktitle', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '1815-12-10 00:00:00', 'testspammer', 'test url', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'b386bdfc8ac7b76ca96e6784736c4b95', '96c7cf1a752564f8ae0b6540e131af73', '65e49a5791c3dae2356d26fb9040fe29', '', 'test entrytype', 'test spam title', 'test spammer', 'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:34:34', 0),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 3, '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '92e8d9c7588eced69419b911b31580ee', 'd9eea4aa159d70ecfabafa0c91bbc9f0', '522833042311cc30b8775772335424a7', '', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', 'test privnote', -1, '2008-05-05 18:11:55', 0),
(13, 'test journal', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 1, '1815-12-10 00:00:00', 'testuser2', 'http://private.bibtex.url.com', '2', 'test description', 'test annote', 'test note', 'test page', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '8711751127efb070ee910a5d145a168b', 'ac6aa3ccb181e61801cefbc1401d409a', '1b298f199d487bc527a62326573892b8', '', 'test entrytype', 'test private title', 'test author', 'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0),
(14, 'test journal', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 2, '1815-12-10 00:00:00', 'testuser2', 'http://friend.bibtex.url.com', '2', 'test description', 'test annote', 'test note', 'test page', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title', 'test author', 'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0);
-- --------------------------------------------------------

-- 
-- Structure for table `bibtexurls`
-- 

DROP TABLE IF EXISTS `bibtexurls`;
CREATE TABLE `bibtexurls` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `url` varchar(255) NOT NULL default '',
  `text` text,
  `group` int(10) default '0',
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`content_id`,`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `bibtexurls`
-- 

INSERT INTO `bibtexurls` (`content_id`, `url`, `text`, `group`, `date`) VALUES
(10, 'http://www.testurl.org', 'test text', 0, '2008-07-09 11:03:24');

-- --------------------------------------------------------

-- 
-- Structure for table `bookmark`
-- 

DROP TABLE IF EXISTS `bookmark`;
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

-- 
-- Data for table `bookmark`
-- 

INSERT INTO `bookmark` (`content_id`, `book_url_hash`, `book_description`, `book_extended`, `group`, `date`, `user_name`, `to_bib`, `change_date`, `rating`) VALUES
(1, '6f372faea7ff92eedf52f597090a6291', 'test bookmark descripton', 'test bookmark extended', 0, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:17:10', 0),
(2, '108eca7b644e2c5e09853619bc416ed0', 'Google', 'bekannteste Suchmaschine', 0, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:34:17', 0),
(3, '7eda282d1d604c702597600a06f8a6b0', 'Yahoo', 'Yahoo Suchmaschine', 0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 10:16:55', 0),
(4, 'b7aa3a91885e432c6c95bec0145c3968', 'FriendScout24', 'Seite f√ºr einen "friend"', 2, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:16:46', 0),
(5, '965a65fdc161e354f3828050390e2b06', 'web.de', 'WEB.de Freemail', 0, '1815-12-10 00:00:00', 'testuser3', 0, '2008-01-18 10:16:39', 0),
(6, '20592a292e53843965c1bb42bfd51876', 'uni-kassel', 'UniK', 0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 11:29:03', 0),
(7, '16dfed76f9d846056a6a3c0d022c3493', 'finetune', 'finetune.com', 4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:14:33', 0),
(8, 'e9ea2574c49c3778f166e8b4b6ed63dd', 'apple', 'apple.com', 4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:20:57', 0),
(9, '294a9e1d594297e7bb9da9e11229c5d7', 'fireball.com', 'fireball', 1, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-29 10:36:06', 0);

-- --------------------------------------------------------

-- 
-- Structure for table `collector`
-- 

DROP TABLE IF EXISTS `collector`;
CREATE TABLE `collector` (
  `user_name` varchar(30) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`user_name`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `collector`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `document`
-- 

DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `hash` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) default '',
  `user_name` varchar(255) default '',
  `date` datetime default '0000-00-00 00:00:00',
  `md5hash` char(32) not null default '00000000000000000000000000000000',
  PRIMARY KEY  (`hash`,`content_id`),
  KEY `content_id_idx` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `document`
-- 
INSERT INTO `document` VALUES ('00000000000000000000000000000000', 10, 'testdocument_1.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000000');
INSERT INTO `document` VALUES ('00000000000000000000000000000001', 10, 'testdocument_2.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000001');

-- --------------------------------------------------------

-- 
-- Structure for table `extended_fields_data`
-- 

DROP TABLE IF EXISTS `extended_fields_data`;
CREATE TABLE `extended_fields_data` (
  `key_id` int(10) unsigned NOT NULL,
  `value` text NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`content_id`,`key_id`),
  KEY `key_id` (`key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `extended_fields_data`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `extended_fields_map`
-- 

DROP TABLE IF EXISTS `extended_fields_map`;
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

-- 
-- Data for table `extended_fields_map`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `friends`
-- 

DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends` (
  `friends_id` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL default '',
  `f_user_name` varchar(30) NOT NULL default '',
  `friendship_date` datetime NOT NULL default '1815-12-10 00:00:00',
  PRIMARY KEY  (`friends_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

-- 
-- Data for table `friends`
-- 

INSERT INTO `friends` VALUES (1, 'testuser1', 'testuser2', '1815-12-10 00:00:00');
INSERT INTO `friends` VALUES (3, 'testuser1', 'testuser3', '1815-12-10 00:00:00');
INSERT INTO `friends` VALUES (2, 'testuser2', 'testuser1', '1815-12-10 00:00:00');

-- --------------------------------------------------------

-- 
-- Structure for table `groupids`
-- 

DROP TABLE IF EXISTS `groupids`;
CREATE TABLE `groupids` (
  `group_name` varchar(30) NOT NULL default '',
  `group` int(10) NOT NULL default '0',
  `privlevel` tinyint(3) unsigned default '1',
  `sharedDocuments` tinyint(1) default '0',
  PRIMARY KEY  (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `groupids`
-- 

INSERT INTO `groupids` VALUES ('public', -2147483648, 1, 0);
INSERT INTO `groupids` VALUES ('private', -2147483647, 1, 0);
INSERT INTO `groupids` VALUES ('friends', -2147483646, 1, 0);
INSERT INTO `groupids` VALUES ('public', 0, 1, 0);
INSERT INTO `groupids` VALUES ('private', 1, 1, 0);
INSERT INTO `groupids` VALUES ('friends', 2, 1, 0);
INSERT INTO `groupids` VALUES ('testgroup1', 3, 0, 1);
INSERT INTO `groupids` VALUES ('testgroup2', 4, 1, 0);
INSERT INTO `groupids` VALUES ('testgroup3', 5, 2, 0);

-- --------------------------------------------------------

-- 
-- Structure for table `groups`
-- 

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `user_name` varchar(30) NOT NULL default '',
  `group` int(10) default '0',
  `defaultgroup` int(10) default '0',
  `start_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_status` int(10) NOT NULL default '7'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `groups`
-- 

INSERT INTO `groups` VALUES ('testuser1', 3, 3, '2007-01-01 01:01:01', 7);
INSERT INTO `groups` VALUES ('testuser2', 3, 3, '2007-01-01 01:01:01', 7);
INSERT INTO `groups` VALUES ('testuser1', 4, 3, '2007-01-01 01:01:01', 7);
INSERT INTO `groups` VALUES ('testuser1', 5, 3, '2007-01-01 01:01:01', 7);

-- --------------------------------------------------------

-- 
-- Structure for table `highwirelist`
-- 

DROP TABLE IF EXISTS `highwirelist`;
CREATE TABLE `highwirelist` (
  `list` longtext NOT NULL,
  `lastupdate` timestamp NOT NULL default CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `highwirelist`
-- 

INSERT INTO `highwirelist` VALUES ('foo', '2007-12-20 20:36:50');

-- --------------------------------------------------------

-- 
-- Structure for table `ids`
-- 

DROP TABLE IF EXISTS `ids`;
CREATE TABLE `ids` (
  `name` tinyint(3) unsigned NOT NULL,
  `value` int(10) unsigned NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `ids`
-- 

INSERT INTO `ids` VALUES (0, 14, 'content_id');
INSERT INTO `ids` VALUES (1, 24, 'tas id');
INSERT INTO `ids` VALUES (2, 0, 'relation id');
INSERT INTO `ids` VALUES (3, 0, 'question id');
INSERT INTO `ids` VALUES (4, 1, 'cycle id');
INSERT INTO `ids` VALUES (5, 0, 'extended_fields_id');
INSERT INTO `ids` VALUES (7, 0, 'scraper_metadata_id');

-- --------------------------------------------------------

-- 
-- Structure for table `inetAddressStates`
-- 

DROP TABLE IF EXISTS `inetAddressStates`;
CREATE TABLE `inetAddressStates` (
  `address` char(15) NOT NULL default '',
  `status` tinyint(3) default NULL,
  PRIMARY KEY  (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `inetAddressStates`
-- 

INSERT INTO `inetAddressStates` VALUES ('192.168.0.1', 0);

-- --------------------------------------------------------

-- 
-- Structure for table `log_bibtex`
-- 

DROP TABLE IF EXISTS `log_bibtex`;
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
  `scraperid` int(11) NOT NULL default '-1',
  `simhash0` char(32) NOT NULL default '',
  `simhash1` char(32) NOT NULL default '',
  `simhash2` char(32) NOT NULL default '',
  `simhash3` char(32) NOT NULL default '',
  `new_content_id` int(10) unsigned NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_bibtex`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_bookmark`
-- 

DROP TABLE IF EXISTS `log_bookmark`;
CREATE TABLE `log_bookmark` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `book_url_hash` char(32) NOT NULL default '',
  `book_description` text NOT NULL,
  `book_extended` text,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  `new_content_id` int(10) unsigned NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL default '0',
  PRIMARY KEY  (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_bookmark`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_collector`
-- 

DROP TABLE IF EXISTS `log_collector`;
CREATE TABLE `log_collector` (
  `user_name` varchar(30) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `add_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `del_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  KEY `user_name_content_id_idx` (`user_name`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_collector`
-- 

INSERT INTO `collector` (`user_name`, `content_id`, `date`) VALUES
('testuser1', 10, '2008-06-18 14:27:35'),
('testuser1', 12, '2008-06-18 14:27:35'),
('testuser2', 13, '2008-06-18 14:33:01'),
('testuser2', 14, '2008-06-18 14:33:22');

-- --------------------------------------------------------

-- 
-- Structure for table `log_friends`
-- 

DROP TABLE IF EXISTS `log_friends`;
CREATE TABLE `log_friends` (
  `friends_id` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL default '',
  `f_user_name` varchar(30) NOT NULL default '',
  `friendship_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `friendship_end_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`friends_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Data for table `log_friends`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_groups`
-- 

DROP TABLE IF EXISTS `log_groups`;
CREATE TABLE `log_groups` (
  `user_name` varchar(30) NOT NULL default '',
  `group` int(10) default '0',
  `defaultgroup` int(10) default '0',
  `start_date` datetime NOT NULL default '1815-12-10 00:00:00',
  `end_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_status` int(10) NOT NULL default '7'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_groups`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_tagtagrelations`
-- 

DROP TABLE IF EXISTS `log_tagtagrelations`;
CREATE TABLE `log_tagtagrelations` (
  `relationID` int(10) unsigned NOT NULL,
  `lower` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`relationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_tagtagrelations`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_tas`
-- 

DROP TABLE IF EXISTS `log_tas`;
CREATE TABLE `log_tas` (
  `tas_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `content_type` tinyint(3) unsigned default NULL,
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`tas_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_tas`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_user`
-- 

DROP TABLE IF EXISTS `log_user`;
CREATE TABLE `log_user` (
  `nr` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_password` varchar(32) NOT NULL,
  `user_homepage` varchar(255) default '',
  `user_realname` varchar(255) NOT NULL,
  `spammer` tinyint(1) NOT NULL default '0',
  `openurl` varchar(255) default NULL,
  `reg_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `ip_address` varchar(255) default NULL,
  `id` int(11) default NULL,
  `tmp_password` char(32) default NULL,
  `tmp_request_date` datetime NOT NULL,
  `tagbox_style` tinyint(4) default NULL,
  `tagbox_sort` tinyint(4) default NULL,
  `tagbox_minfreq` tinyint(4) default NULL,
  `tagbox_tooltip` tinyint(4) default NULL,
  `list_itemcount` smallint(6) default NULL,
  `spammer_suggest` tinyint(1) NOT NULL default '0',
  `birthday` date default NULL,
  `gender` char(1) default NULL,
  `profession` varchar(255) default NULL,
  `interests` varchar(255) default NULL,
  `hobbies` varchar(255) default NULL,
  `place` varchar(255) default NULL,
  `profilegroup` tinyint(1) NOT NULL default '0',
  `api_key` varchar(32) default NULL,
  `updated_by` varchar(30) default NULL,
  `updated_at` datetime default '1815-12-10 00:00:00',
  `lang` char(2) default NULL,
  `role` tinyint(4) NOT NULL,
  `timestamp` mediumtext NOT NULL,
  `prediction` int(11) default '9',
  `algorithm` varchar(255) default NULL,
  `count` int(11) default '0',
  `log_level` tinyint(4) NOT NULL default '0',
  `to_classify` tinyint(4) default '1',
  PRIMARY KEY  (`nr`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_user`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `log_prediction`
-- 

DROP TABLE IF EXISTS `log_prediction`;
CREATE TABLE `log_prediction` (
  `ID` int(11) NOT NULL auto_increment,
  `user_name` varchar(30) NOT NULL,
  `prediction` tinyint(4) NOT NULL,
  `timestamp` bigint(20) default NULL,
  `updated_at` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `algorithm` varchar(100) default NULL,
  `mode` char(1) default NULL,
  `confidence` double default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- 
-- Data for table `log_prediction`
-- 

INSERT INTO `log_prediction` VALUES (1, 'testspammer', 1, UNIX_TIMESTAMP(NOW()),'2008-06-18 14:27:35', 'testlogging', 0, 0.2);

-- --------------------------------------------------------

-- 
-- Structure for table `picked_concepts`
-- 

DROP TABLE IF EXISTS `picked_concepts`;
CREATE TABLE `picked_concepts` (
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `user_name` varchar(30) NOT NULL,
  `date_of_create` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`user_name`,`upper`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `picked_concepts`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `prediction`
-- 

DROP TABLE IF EXISTS `prediction`;
CREATE TABLE `prediction` (
  `user_name` varchar(30) NOT NULL,
  `prediction` tinyint(4) NOT NULL,
  `timestamp` bigint(20) default NULL,
  `updated_at` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `algorithm` varchar(100) default NULL,
  `mode` char(1) default NULL,
  `evaluator` tinyint(4) default NULL,
  `confidence` double default NULL,
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- 
-- Data for table `prediction`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `rankings`
-- 

DROP TABLE IF EXISTS `rankings`;
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

-- 
-- Data for table `rankings`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `scraperMetaData`
-- 

DROP TABLE IF EXISTS `scraperMetaData`;
CREATE TABLE `scraperMetaData` (
  `id` int(10) unsigned NOT NULL,
  `metaResult` text,
  `scraper` varchar(255) NOT NULL,
  `url` text,
  `scrape_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `scraperMetaData`
-- 

-- --------------------------------------------------------

-- 
-- Structure for table `search_bibtex`
-- 

DROP TABLE IF EXISTS `search_bibtex`;
CREATE TABLE `search_bibtex` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `content` text NOT NULL,
  `author` text NOT NULL,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`content_id`),
  KEY `group_content_type_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_content_type_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `user_name_content_type_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `date_idx` (`date`),
  FULLTEXT KEY `content_fidx` (`content`),
  FULLTEXT KEY `author_fidx` (`author`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Data for table `search_bibtex`
-- 

INSERT INTO `search_bibtex` (`content_id`, `content`, `author`, `group`, `date`, `user_name`) VALUES
(10, 'test bibtext search string', 'author', 0, '1815-12-10 00:00:00', 'testuser1');

-- --------------------------------------------------------

-- 
-- Structure for table `search_bookmark`
-- 

DROP TABLE IF EXISTS `search_bookmark`;
CREATE TABLE `search_bookmark` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `content` text NOT NULL,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`content_id`),
  KEY `group_content_type_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_content_type_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `user_name_content_type_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `date_idx` (`date`),
  FULLTEXT KEY `content_fidx` (`content`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 
-- Data for table `search_bookmark`
-- 

INSERT INTO `search_bookmark` (`content_id`, `content`, `group`, `date`, `user_name`) VALUES
(2, 'google suchmaschine gmail earth sketchup maps news images bot adwords', 0, '1815-12-10 00:00:00', 'testuser1');

-- --------------------------------------------------------

-- 
-- Structure for table `spammer_tags`
-- 

DROP TABLE IF EXISTS `spammer_tags`;
CREATE TABLE `spammer_tags` (
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `spammer` tinyint(1) NOT NULL default '1',
  UNIQUE KEY `tag_name` (`tag_name`),
  KEY `tag_name_idx` (`tag_name`),
  KEY `tag_name_spammer` (`tag_name`,`spammer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `spammer_tags`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `tags`
-- 

DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `tag_id` int(10) unsigned NOT NULL auto_increment,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_stem` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_ctr` int(10) unsigned NOT NULL default '1',
  `tag_ctr_public` int(10) unsigned NOT NULL default '0',
  `waiting_content_sim` float NOT NULL default '0',
  PRIMARY KEY  (`tag_id`),
  UNIQUE KEY `tag_name_idx` (`tag_name`),
  KEY `tag_ctr_idx` (`tag_ctr`),
  KEY `tag_ctr_public_idx` (`tag_ctr_public`),
  KEY `tag_name_ctr_idx` (`tag_name`,`tag_ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Data for table `tags`
-- 

INSERT INTO `tags` (`tag_id`, `tag_name`, `tag_stem`, `tag_ctr`, `tag_ctr_public`, `waiting_content_sim`) VALUES
(1, 'testtag', '', 2, 2, 0),
(2, 'suchmaschine', '', 4, 4, 0),
(3, 'google', '', 1, 1, 0),
(4, 'yahoo', '', 1, 1, 0),
(5, 'friends', '', 1, 1, 0),
(6, 'friendscout', '', 1, 1, 0),
(7, 'web', '', 1, 1, 0),
(8, 'freemail', '', 1, 1, 0),
(9, 'uni', '', 1, 1, 0),
(10, 'kassel', '', 1, 1, 0),
(11, 'finetune', '', 1, 1, 0),
(12, 'radio', '', 1, 1, 0),
(13, 'apple', '', 1, 1, 0),
(14, 'fireball', '', 1, 1, 0),
(15, 'testbibtex', '', 2, 2, 0),
(16, 'spam', '', 1, 0, 0),
(17, 'bibtexgroup', '', 1, 1, 0),
(18, 'privatebibtex', '', 1, 1, 0),
(19, 'friendbibtex', '', 1, 1, 0);

-- --------------------------------------------------------

-- 
-- Structure for table `tagtag`
-- 

DROP TABLE IF EXISTS `tagtag`;
CREATE TABLE `tagtag` (
  `t1` varchar(255) default NULL,
  `t2` varchar(255) default NULL,
  `ctr` int(10) NOT NULL default '1',
  `ctr_public` int(10) unsigned NOT NULL default '0',
  KEY `t1_ctr_idx` (`t1`,`ctr`),
  KEY `t1_t2_idx` (`t1`(10),`t2`(10)),
  KEY `t1_ctr_public_idx` (`t1`,`ctr_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `tagtag`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `tagtag_batch`
-- 

DROP TABLE IF EXISTS `tagtag_batch`;
CREATE TABLE `tagtag_batch` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `tags` text,
  `toinc` tinyint(1) default NULL,
  `isactive` tinyint(1) default '0',
  `id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Data for table `tagtag_batch`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `tagtag_temp`
-- 

DROP TABLE IF EXISTS `tagtag_temp`;
CREATE TABLE `tagtag_temp` (
  `t1` varchar(255) default NULL,
  `t2` varchar(255) default NULL,
  `incdec` tinyint(1) default NULL,
  `id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Data for table `tagtag_temp`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `tagtagrelations`
-- 

DROP TABLE IF EXISTS `tagtagrelations`;
CREATE TABLE `tagtagrelations` (
  `relationID` int(10) unsigned NOT NULL auto_increment,
  `date_of_create` datetime NOT NULL default '1815-12-10 00:00:00',
  `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `user_name` varchar(30) NOT NULL default '',
  `lower` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `upper` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  PRIMARY KEY  (`relationID`),
  UNIQUE KEY `user_name` (`user_name`,`lower`(150),`upper`(150))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- 
-- Data for table `tagtagrelations`
-- 


-- --------------------------------------------------------

-- 
-- Structure for table `tas`
-- 

DROP TABLE IF EXISTS `tas`;
CREATE TABLE `tas` (
  `tas_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_lower` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `content_type` tinyint(1) unsigned default NULL,
  `user_name` varchar(30) NOT NULL default '',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `group` int(10) default '0',
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

-- 
-- Data for table `tas`
-- 

INSERT INTO `tas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(1, 'testtag', 'testtag', 1, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:20:07'),
(2, 'google', 'google', 2, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(3, 'suchmaschine', 'suchmaschine', 2, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:19:51'),
(4, 'yahoo', 'yahoo', 3, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 10:21:12'),
(5, 'suchmaschine', 'suchmaschine', 3, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 10:21:47'),
(6, 'friends', 'friends', 4, 1, 'testuser1', '1815-12-10 00:00:00', 2, '2008-01-18 10:24:31'),
(7, 'friendscout', 'friendscout', 4, 1, 'testuser1', '1815-12-10 00:00:00', 2, '2008-01-18 10:24:44'),
(8, 'web', 'web', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(9, 'freemail', 'freemail', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(10, 'suchmaschine', 'suchmaschine', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(11, 'uni', 'uni', 6, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 11:30:05'),
(12, 'kassel', 'kassel', 6, 1, 'testuser2', '0000-00-00 00:00:00', 0, '2008-01-18 11:30:05'),
(13, 'finetune', 'finetune', 7, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:22:09'),
(14, 'radio', 'radio', 7, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:22:20'),
(15, 'apple', 'apple', 8, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:20:37'),
(16, 'suchmaschine', 'suchmaschine', 9, 1, 'testuser1', '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(17, 'fireball', 'fireball', 9, 1, 'testuser1', '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(18, 'testbibtex', 'testbibtex', 10, 2, 'testuser1', '1815-12-10 00:00:00', 0, '2008-03-19 11:21:44'),
(19, 'testtag', 'testtag', 10, 2, 'testuser1', '1815-12-10 00:00:00', 0, '2008-03-19 11:27:34'),
(20, 'spam', 'spam', 11, 2, 'testspammer', '1815-12-10 00:00:00', 0, '2008-03-20 16:35:21'),
(21, 'bibtexgroup', 'bibtexgroup', 12, 2, 'testuser1', '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(22, 'privatebibtex', 'privatebibtex', 13, 2, 'testuser2', '1815-12-10 00:00:00', 1, '2008-03-20 20:35:21'),
(23, 'friendbibtex', 'friendbibtex', 14, 2, 'testuser2', '1815-12-10 00:00:00', 2, '2008-03-20 20:35:21'),
(24, 'testbibtex', 'testbibtex', 12, 2, 'testuser1', '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21');

-- --------------------------------------------------------

-- 
-- Structure for table `temp_bibtex`
-- 

DROP TABLE IF EXISTS `temp_bibtex`;
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
  `popular_days` smallint NOT NULL default '1',
  PRIMARY KEY (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `temp_bibtex`
-- 

INSERT INTO `temp_bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `bookTitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `entrytype`, `title`, `author`, `editor`, `year`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `ctr`, `rank`, `rating`, `popular_days`) VALUES
(10, 'test journal', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '9abf98937435f05aec3d58b214a2ac58', 'd9eea4aa159d70ecfabafa0c91bbc9f0', 'b77ddd8087ad8856d77c740c8dc2864a', '', 1, 1, 0, 1),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '92e8d9c7588eced69419b911b31580ee', 'd9eea4aa159d70ecfabafa0c91bbc9f0', '522833042311cc30b8775772335424a7', '', 1, 2, 0, 2);

-- --------------------------------------------------------

-- 
-- Structure for table `temp_bookmark`
-- 

DROP TABLE IF EXISTS `temp_bookmark`;
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
  `popular_days` smallint NOT NULL default '1',
  PRIMARY KEY (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `temp_bookmark`
-- 

INSERT INTO `temp_bookmark` (`content_id`, `book_description`, `book_extended`, `book_url_hash`, `date`, `user_name`, `book_url_ctr`, `rank`, `rating`, `popular_days`) VALUES
(1, 'test bookmark descripton	', 'test bookmark extended	', '6f372faea7ff92eedf52f597090a6291', '1815-12-10 00:00:00', 'testuser1', 1, 1, 0, 1);

-- --------------------------------------------------------

-- 
-- Structure for table `urls`
-- 

DROP TABLE IF EXISTS `urls`;
CREATE TABLE `urls` (
  `book_url_hash` char(32) NOT NULL default '',
  `book_url` text NOT NULL,
  `book_url_ctr` int(10) unsigned NOT NULL default '1',
  PRIMARY KEY  (`book_url_hash`),
  KEY `book_url_idx` (`book_url`(255)),
  KEY `book_url_ctr_idx` (`book_url_ctr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `urls`
-- 

INSERT INTO `urls` (`book_url_hash`, `book_url`, `book_url_ctr`) VALUES
('108eca7b644e2c5e09853619bc416ed0', 'http://www.google.de', 1),
('16dfed76f9d846056a6a3c0d022c3493', 'http://www.finetune.com', 1),
('20592a292e53843965c1bb42bfd51876', 'http://www.uni-kassel.de', 1),
('294a9e1d594297e7bb9da9e11229c5d7', 'http://www.fireball.com\r\n', 1),
('6f372faea7ff92eedf52f597090a6291', 'http://www.testurl.org', 1),
('7eda282d1d604c702597600a06f8a6b0', 'http://www.yahoo.de', 1),
('965a65fdc161e354f3828050390e2b06', 'http://www.web.de', 1),
('b7aa3a91885e432c6c95bec0145c3968', 'http://www.friendscout24.de', 1),
('e9ea2574c49c3778f166e8b4b6ed63dd', 'http://www.apple.com\r\n', 1);

-- --------------------------------------------------------

-- 
-- Structure for table `user`
-- 

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_name` varchar(30) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `user_password` char(32) NOT NULL,
  `user_homepage` varchar(255) NOT NULL,
  `user_realname` varchar(255) NOT NULL,
  `spammer` tinyint(1) NOT NULL default '9',
  `openurl` varchar(255) default NULL,
  `reg_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `ip_address` varchar(255) default NULL,
  `tmp_password` char(32) default NULL,
  `tmp_request_date` datetime NOT NULL default '1815-12-10 00:00:00',
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
  `role` tinyint(3) NOT NULL default 1,
  `to_classify` tinyint(4) default '1',
  `log_level` tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `user`
-- 

INSERT INTO `user` VALUES ('testgroup1', 'testgroup1@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/group/testgroup1', 'Test Group 1', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testgroup2', 'testgroup2@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/group/testgroup2', 'Test Group 2', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testgroup3', 'testgroup3@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/group/testgroup3', 'Test Group 3', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testspammer', 'testspammer@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/', 'Test Spammer', 1, 'http://sfxserv.rug.ac.be:8888/rug', '2007-02-02 02:02:02', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testuser1', 'testuser1@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/user/testuser1', 'Test User 1', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '11111111111111111111111111111111', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testuser2', 'testuser2@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/user/testuser2', 'Test User 2', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '22222222222222222222222222222222', 'en', 0, 1, 0);
INSERT INTO `user` VALUES ('testuser3', 'testuser3@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/user/testuser3', 'Test User 3', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 10, 1, NULL, 'm', 'test-place', 'test-profession', 'test-interests', 'test-hobbies', 1, NULL, '1815-12-10 00:00:00', '33333333333333333333333333333333', 'en', 1, 0, 0);

--
-- Data for table `openIDUser`
-- 
DROP TABLE IF EXISTS `openIDUser`;
CREATE TABLE `openIDUser` (
  `user_name` varchar(30) NOT NULL,
  `openID` varchar(255) NOT NULL,
  PRIMARY KEY  (`openID`),
  KEY `user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- --------------------------------------------------------

-- 
-- Structure for table `weights`
-- 

DROP TABLE IF EXISTS `weights`;
CREATE TABLE `weights` (
  `id` int(11) default NULL,
  `weight` double default NULL,
  `dim` int(11) default NULL,
  `item` varchar(255) default NULL,
  `itemtype` tinyint(1) unsigned default NULL,
  KEY `id` (`id`),
  KEY `dim` (`dim`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 
-- Data for table `weights`
--


-- 
-- Constraints for table `weights`
-- 
ALTER TABLE `weights`
  ADD CONSTRAINT `weights_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rankings` (`id`) ON DELETE CASCADE;
  
--
-- Structure for table `grouptas`
--
DROP TABLE IF EXISTS `grouptas`;
CREATE TABLE `grouptas` (
  `tas_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `tag_lower` varchar(255) NOT NULL default '',
  `content_id` int(10) unsigned NOT NULL default '0',
  `content_type` tinyint(1) unsigned default NULL,
  `user_name` varchar(30) NOT NULL default '',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `group` int(10) default '0',
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

-- 
-- Data for table `grouptas`
-- 

INSERT INTO `grouptas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(1, 'testtag', 'testtag', 1, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:20:07'),
(2, 'google', 'google', 2, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(3, 'suchmaschine', 'suchmaschine', 2, 1, 'testuser1', '1815-12-10 00:00:00', 0, '2008-01-18 10:19:51'),
(4, 'yahoo', 'yahoo', 3, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 10:21:12'),
(5, 'suchmaschine', 'suchmaschine', 3, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 10:21:47'),
(6, 'friends', 'friends', 4, 1, 'testuser1', '1815-12-10 00:00:00', 2, '2008-01-18 10:24:31'),
(7, 'friendscout', 'friendscout', 4, 1, 'testuser1', '1815-12-10 00:00:00', 2, '2008-01-18 10:24:44'),
(8, 'web', 'web', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(9, 'freemail', 'freemail', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(10, 'suchmaschine', 'suchmaschine', 5, 1, 'testuser3', '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(11, 'uni', 'uni', 6, 1, 'testuser2', '1815-12-10 00:00:00', 0, '2008-01-18 11:30:05'),
(12, 'kassel', 'kassel', 6, 1, 'testuser2', '0000-00-00 00:00:00', 0, '2008-01-18 11:30:05'),
(13, 'finetune', 'finetune', 7, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:22:09'),
(14, 'radio', 'radio', 7, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:22:20'),
(15, 'apple', 'apple', 8, 1, 'testuser1', '1815-12-10 00:00:00', 4, '2008-01-21 13:20:37'),
(16, 'suchmaschine', 'suchmaschine', 9, 1, 'testuser1', '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(17, 'fireball', 'fireball', 9, 1, 'testuser1', '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(18, 'testbibtex', 'testbibtex', 10, 2, 'testuser1', '1815-12-10 00:00:00', 0, '2008-03-19 11:21:44'),
(19, 'testtag', 'testtag', 10, 2, 'testuser1', '1815-12-10 00:00:00', 0, '2008-03-19 11:27:34'),
(20, 'spam', 'spam', 11, 2, 'testspammer', '1815-12-10 00:00:00', 0, '2008-03-20 16:35:21'),
(21, 'bibtexgroup', 'bibtexgroup', 12, 2, 'testuser1', '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(22, 'privatebibtex', 'privatebibtex', 13, 2, 'testuser2', '1815-12-10 00:00:00', 1, '2008-03-20 20:35:21'),
(23, 'friendbibtex', 'friendbibtex', 14, 2, 'testuser2', '1815-12-10 00:00:00', 2, '2008-03-20 20:35:21'),
(24, 'testbibtex', 'testbibtex', 12, 2, 'testuser1', '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21');



-- --------------------------------------------------------

-- 
-- Structure for table `clicklog`
-- 

DROP TABLE IF EXISTS `clicklog`;
CREATE TABLE `clicklog` (
  `id` int(11) NOT NULL auto_increment,
  `logdate` datetime default NULL,
  `dompath` text collate utf8_unicode_ci,
  `dompathwclasses` text collate utf8_unicode_ci,
  `type` text collate utf8_unicode_ci,
  `pageurl` text collate utf8_unicode_ci,
  `ahref` text collate utf8_unicode_ci,
  `atitle` text collate utf8_unicode_ci,
  `useragent` text collate utf8_unicode_ci,
  `host` text collate utf8_unicode_ci,
  `completeheader` text collate utf8_unicode_ci,
  `xforwardedfor` text collate utf8_unicode_ci,
  `username` text collate utf8_unicode_ci,
  `sessionid` text collate utf8_unicode_ci,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci
