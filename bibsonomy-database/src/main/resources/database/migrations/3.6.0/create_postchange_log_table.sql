CREATE TABLE `log_postchange` (
	`post_owner` VARCHAR(50) NULL DEFAULT NULL,
	`post_editor` VARCHAR(50) NOT NULL,
	`old_intrahash` VARCHAR(32) NOT NULL,
	`new_intrahash` VARCHAR(32) NOT NULL,
	`content_type` TINYINT(4) NOT NULL,
	`date` DATETIME NOT NULL
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
