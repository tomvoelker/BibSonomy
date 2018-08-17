DROP TABLE IF EXISTS `project_memberships`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `project_memberships` (
  `user_name` varchar(30) NOT NULL default '',
  `project` int(10) default '0',
  `defaultproject` int(10) default '0',
  `start_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `project_role` int(10) NOT NULL default '2',
  PRIMARY KEY (`group`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `log_project_memberships`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log_group_memberships` (
	`user_name` VARCHAR(30) NOT NULL DEFAULT '',
	`project` INT(10) NULL DEFAULT '0',
	`defaultproject` INT(10) NULL DEFAULT '0',
	`start_date` DATETIME NOT NULL DEFAULT '1815-12-10 00:00:00',
	`end_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`project_role` INT(10) NOT NULL DEFAULT '2',
) ENGINE=InnoDB DEFAULT CHARSET=utf8;