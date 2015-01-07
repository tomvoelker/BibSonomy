ALTER TABLE log_bibtex ADD `current_content_id` int(10) unsigned NOT NULL default '0';
ALTER TABLE log_bookmark ADD `current_content_id` int(10) unsigned NOT NULL default '0';
ALTER TABLE log_gold_standard ADD `current_content_id` int(10) unsigned NOT NULL default '0';