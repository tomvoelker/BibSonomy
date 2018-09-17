ALTER TABLE `groupids` ADD `parent` int(10) DEFAULT NULL;
ALTER TABLE `pending_groupids` ADD `parent` int(10) DEFAULT NULL;

--
-- Table structure for table `group_hierarchy`
--

DROP TABLE IF EXISTS `group_hierarchy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `group_hierarchy` (
  `child_group_id` int(10) NOT NULL,
  `parent_group_id` int(10) NOT NULL,
  FOREIGN KEY (`child_group_id`) REFERENCES `groupids`(`group`),
  FOREIGN KEY (`parent_group_id`) REFERENCES `groupids`(`group`),
  INDEX(`child_group_id`),
  INDEX(`parent_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

INSERT INTO ids (`name`, `value`, `description`) VALUES (20, (SELECT max(`group`) + 1 FROM groupids), 'group_id')