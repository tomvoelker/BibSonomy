CREATE TABLE `repository` (
  `inter_hash` char(32) NOT NULL default '',
  `intra_hash` char(32) NOT NULL default '',
  `repository_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(30) NOT NULL,
  `repository_name` varchar(30) NOT NULL,
  KEY  (`inter_hash`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;