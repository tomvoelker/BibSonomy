-- log_bibtex data
SELECT content_id, new_content_id
INTO OUTFILE 'PATH/log_bibtex.csv'
    FIELDS TERMINATED BY ','  
    ENCLOSED BY '\"'
    ESCAPED BY '\\'
    LINES TERMINATED BY '\n'
FROM log_bibtex;

-- log_bookmark
SELECT content_id, new_content_id
INTO OUTFILE 'PATH/log_bookmark.csv'
    FIELDS TERMINATED BY ','  
    ENCLOSED BY '\"'
    ESCAPED BY '\\'
    LINES TERMINATED BY '\n'
FROM log_bookmark;