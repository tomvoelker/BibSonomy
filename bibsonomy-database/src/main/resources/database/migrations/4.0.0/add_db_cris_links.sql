DROP TABLE IF EXISTS `cris_links`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `cris_links` (
  `id` int(11) unsigned NOT NULL,
  `source_id` int(11) DEFAULT NULL,
  `source_type` int(2) DEFAULT NULL,
  `target_id` int(11) DEFAULT NULL,
  `target_type` int(2) DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `linktype_type` int(11) DEFAULT NULL,
  `linktype_value` varchar(30) DEFAULT NULL,
  `link_source` int(2) DEFAULT NULL,
  `updated_by` varchar(30) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `log_cris_links`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log_cris_links` (
  `id` int(11) unsigned NOT NULL,
  `source_id` int(11) DEFAULT NULL,
  `source_type` int(2) DEFAULT NULL,
  `target_id` int(11) DEFAULT NULL,
  `target_type` int(2) DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `linktype_type` int(11) DEFAULT NULL,
  `linktype_value` varchar(30) DEFAULT NULL,
  `link_source` int(2) DEFAULT NULL,
  `updated_by` varchar(30) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `new_id` int(11) NOT NULL,
  `log_date` timestamp NULL DEFAULT NULL,
  `log_user` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- insert the new id into the database
INSERT INTO `ids` VALUES
	(19, 0, 'cris_link_id');