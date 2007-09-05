PAGE_HOME:

explain 
SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.user_name,bb.book_url,bb.book_url_ctr,t.tag_name 
  FROM 
    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,b.user_name,u.book_url,u.book_url_ctr 
      FROM bookmark b, urls u  
      WHERE b.group = 0 
        AND u.book_url_hash = b.book_url_hash 
      ORDER BY date DESC
      LIMIT 5) AS bb 
    LEFT OUTER JOIN tas AS t 
      ON t.content_id=bb.content_id 
  ORDER BY bb.date DESC, bb.content_id DESC;

explain 
SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,b.user_name,u.book_url,u.book_url_ctr,b.content_id 
  FROM bookmark b, urls u 
  WHERE b.group = 0 
    AND u.book_url_hash = b.book_url_hash 
  ORDER BY date DESC
  LIMIT 5;

### FILESORT! ###



PAGE_USER:

explain 
SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.book_url,bb.book_url_ctr,t.tag_name,g.group_name 
  FROM 
    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url,u.book_url_ctr,b.group 
      FROM bookmark b, urls u 
      WHERE u.book_url_hash=b.book_url_hash 
        AND b.user_name = 'joshua' 
	AND b.group = 0 
      ORDER BY date DESC 
      LIMIT 10) AS bb 
    LEFT OUTER JOIN tas AS t 
      ON t.content_id=bb.content_id, groups AS g 
  WHERE bb.group = g.group 
  ORDER BY bb.date DESC, bb.content_id DESC;

explain 
SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url,u.book_url_ctr,b.group 
  FROM bookmark b, urls u 
  WHERE u.book_url_hash=b.book_url_hash 
    AND b.user_name = 'joshua' 
    AND b.group = 0 
  ORDER BY date DESC 
  LIMIT 10;


### FILESORT! ###


PAGE_USERTAG:

explain 
SELECT t.tag_name,t.content_id,a.book_url,a.book_url_hash,a.book_description,a.book_extended,a.date,a.book_url_ctr,g.group_name  
  FROM 
    (SELECT b.content_id,u.book_url,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url_ctr,b.group 
      FROM bookmark b, urls u,  tas t1 
      WHERE  t1.tag_name='web' 
        AND t1.group in (0) 
	AND t1.content_type=1 
	AND t1.user_name = 'joshua' 
	AND b.book_url_hash=u.book_url_hash 
	AND t1.content_id=b.content_id 
      ORDER BY t1.date DESC 
      LIMIT 10) AS a 
    LEFT OUTER JOIN tas AS t 
      ON a.content_id=t.content_id, groups AS g  
  WHERE a.group = g.group   
  ORDER BY a.date DESC, a.content_id DESC;

explain 
SELECT * 
  FROM bookmark b, urls u, tas t1 
  WHERE t1.tag_name='web' 
    AND t1.group in (0) 
    AND t1.content_type=1 
    AND t1.user_name = 'joshua' 
    AND b.book_url_hash=u.book_url_hash 
    AND t1.content_id=b.content_id 
  ORDER BY t1.date DESC 
  LIMIT 10;

### FILESORT! ###



PAGE_TAG:

explain 
SELECT t.tag_name,t.content_id,b.book_description,b.book_extended,b.user_name,b.date,b.book_url_hash,u.book_url,u.book_url_ctr  
  FROM bookmark b, urls u, tas t,     
    (SELECT t1.content_id      
      FROM  tas t1 
      WHERE  t1.tag_name='web'       
        AND t1.content_type=1      
	AND t1.group = 0      
      ORDER BY t1.date DESC 
      LIMIT 10) AS tt    
   WHERE b.content_id=tt.content_id      
     AND t.content_id=tt.content_id      
     AND b.book_url_hash=u.book_url_hash      
   ORDER BY b.date DESC, b.content_id DESC;

explain 
SELECT t1.content_id 
  FROM tas t1 
  WHERE t1.tag_name='web' 
    AND t1.content_type=1 
    AND t1.group = 0 
  ORDER BY t1.date DESC 
  LIMIT 10;

### FILESORT! ###


PAGE_URL:

explain 
SELECT t.tag_name,b.content_id,b.book_description,b.book_extended,b.user_name,b.date,b.book_url_hash,u.book_url,u.book_url_ctr  
  FROM    
    (SELECT book_url_hash,book_description,book_extended,user_name,date,content_id      
      FROM bookmark       
      WHERE book_url_hash='0904d9d994c2d37f07bcf392cb45689f' 
        AND bookmark.group = 0      
      ORDER BY date DESC 
      LIMIT 10) AS b  
    LEFT OUTER JOIN tas t 
      ON b.content_id=t.content_id, urls u      
  WHERE b.book_url_hash=u.book_url_hash      
  ORDER BY b.date DESC, b.content_id DESC;

