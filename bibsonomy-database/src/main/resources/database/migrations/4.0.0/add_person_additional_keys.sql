CREATE TABLE `person_additional_keys` (
  `person_id` varchar(64) NOT NULL,
  `key` varchar(64) NOT NULL,
  `value` varchar(64) NOT NULL,
  UNIQUE KEY (`person_id`, `key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;