-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 20. Jul 2013 um 11:28
-- Server Version: 5.5.27
-- PHP-Version: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `bibsonomy_recommender`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `log_recommender`
--

CREATE TABLE IF NOT EXISTS `log_recommender` (
  `query_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `post_id` varchar(255) NOT NULL DEFAULT '-1',
  `user_name` varchar(30) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `content_type` varchar(255) NOT NULL,
  `timeout` int(5) DEFAULT '1000',
  PRIMARY KEY (`query_id`),
  KEY `post_id_user_name_date` (`post_id`,`user_name`,`date`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=667 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_bibtex`
--

CREATE TABLE IF NOT EXISTS `recommender_bibtex` (
  `content_id` int(10) unsigned NOT NULL DEFAULT '0',
  `journal` text,
  `volume` varchar(255) DEFAULT NULL,
  `chapter` varchar(255) DEFAULT NULL,
  `edition` varchar(255) DEFAULT NULL,
  `month` varchar(45) DEFAULT NULL,
  `day` varchar(45) DEFAULT NULL,
  `booktitle` text,
  `howPublished` varchar(255) DEFAULT NULL,
  `institution` varchar(255) DEFAULT NULL,
  `organization` varchar(255) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `school` varchar(255) DEFAULT NULL,
  `series` varchar(255) DEFAULT NULL,
  `bibtexKey` varchar(255) DEFAULT NULL,
  `group` int(10) DEFAULT '0',
  `date` datetime DEFAULT '1815-12-10 00:00:00',
  `user_name` varchar(255) DEFAULT NULL,
  `url` text,
  `type` varchar(255) DEFAULT NULL,
  `description` text,
  `annote` varchar(255) DEFAULT NULL,
  `note` text,
  `pages` varchar(15) DEFAULT NULL,
  `bKey` varchar(255) DEFAULT NULL,
  `number` varchar(45) DEFAULT NULL,
  `crossref` varchar(255) DEFAULT NULL,
  `misc` text,
  `bibtexAbstract` text,
  `simhash0` char(32) NOT NULL DEFAULT '',
  `simhash1` char(32) NOT NULL DEFAULT '',
  `simhash2` char(32) NOT NULL DEFAULT '',
  `simhash3` char(32) NOT NULL DEFAULT '',
  `entrytype` varchar(30) DEFAULT NULL,
  `title` text,
  `author` text,
  `editor` text,
  `year` varchar(45) DEFAULT NULL,
  `privnote` text,
  `scraperid` int(11) NOT NULL DEFAULT '-1',
  `change_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `rating` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`content_id`),
  KEY `unique_user_name_simhash2` (`user_name`,`simhash2`),
  KEY `group_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `bib_hash_group_date_content_id_idx` (`simhash0`,`group`,`date`,`content_id`),
  KEY `user_name_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `simhash1_group_date_content_id_idx` (`simhash1`,`group`,`date`,`content_id`),
  KEY `user_name_simhash1_idx` (`user_name`,`simhash1`),
  KEY `user_name_simhash0_idx` (`user_name`,`simhash0`),
  KEY `date_idx` (`date`),
  KEY `user_name_simhash2_idx` (`user_name`,`simhash2`),
  KEY `simhash0_group_date_content_id_idx` (`simhash0`,`group`,`date`,`content_id`),
  KEY `simhash2_group_date_content_id_idx` (`simhash2`,`group`,`date`,`content_id`),
  KEY `bibtexkey_key` (`bibtexKey`),
  KEY `user_bibtexkey_key` (`user_name`,`bibtexKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_bookmark`
--

CREATE TABLE IF NOT EXISTS `recommender_bookmark` (
  `content_id` int(10) unsigned NOT NULL DEFAULT '0',
  `book_url_hash` varchar(32) DEFAULT '',
  `book_url` text,
  `book_description` text,
  `book_extended` text,
  `group` int(10) DEFAULT '0',
  `date` datetime DEFAULT '1815-12-10 00:00:00',
  `user_name` varchar(30) DEFAULT '',
  `to_bib` tinyint(3) DEFAULT '0',
  `change_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `rating` tinyint(3) DEFAULT '0',
  PRIMARY KEY (`content_id`),
  KEY `group_date_content_id_idx` (`group`,`date`,`content_id`),
  KEY `user_name_group_date_content_id_idx` (`user_name`,`group`,`date`,`content_id`),
  KEY `user_name_date_content_id_idx` (`user_name`,`date`,`content_id`),
  KEY `book_url_hash_group_date_content_id_idx` (`book_url_hash`,`group`,`date`,`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_log_entities`
--

CREATE TABLE IF NOT EXISTS `recommender_log_entities` (
  `RequestId` int(10) NOT NULL,
  `EntityId` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `log_id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=73 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_postmap`
--

CREATE TABLE IF NOT EXISTS `recommender_postmap` (
  `post_id` int(11) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `date` datetime NOT NULL,
  `hash` char(32) NOT NULL,
  PRIMARY KEY (`post_id`,`user_name`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_preset`
--

CREATE TABLE IF NOT EXISTS `recommender_preset` (
  `query_id` varchar(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  `tag` varchar(30) NOT NULL,
  PRIMARY KEY (`query_id`,`setting_id`,`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_querymap`
--

CREATE TABLE IF NOT EXISTS `recommender_querymap` (
  `query_id` bigint(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  PRIMARY KEY (`query_id`,`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_recommendations`
--

CREATE TABLE IF NOT EXISTS `recommender_recommendations` (
  `query_id` bigint(20) NOT NULL,
  `score` double NOT NULL,
  `confidence` double NOT NULL,
  `tag` varchar(255) NOT NULL,
  PRIMARY KEY (`query_id`,`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_recommendations_library`
--

CREATE TABLE IF NOT EXISTS `recommender_recommendations_library` (
  `query_id` bigint(20) NOT NULL,
  `score` double NOT NULL,
  `confidence` double NOT NULL,
  `responsetitle` varchar(255) NOT NULL,
  `responseid` varchar(255) NOT NULL,
  PRIMARY KEY (`query_id`,`responsetitle`,`responseid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_result`
--

CREATE TABLE IF NOT EXISTS `recommender_result` (
  `result_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `query_id` bigint(20) NOT NULL,
  `setting_id` bigint(20) NOT NULL,
  `rec_latency` int(11) DEFAULT NULL,
  `score` double NOT NULL,
  `confidence` double NOT NULL,
  `tag` varchar(255) NOT NULL,
  PRIMARY KEY (`result_id`),
  UNIQUE KEY `query_id` (`query_id`,`setting_id`,`tag`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=47 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_result_library`
--

CREATE TABLE IF NOT EXISTS `recommender_result_library` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=290 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_selection`
--

CREATE TABLE IF NOT EXISTS `recommender_selection` (
  `query_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `setting_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`query_id`,`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_selectormap`
--

CREATE TABLE IF NOT EXISTS `recommender_selectormap` (
  `query_id` bigint(20) NOT NULL,
  `selector_id` bigint(20) NOT NULL,
  PRIMARY KEY (`query_id`,`selector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_selectors`
--

CREATE TABLE IF NOT EXISTS `recommender_selectors` (
  `selector_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `selector_name` varchar(50) NOT NULL,
  `selector_meta` blob,
  PRIMARY KEY (`selector_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_settings`
--

CREATE TABLE IF NOT EXISTS `recommender_settings` (
  `setting_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rec_id` varchar(255) NOT NULL,
  `rec_meta` blob,
  `rec_descr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=49 ;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `recommender_status`
--

CREATE TABLE IF NOT EXISTS `recommender_status` (
  `setting_id` bigint(20) unsigned NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `type` int(1) NOT NULL DEFAULT '0',
  `itemrecommender` int(1) NOT NULL,
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
