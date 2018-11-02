ALTER TABLE `log_pub_person` ADD COLUMN `log_date` datetime DEFAULT NULL;
ALTER TABLE `log_pub_person` ADD COLUMN `edited_by` varchar(30) DEFAULT NULL;