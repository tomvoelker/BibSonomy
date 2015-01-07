RENAME TABLE `groups` TO `group_memberships`;
ALTER TABLE `group_memberships` CHANGE COLUMN `userSharedDocuments` `user_shared_documents` TINYINT;
ALTER TABLE `group_memberships` CHANGE COLUMN `user_status` `group_role` INT;

CREATE TABLE `pending_groupids` (
  `group_name` VARCHAR(30) NOT NULL DEFAULT '',
  `request_user_name` VARCHAR(30) NOT NULL,
  `request_reason` TEXT NOT NULL,
  `request_submission_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `group` INT(10) NOT NULL DEFAULT '0',
  `privlevel` TINYINT(3) UNSIGNED NULL DEFAULT '1',
  `sharedDocuments` TINYINT(1) NULL DEFAULT '0',
  `publ_reporting_mail` VARCHAR(255) NULL DEFAULT NULL,
  `publ_reporting_mail_template` TEXT NULL,
  `publ_reporting_external_url` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pending_group_memberships` (
  `user_name` VARCHAR(30) NOT NULL DEFAULT '',
  `group` INT(10) NULL DEFAULT '0',
  `defaultgroup` INT(10) NULL DEFAULT '0',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `group_role` INT(10) NOT NULL DEFAULT '7',
  `user_shared_documents` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- set the dummy user role
UPDATE groupids
  JOIN user ON group_name = user_name
  JOIN group_memberships on groupids.`group` = group_memberships.`group` AND group_memberships.`user_name` = group_name
SET group_role = 2;