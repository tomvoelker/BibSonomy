groupstags:

SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = ?
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC

SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = ? 
    AND t.user_name = ?
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC


SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = ?
  GROUP BY t.tag_name 
  ORDER BY tag_name

SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = ? 
    AND t.user_name = ?
  GROUP BY t.tag_name 
  ORDER BY tag_name


relatedtags:

SELECT t2, ctr 
  FROM tagtag 
  WHERE t1 = ? 
  ORDER BY ctr DESC 
  LIMIT 10


relatedusertags:

SELECT tt.tag_name,count(tt.tag_name) AS tag_anzahl
  FROM tas t, tas tt
  WHERE tt.content_id=t.content_id
    AND t.user_name=?
    AND t.tag_name=?
    AND tt.tag_name!=?
  GROUP BY tt.tag_name
  ORDER BY tag_anzahl DESC 
  LIMIT 10


userstags:

SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.user_name = ?
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC

SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.user_name = ?
  GROUP BY t.tag_name 
  ORDER BY tag_name


tags:

SELECT tag_name, tag_ctr 
  FROM tags 
  ORDER BY tag_ctr DESC 
  LIMIT ${5*itemsPerPart}


popular:

SELECT u.book_url_ctr, u.book_url, b.book_url_hash, b.group, b.book_description, b.book_extended, b.user_name, b.date, b.content_id 
  FROM bookmark b, urls u 
  WHERE b.group=0 
    AND b.book_url_hash=u.book_url_hash 
    AND DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= b.date
  GROUP BY b.book_url_hash
  ORDER BY u.book_url_ctr DESC
  LIMIT 5


