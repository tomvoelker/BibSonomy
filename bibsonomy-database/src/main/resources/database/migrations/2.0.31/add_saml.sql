-- Table structure for table `samlUser`
--
DROP TABLE IF EXISTS `samlUser`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `samlUser`(
  `user_name` varchar(30) NOT NULL default '',
  `samlUserId` varchar(255) NOT NULL default '',
  `identity_provider` varchar(255) NOT NULL default '',
  `lastAccess` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY  (`samlUserId`, `identity_provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--