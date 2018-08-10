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
  `updated_by` varchar(30) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- insert the new id into the database
INSERT INTO `ids` VALUES
	(19, 0, 'cris_link_id');