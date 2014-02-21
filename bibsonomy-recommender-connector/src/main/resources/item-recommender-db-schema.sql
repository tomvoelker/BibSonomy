-- MySQL dump 10.13  Distrib 5.5.35, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: recommender_item
-- ------------------------------------------------------
-- Server version	5.5.35-0ubuntu0.13.10.2

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
-- Table structure for table `log_recommender`
--

DROP TABLE IF EXISTS `log_recommender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_recommender` (
  `query_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_id` varchar(255) NOT NULL DEFAULT '-1',
  `user_name` varchar(30) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `content_type` varchar(255) NOT NULL,
  `timeout` int(5) DEFAULT '1000',
  PRIMARY KEY (`query_id`),
  KEY `post_id_user_name_date` (`entity_id`,`user_name`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=1922 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_feedback`
--

DROP TABLE IF EXISTS `recommender_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_feedback` (
  `entity_id` varchar(255) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `date` datetime NOT NULL,
  `result_id` varchar(255) NOT NULL,
  PRIMARY KEY (`entity_id`,`user_name`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_log_entities`
--

DROP TABLE IF EXISTS `recommender_log_entities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_log_entities` (
  `RequestId` int(10) NOT NULL,
  `EntityId` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `log_id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1327 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_preset`
--

DROP TABLE IF EXISTS `recommender_preset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_preset` (
  `query_id` varchar(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  `entityId` varchar(255) NOT NULL,
  PRIMARY KEY (`query_id`,`setting_id`,`entityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_querymap`
--

DROP TABLE IF EXISTS `recommender_querymap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_querymap` (
  `query_id` bigint(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  PRIMARY KEY (`query_id`,`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_recommendations`
--

DROP TABLE IF EXISTS `recommender_recommendations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_recommendations` (
  `query_id` bigint(20) NOT NULL,
  `score` double NOT NULL,
  `confidence` double NOT NULL,
  `responsetitle` varchar(255) NOT NULL,
  `responseid` varchar(255) NOT NULL,
  PRIMARY KEY (`query_id`,`responsetitle`,`responseid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_result`
--

DROP TABLE IF EXISTS `recommender_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_result` (
  `result_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `query_id` bigint(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  `rec_latency` int(11) DEFAULT NULL,
  `score` double NOT NULL,
  `confidence` double NOT NULL,
  `responsetitle` varchar(255) NOT NULL,
  `responseid` varchar(255) NOT NULL,
  PRIMARY KEY (`result_id`),
  UNIQUE KEY `result_id` (`result_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2722 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_selection`
--

DROP TABLE IF EXISTS `recommender_selection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_selection` (
  `query_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `setting_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`query_id`,`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_selectormap`
--

DROP TABLE IF EXISTS `recommender_selectormap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_selectormap` (
  `query_id` bigint(20) NOT NULL,
  `selector_id` bigint(20) NOT NULL,
  PRIMARY KEY (`query_id`,`selector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_selectors`
--

DROP TABLE IF EXISTS `recommender_selectors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_selectors` (
  `selector_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `selector_name` varchar(50) NOT NULL,
  `selector_meta` blob,
  PRIMARY KEY (`selector_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_settings`
--

DROP TABLE IF EXISTS `recommender_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_settings` (
  `setting_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rec_id` varchar(255) NOT NULL,
  `rec_meta` blob,
  `rec_descr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_settings_log`
--

DROP TABLE IF EXISTS `recommender_settings_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_settings_log` (
  `setting_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rec_id` varchar(255) NOT NULL,
  `rec_meta` blob,
  `rec_descr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recommender_status`
--

DROP TABLE IF EXISTS `recommender_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommender_status` (
  `setting_id` bigint(20) unsigned NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `local` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-02-21 19:26:26
