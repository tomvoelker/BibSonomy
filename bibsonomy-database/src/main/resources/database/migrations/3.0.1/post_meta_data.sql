ALTER TABLE post_metadata ADD COLUMN `content_id` int(10) unsigned NOT NULL;
ALTER TABLE post_metadata ADD COLUMN `ref_content_id` int(10) unsigned NULL;

-- FIXME: add queries to fill content id and ref content id and remove interhash and intrahash columns!