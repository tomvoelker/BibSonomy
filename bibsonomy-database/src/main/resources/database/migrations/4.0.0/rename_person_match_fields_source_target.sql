ALTER TABLE `person_match` CHANGE COLUMN `person1_id` `target_person_id` varchar(64) NOT NUll;
ALTER TABLE `person_match` CHANGE COLUMN `person2_id` `source_person_id` varchar(64) NOT NUll;