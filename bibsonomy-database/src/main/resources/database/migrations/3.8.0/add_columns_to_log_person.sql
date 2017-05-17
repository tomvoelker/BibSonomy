ALTER TABLE `log_person` ADD `college` VARCHAR(128) DEFAULT NULL AFTER `orcid`;
ALTER TABLE `log_person` ADD `email` VARCHAR(255) DEFAULT NULL AFTER `college`;
ALTER TABLE `log_person` ADD `homepage` VARCHAR(255) DEFAULT NULL AFTER `email`;