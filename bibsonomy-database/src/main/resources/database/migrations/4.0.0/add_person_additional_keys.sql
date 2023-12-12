DROP TABLE IF EXISTS `person_additional_keys`;
CREATE TABLE `person_additional_keys` (
  `person_id` varchar(64) NOT NULL,
  `key_name` varchar(64) NOT NULL,
  `key_value` varchar(64) NOT NULL,
  UNIQUE KEY (`person_id`, `key_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;