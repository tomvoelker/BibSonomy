CREATE INDEX `current_content_id_user_name` ON `log_bibtex` (`current_content_id`,`user_name`);

CREATE INDEX `current_content_id_user_name` ON `log_bookmark` (`current_content_id`,`user_name`);

CREATE INDEX `current_content_id` ON `log_gold_standard` (`current_content_id`);

CREATE INDEX `content_id_idx` ON `log_tas` (`content_id`);
