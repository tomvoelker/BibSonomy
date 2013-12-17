-- add cplumn to discussion table
ALTER TABLE discussion ADD content_type tinyint(1) unsigned NOT NULL DEFAULT 0;
ALTER TABLE log_discussion ADD content_type tinyint(1) unsigned NOT NULL DEFAULT 0;

-- set content_type
UPDATE discussion AS d SET d.content_type = (SELECT content_type FROM gold_standard WHERE d.interHash = simhash1);
UPDATE log_discussion AS d SET d.content_type = (SELECT content_type FROM gold_standard WHERE d.interHash = simhash1);
