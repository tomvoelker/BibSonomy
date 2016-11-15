ALTER TABLE `person` ADD `college` VARCHAR(200) NULL AFTER `orcid`;
ALTER TABLE `person` ADD `email` VARCHAR(200) NULL AFTER `college`;
ALTER TABLE `person` ADD `homepage` VARCHAR(200) NULL AFTER `email`;