ALTER TABLE `sync` ADD `already_synced_once` TINYINT(1) NULL DEFAULT '0' AFTER `autosync`;

UPDATE sync s JOIN sync_data s2 ON s2.service_id = s.service_id AND s2.user_name = s.user_name SET s.already_synced_once = 1 WHERE s2.status = "done";