explain 
SELECT * 
  FROM bookmark 
  WHERE book_url_hash='0904d9d994c2d37f07bcf392cb45689f' 
    AND bookmark.group = 0      
  ORDER BY date DESC 
  LIMIT 10; 

### FILESORT! ###



PAGE_GROUP_USER:

explain 
SELECT bb.content_id,bb.user_name,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.book_url,bb.book_url_ctr,t.tag_name,g.group_name  
  FROM    
    (SELECT b.content_id,b.user_name,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url,u.book_url_ctr,b.group       
      FROM bookmark b, urls u       
      WHERE u.book_url_hash=b.book_url_hash 
        AND b.group = 'public' 
	AND b.user_name = 'joshua'       
      ORDER BY date DESC       
      LIMIT 10) AS bb    
    LEFT OUTER JOIN tas AS t 
      ON t.content_id=bb.content_id, 
    groups AS g    
  WHERE bb.group = g.group 
  ORDER BY bb.date DESC, bb.content_id DESC;

explain 
SELECT * 
  FROM bookmark b, urls u 
  WHERE u.book_url_hash=b.book_url_hash 
    AND b.group = 'public' 
    AND b.user_name = 'joshua'       
  ORDER BY date DESC       
  LIMIT 10; 

### FILESORT! ###



PAGE_GROUP:

explain 
SELECT bb.content_id,bb.user_name,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.book_url,bb.book_url_ctr,t.tag_name,g.group_name  
  FROM    
    (SELECT b.content_id,b.user_name,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url,u.book_url_ctr,b.group       
      FROM bookmark b, urls u       
      WHERE u.book_url_hash=b.book_url_hash 
        AND b.group = 3       
      ORDER BY date DESC       
      LIMIT 10) AS bb    
    LEFT OUTER JOIN tas AS t 
      ON t.content_id=bb.content_id, 
    groups AS g    
  WHERE bb.group = g.group 
  ORDER BY bb.date DESC, bb.content_id DESC; 

explain 
SELECT * 
  FROM bookmark b, urls u       
  WHERE u.book_url_hash=b.book_url_hash 
    AND b.group = 3       
  ORDER BY date DESC       
  LIMIT 10; 

### FILESORT! ###


--------------------------- Boxen und extra Seiten -------------------------


groupstags:

explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = 3
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC;

explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = 0
    AND t.user_name = 'joshua'
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC;

(falscher Index wird genommen)


explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = 3
  GROUP BY t.tag_name 
  ORDER BY tag_name;

explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.group = 0 
    AND t.user_name = 'joshua'
  GROUP BY t.tag_name 
  ORDER BY tag_name;

(falscher Index wird genommen)


relatedtags:

explain SELECT t2, ctr 
  FROM tagtag 
  WHERE t1 = 'web'
  ORDER BY ctr DESC 
  LIMIT 10;

### KEIN FILESORT ###

relatedusertags:

explain SELECT tt.tag_name,count(tt.tag_name) AS tag_anzahl
  FROM tas t, tas tt
  WHERE tt.content_id=t.content_id
    AND t.user_name='joshua'
    AND t.tag_name='web'
    AND tt.tag_name!='web'
  GROUP BY tt.tag_name
  ORDER BY tag_anzahl DESC 
  LIMIT 10;

### FALSCHE INDIZES ###


userstags:

explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.user_name = 'joshua'
  GROUP BY t.tag_name 
  ORDER BY tag_anzahl DESC;

explain SELECT tag_name, count(tag_name) AS tag_anzahl
  FROM tas t 
  WHERE t.user_name = 'joshua'
  GROUP BY t.tag_name 
  ORDER BY tag_name;


tags:

explain SELECT tag_name, tag_ctr 
  FROM tags 
  ORDER BY tag_ctr DESC 
  LIMIT 10;


popular:

explain SELECT u.book_url_ctr, u.book_url, b.book_url_hash, b.group, b.book_description, b.book_extended, b.user_name, b.date, b.content_id 
  FROM bookmark b, urls u 
  WHERE b.group=0 
    AND b.book_url_hash=u.book_url_hash 
    AND DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= b.date
  GROUP BY b.book_url_hash
  ORDER BY u.book_url_ctr DESC
  LIMIT 5;




