ALTER TABLE `groupids` ADD `allow_join` TINYINT(1) NULL DEFAULT '1' AFTER `sharedDocuments`;
ALTER TABLE `pending_groupids` ADD `allow_join` TINYINT(1) NULL DEFAULT '1' AFTER `sharedDocuments`;

ALTER TABLE `groupids` ADD `shortDescription` TEXT NULL AFTER `allow_join`;
ALTER TABLE `pending_groupids` ADD `shortDescription` TEXT NULL AFTER `allow_join`;