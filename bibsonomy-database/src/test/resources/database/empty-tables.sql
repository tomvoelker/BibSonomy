-- 
-- This script deletes all rows from all BibSonomy tables.
-- 
-- @author: Dominik Benz, dbenz@cs.uni-kassel.de
-- @version: $Id$


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

DELETE FROM `ContentModifiedTags`;
DELETE FROM `DBLP`;
DELETE FROM `DBLPFailures`;
DELETE FROM `MostSimTagsByContent`;
DELETE FROM `TagContent`;
DELETE FROM `TagUser`;
DELETE FROM `TmpMostSimTagsByContent`;
DELETE FROM `author`;
DELETE FROM `author_bibtex_content`;
DELETE FROM `author_bibtex_name`;
DELETE FROM `bibhash`;
DELETE FROM `bibtex`;
DELETE FROM `bibtexurls`;
DELETE FROM `bookmark`;
DELETE FROM `classifier_settings`;
DELETE FROM `clicklog`;
DELETE FROM `collector`;
DELETE FROM `document`;
DELETE FROM `extended_fields_data`;
DELETE FROM `extended_fields_map`;
DELETE FROM `friends`;
DELETE FROM `followers`;
DELETE FROM `group_tagsets`;
DELETE FROM `groupids`;
DELETE FROM `groups`;
DELETE FROM `grouptas`;
DELETE FROM `highwirelist`;
DELETE FROM `ids`;
DELETE FROM `inboxMail`;
DELETE FROM `inetAddressStates`;
DELETE FROM `log_bibtex`;
DELETE FROM `log_bookmark`;
DELETE FROM `log_collector`;
DELETE FROM `log_friends`;
DELETE FROM `log_followers`;
DELETE FROM `log_groups`;
DELETE FROM `log_prediction`;
DELETE FROM `log_tagtagrelations`;
DELETE FROM `log_tas`;
DELETE FROM `log_user`;
DELETE FROM `openIDUser`;
DELETE FROM `picked_concepts`;
DELETE FROM `popular_tags`;
DELETE FROM `prediction`;
DELETE FROM `ranking_queue`;
DELETE FROM `rankings`;
DELETE FROM `scraperMetaData`;
DELETE FROM `search_bibtex`;
DELETE FROM `search_bibtex_old`;
DELETE FROM `search_bookmark`;
DELETE FROM `search_bookmark_old`;
DELETE FROM `search_old`;
DELETE FROM `spammer_tags`;
DELETE FROM `tags`;
DELETE FROM `tagtag`;
DELETE FROM `tagtag_batch`;
DELETE FROM `tagtag_similarity`;
DELETE FROM `tagtag_similarity2`;
DELETE FROM `tagtag_temp`;
DELETE FROM `tagtagrelations`;
DELETE FROM `tas`;
DELETE FROM `temp_bibtex`;
DELETE FROM `temp_bookmark`;
DELETE FROM `urls`;
DELETE FROM `user`;
DELETE FROM `useruser_similarity`;
DELETE FROM `useruser_similarity2`;
DELETE FROM `useruser_similarity_measures`;
DELETE FROM `weights`;

ALTER TABLE `classifier_settings` AUTO_INCREMENT=0;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
