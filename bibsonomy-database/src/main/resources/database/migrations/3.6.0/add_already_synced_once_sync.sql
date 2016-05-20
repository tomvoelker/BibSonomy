ALTER TABLE `sync` ADD `already_synced_once` TINYINT(1) NULL DEFAULT '0' AFTER `autosync`;

UPDATE sync SET already_synced_once = 1;