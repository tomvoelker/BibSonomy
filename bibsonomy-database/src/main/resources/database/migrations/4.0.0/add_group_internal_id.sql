-- adds the external id to the group database table (used for syncing groups with organisation units)
ALTER TABLE `groupids` ADD COLUMN `internal_id` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `pending_groupids` ADD COLUMN `internal_id` VARCHAR(255) DEFAULT NULL;