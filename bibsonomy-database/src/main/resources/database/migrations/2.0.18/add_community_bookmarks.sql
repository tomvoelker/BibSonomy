-- remove 'publications' table name
RENAME TABLE gold_standard_publications TO gold_standard;
RENAME TABLE log_gold_standard_publications TO log_gold_standard;
-- add column 'content_type'
ALTER TABLE gold_standard ADD COLUMN `content_type` tinyint(1) unsigned default NULL;
ALTER TABLE log_gold_standard ADD COLUMN `content_type` tinyint(1) unsigned default NULL;
-- every table entry should be a publication
UPDATE gold_standard SET `content_type` = 2;
UPDATE log_gold_standard SET `content_type` = 2;