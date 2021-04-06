DROP TABLE IF EXISTS `log_groupids`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log_groupids` (
  `group_name` varchar(30) NOT NULL default '',
  `group` int(10) NOT NULL default '0',
  `parent` int(10) DEFAULT NULL,
  `privlevel` tinyint(3) unsigned default '1',
  `sharedDocuments` tinyint(1) default '0',
  `allow_join` TINYINT(1) NULL DEFAULT '1',
  `shortDescription` TEXT NULL,
  `publ_reporting_mail` varchar(255) DEFAULT NULL,
  `publ_reporting_mail_template` text,
  `publ_reporting_external_url` varchar(255) DEFAULT NULL,
  `organization` BOOLEAN DEFAULT FALSE,
  `internal_id` VARCHAR(255) DEFAULT NULL,
  `log_reason` int(2) DEFAULT NULL,
  `log_date` timestamp NULL DEFAULT NULL,
  `log_user` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `log_group_memberships`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log_group_memberships` (
  `user_name` varchar(30) NOT NULL default '',
  `group` int(10) default '0',
  `defaultgroup` int(10) default '0',
  `start_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `group_role` int(10) NOT NULL default '2',
  `user_shared_documents` tinyint(1) default '0',
  `log_reason` int(2) DEFAULT NULL,
  `log_date` timestamp NULL DEFAULT NULL,
  `log_user` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;