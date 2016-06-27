CREATE TABLE `log_postchange` (
	`post_owner` VARCHAR(50) NULL DEFAULT NULL,
	`post_editor` VARCHAR(50) NOT NULL,
	`old_content_id` INT(11) NOT NULL,
	`new_content_id` INT(11) NOT NULL,
	`current_content_id` INT(11) NOT NULL,
	`content_type` TINYINT(4) NOT NULL,
	`date` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
