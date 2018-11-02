ALTER TABLE `log_person_name` ADD COLUMN `log_date` datetime DEFAULT NULL;
ALTER TABLE `log_person_name` ADD COLUMN `edited_by` varchar(30) DEFAULT NULL;