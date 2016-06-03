ALTER TABLE `sync` ADD `already_synced_once` TINYINT(1) NULL DEFAULT '0' AFTER `autosync`;

UPDATE sync s INNER JOIN
	( 	SELECT DISTINCT sd.service_id, sd.user_name
		FROM sync_data sd WHERE sd.status = "done"
	) 	AS s2 ON s2.service_id = s.service_id and s2.user_name = s.user_name
SET s.already_synced_once = 1;