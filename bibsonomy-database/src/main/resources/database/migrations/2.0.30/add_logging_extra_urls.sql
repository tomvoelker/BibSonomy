--
-- Table structure for table `log_bibtexurls`
--
DROP TABLE IF EXISTS `log_bibtexurls`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log_bibtexurls` (
  `content_id` int(10) unsigned NOT NULL default '0',
  `url` varchar(255) NOT NULL default '',
  `text` text,
  `group` int(10) default '0',
  `date` datetime NOT NULL default '1815-12-10 00:00:00',
  `log_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`content_id`,`url`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;