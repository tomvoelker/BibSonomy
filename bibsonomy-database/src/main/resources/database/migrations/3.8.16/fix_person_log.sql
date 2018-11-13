ALTER TABLE `log_person` DROP PRIMARY KEY;
ALTER TABLE `log_person` DROP KEY `person_change_id`;
ALTER TABLE `log_person` ADD COLUMN `id` int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;