CREATE DATABASE  IF NOT EXISTS `dnb2_light` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dnb2_light`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: dnb2_light
-- ------------------------------------------------------
-- Server version	5.6.17

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
-- Table structure for table `title_hs_class`
--

DROP TABLE IF EXISTS `title_hs_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `title_hs_class` (
  `title_id` varchar(15) NOT NULL,
  `primary` tinyint(1) DEFAULT NULL,
  `class` varchar(45) NOT NULL DEFAULT '',
  `kind` varchar(45) DEFAULT NULL,
  `std_class` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`title_id`,`class`),
  KEY `title_id` (`title_id`),
  KEY `class` (`class`),
  KEY `std_class` (`std_class`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `title_hs_link`
--

DROP TABLE IF EXISTS `title_hs_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `title_hs_link` (
  `title_id` varchar(55) NOT NULL,
  `person_id` varchar(55) NOT NULL,
  `unique_person_id` varchar(55) NOT NULL,
  `diss` tinyint(4) DEFAULT '0',
  `habil` tinyint(4) DEFAULT '0',
  `person_function` varchar(55) NOT NULL DEFAULT '',
  `user_id` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`title_id`,`person_id`,`person_function`),
  KEY `title_id` (`title_id`),
  KEY `person_id` (`person_id`),
  KEY `unique_person_id` (`unique_person_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`Johannes`@`%`*/ /*!50003 TRIGGER `dnb2_light`.`title_hs_link_BEFORE_INSERT` BEFORE INSERT ON `title_hs_link` FOR EACH ROW SET NEW.user_id = user() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`Johannes`@`%`*/ /*!50003 TRIGGER `dnb2_light`.`title_hs_link_BEFORE_UPDATE` BEFORE UPDATE ON `title_hs_link` FOR EACH ROW SET NEW.user_id = user() */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `title_hs_link_kassel_jena`
--

DROP TABLE IF EXISTS `title_hs_link_kassel_jena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `title_hs_link_kassel_jena` (
  `title_id` varchar(55) NOT NULL,
  `person_id` varchar(55) NOT NULL,
  `person_function` varchar(55) NOT NULL DEFAULT '',
  PRIMARY KEY (`title_id`,`person_id`,`person_function`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `title_hs_person`
--

DROP TABLE IF EXISTS `title_hs_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `title_hs_person` (
  `person_id` varchar(255) NOT NULL,
  `gnd` double DEFAULT NULL,
  `diff_person` tinyint(4) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `affix_sn` varchar(45) DEFAULT NULL,
  `affix_fn` varchar(45) DEFAULT NULL,
  `initials` varchar(45) DEFAULT NULL,
  `dob` int(11) DEFAULT NULL,
  `dod` int(11) DEFAULT NULL,
  `pob` varchar(255) DEFAULT NULL,
  `nat` varchar(45) DEFAULT NULL,
  `ctry` varchar(45) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `title_hs_title`
--

DROP TABLE IF EXISTS `title_hs_title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `title_hs_title` (
  `title_id` varchar(255) NOT NULL,
  `duplicate_id` varchar(255) NOT NULL,
  `e_source` double DEFAULT NULL,
  `title` text,
  `subtitle` text,
  `pub` double DEFAULT NULL,
  `same_pub` varchar(255) DEFAULT NULL,
  `pub_year_c` int(11) DEFAULT NULL,
  `sub_year_c` int(11) DEFAULT NULL,
  `diss` tinyint(4) DEFAULT NULL,
  `habil` tinyint(4) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `uni` varchar(255) DEFAULT NULL,
  `land` varchar(255) DEFAULT NULL,
  `DDR` varchar(255) DEFAULT NULL,
  `uni_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`title_id`),
  KEY `duplicate_id` (`duplicate_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-29 19:22:12
