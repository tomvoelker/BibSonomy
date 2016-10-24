ALTER TABLE `sync` ADD `already_synced_once` TINYINT(1) NULL DEFAULT '0' AFTER `autosync`;

-- we have to dump the done states for each system and each client system must set the already_synced_once flag 