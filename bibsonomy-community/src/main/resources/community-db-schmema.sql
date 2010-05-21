-- MySQL dump 10.11
--
-- Host: localhost    Database: bibsonomy_community
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu10.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `algorithms`
--

DROP TABLE IF EXISTS `algorithms`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `algorithms` (
  `algorithm_id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `meta` blob,
  PRIMARY KEY  (`algorithm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `community`
--

DROP TABLE IF EXISTS `community`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `community` (
  `run_id` int(11) NOT NULL default '0',
  `community_id` int(11) NOT NULL,
  `user_name` varchar(30) NOT NULL default '',
  `p` double default NULL,
  PRIMARY KEY  (`run_id`,`community_id`,`user_name`),
  KEY `community_id` (`community_id`),
  KEY `run_id` (`run_id`,`community_id`),
  CONSTRAINT `community_ibfk_1` FOREIGN KEY (`run_id`) REFERENCES `run_settings` (`run_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `resources`
--

DROP TABLE IF EXISTS `resources`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `resources` (
  `run_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `content_id` int(10) unsigned NOT NULL,
  `content_type` tinyint(1) unsigned default NULL,
  `p` double default NULL,
  PRIMARY KEY  (`run_id`,`community_id`,`content_id`),
  KEY `community_id` (`community_id`),
  KEY `run_id` (`run_id`,`community_id`),
  CONSTRAINT `resources_ibfk_1` FOREIGN KEY (`run_id`, `community_id`) REFERENCES `community` (`run_id`, `community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `run`
--

DROP TABLE IF EXISTS `run`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `run` (
  `date` datetime default NULL,
  `block_id` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`block_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `run_settings`
--

DROP TABLE IF EXISTS `run_settings`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `run_settings` (
  `run_id` int(11) NOT NULL auto_increment,
  `block_id` int(11) default NULL,
  `algorithm_id` int(11) default NULL,
  `clusters` int(11) default NULL,
  `topics` int(11) default NULL,
  PRIMARY KEY  (`run_id`),
  KEY `algorithm_id` (`algorithm_id`),
  KEY `block_id` (`block_id`),
  CONSTRAINT `run_settings_ibfk_1` FOREIGN KEY (`algorithm_id`) REFERENCES `algorithms` (`algorithm_id`),
  CONSTRAINT `run_settings_ibfk_2` FOREIGN KEY (`block_id`) REFERENCES `run` (`block_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `topics`
--

DROP TABLE IF EXISTS `topics`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `topics` (
  `run_id` int(11) NOT NULL,
  `community_id` int(11) NOT NULL,
  `topic_id` int(11) NOT NULL,
  `tag_name` varchar(255) NOT NULL default '',
  `p` double default NULL,
  PRIMARY KEY  (`run_id`,`community_id`,`topic_id`,`tag_name`),
  KEY `community_id` (`community_id`),
  KEY `run_id` (`run_id`,`community_id`),
  CONSTRAINT `topics_ibfk_1` FOREIGN KEY (`run_id`, `community_id`) REFERENCES `community` (`run_id`, `community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `user_community_annotations`
--

DROP TABLE IF EXISTS `user_community_annotations`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user_community_annotations` (
  `user_name` varchar(30) NOT NULL,
  `run_id` int(11) NOT NULL default '0',
  `community_id` int(11) NOT NULL default '0',
  `annotation` varchar(255) NOT NULL,
  PRIMARY KEY  (`user_name`,`run_id`,`community_id`,`annotation`),
  KEY `run_id` (`run_id`,`community_id`),
  CONSTRAINT `user_community_annotations_ibfk_1` FOREIGN KEY (`run_id`, `community_id`) REFERENCES `community` (`run_id`, `community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-05-20 13:11:49
