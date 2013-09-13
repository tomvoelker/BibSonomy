DROP TABLE `reviews`;
DROP TABLE `log_reviews`;
DROP TABLE `reviews_helpful`;
DROP TABLE `log_reviews_helpful`;

--
-- Table structure for table `discussion`
--

DROP TABLE IF EXISTS `discussion`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `discussion` (
  `discussion_id` int(11) NOT NULL,
  `interHash` varchar(32) NOT NULL DEFAULT '',
  `hash` varchar(32) NOT NULL DEFAULT '',
  `type` tinyint(2) NOT NULL DEFAULT '0',
  `text` text,
  `user_name` varchar(30) NOT NULL DEFAULT '',
  `parent_hash` varchar(32) NULL DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `anonymous` tinyint(1) DEFAULT '0',
  `group` int(10) default '0',
  `date` timestamp NULL DEFAULT NULL,
  `change_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`interHash`, `hash`, `user_name`, `group`),
  KEY `date_idx` (`date`),
  KEY `interHash_user_name_date_idx` (`interHash`,`user_name`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `log_discussion`
--

DROP TABLE IF EXISTS `log_discussion`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `log_discussion` (
  `discussion_id` int(11) DEFAULT NULL,
  `interHash` varchar(32) DEFAULT NULL,
  `hash` varchar(32) NOT NULL DEFAULT '',
  `text` text,
  `user_name` varchar(30) DEFAULT NULL,
  `type` tinyint(2) NOT NULL DEFAULT '0',
  `parent_hash` varchar(32) NULL DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `anonymous` tinyint(1) DEFAULT '0',
  `group` int(10) default '0',
  `date` timestamp NULL DEFAULT NULL,
  `change_date` timestamp NULL DEFAULT NULL,
  `log_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

INSERT INTO `ids` (`name`,`value`,`description`) VALUES (15, 0, 'discussion id');