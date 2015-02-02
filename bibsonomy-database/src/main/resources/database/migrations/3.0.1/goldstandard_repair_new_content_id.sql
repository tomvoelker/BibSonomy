CREATE TABLE tmp_gold_standard AS
SELECT lgs.content_id, lgs.simhash1
FROM log_gold_standard lgs;

INSERT INTO tmp_gold_standard
SELECT gs.content_id, gs.simhash1
FROM gold_standard gs;

UPDATE log_gold_standard lgs
JOIN (SELECT tgs2.content_id, tgs2.simhash1 
  FROM tmp_gold_standard tgs2
  ORDER BY content_id ASC) tgs
ON (tgs.simhash1  = lgs.new_simhash1 AND tgs.content_id > lgs.content_id)
SET lgs.new_content_id = tgs.content_id
WHERE lgs.new_content_id = 0;

DROP TABLE tmp_gold_standard;
