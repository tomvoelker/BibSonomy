/* rename table groups to group_memberhips */
RENAME TABLE `groups` TO `group_memberships`;
/* add new columns */
ALTER TABLE `group_memberships` CHANGE COLUMN `userSharedDocuments` `user_shared_documents` TINYINT DEFAULT '0';
ALTER TABLE `group_memberships` CHANGE COLUMN `user_status` `group_role` INT DEFAULT '2';


/* rename logging table */
RENAME TABLE `log_groups` TO `log_group_memberships`;
ALTER TABLE `log_group_memberships`
	CHANGE COLUMN `user_status` `group_role` INT(10) NOT NULL DEFAULT '2' AFTER `end_date`,
	ADD COLUMN `user_shared_documents` TINYINT(1) NULL DEFAULT '0' AFTER `group_role`;

/* create table for pending groups */
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

/* create table for pending group memberships */
CREATE TABLE `pending_group_memberships` (
  `user_name` VARCHAR(30) NOT NULL DEFAULT '',
  `group` INT(10) NULL DEFAULT '0',
  `defaultgroup` INT(10) NULL DEFAULT '0',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `group_role` INT(10) NOT NULL DEFAULT '2',
  `user_shared_documents` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`user_name`, `group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `group_level_permission` (
  `group` int(10) DEFAULT NULL,
  `permission` tinyint(1) DEFAULT NULL,
  `granted_by` VARCHAR(30) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`group`, permission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;