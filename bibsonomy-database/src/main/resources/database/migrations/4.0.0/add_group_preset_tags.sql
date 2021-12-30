DROP TABLE IF EXISTS `group_preset_tags`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `group_preset_tags` (
  `tag_name` varchar(255) character set utf8 collate utf8_bin NOT NULL default '',
  `group_name` varchar(30) NOT NULL default '',
  `group` int(10) NOT NULL default '0',
  `description` varchar(255),
  UNIQUE KEY `unique_group_tag` (`tag_name`, `group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;