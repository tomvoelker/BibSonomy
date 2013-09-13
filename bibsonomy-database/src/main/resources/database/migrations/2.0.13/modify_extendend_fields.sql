DROP extended_fields_map;

CREATE TABLE `extended_fields_data` (
 `key` varchar(255) NOT NULL,
 `value` text NOT NULL,
 `content_id` int(10) unsigned NOT NULL,
 `date_of_create` datetime NOT NULL,
 `date_of_last_mod` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
 KEY `idx_content_id_key` (`content_id`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;