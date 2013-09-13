CREATE TEMPORARY TABLE lost_gold_standard_bookmarks AS
	SELECT 1 AS content_id, 0 AS `group`, CURRENT_TIMESTAMP AS date, 1 AS content_type, b.book_url_hash AS simhash1, mu.book_url AS url, book_extended AS description, book_description AS title
	FROM bookmark b
	JOIN (SELECT * FROM urls u JOIN (SELECT DISTINCT interHash FROM discussion) AS m ON u.book_url_hash = interHash) AS mu ON b.book_url_hash = mu.book_url_hash GROUP BY b.book_url_hash;

SET autocommit=0;
START TRANSACTION;
--  get next content id
SELECT @A:=MAX(value)+1 FROM ids WHERE name = 0;
UPDATE lost_gold_standard_bookmarks SET content_id = @A:=@A+1;
UPDATE ids SET value = @A+1 WHERE name = 0;
COMMIT;

INSERT INTO gold_standard (content_id, `group`, date, content_type, simhash1, url, description, title) SELECT * FROM lost_gold_standard_bookmarks;

DROP TABLE lost_gold_standard_bookmarks;

-- now for publications
CREATE TEMPORARY TABLE lost_gold_standard_publications AS
	SELECT 1 AS content_id, 0 AS `group`, CURRENT_TIMESTAMP AS date, 2 AS content_type, simhash1, journal, volume, chapter, edition, month, day, booktitle, howPublished, institution, organization, publisher, address, school, series, bibtexKey, url, type,  description, annote, note, pages, bKey, number, crossref, misc, bibtexAbstract, entrytype, title, author, editor, year, scraperid
	FROM bibtex b
	JOIN (SELECT DISTINCT interHash FROM discussion) AS d ON b.simhash1 = d.interHash GROUP BY b.simhash1;

SET autocommit=0;
START TRANSACTION;
--  get next content id
SELECT @A:=MAX(value)+1 FROM ids WHERE name = 0;
UPDATE lost_gold_standard_publications SET content_id = @A:=@A+1;
UPDATE ids SET value = @A+1 WHERE name = 0;
COMMIT;

INSERT IGNORE INTO gold_standard (content_id, `group`, date, content_type, simhash1, journal, volume, chapter, edition, month, day, booktitle, howPublished, institution, organization, publisher, address, school, series, bibtexKey, url, type,  description, annote, note, pages, bKey, number, crossref, misc, bibtexAbstract, entrytype, title, author, editor, year, scraperid) SELECT * FROM lost_gold_standard_publications;

DROP TABLE lost_gold_standard_publications;