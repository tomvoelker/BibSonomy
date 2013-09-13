ALTER TABLE sync_services ADD COLUMN `ssl_dn` varchar(255) not null default='';
ALTER TABLE sync_services ADD COLUMN `secure_api` varchar(255);

DROP TABLE IF EXISTS `sync_services`;
CREATE TABLE `sync_services` (
  `uri` varchar(255) NOT NULL,
  `secure_api` varchar(255),
  `service_id` int(10) unsigned NOT NULL,
  `server` boolean NOT NULL,
  `ssl_dn` varchar(255),
  PRIMARY KEY (`service_id`),
  UNIQUE KEY (`uri`, `server`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;