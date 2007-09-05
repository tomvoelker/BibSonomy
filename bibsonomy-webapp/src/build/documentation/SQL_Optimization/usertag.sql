SELECT t.tag_name,t.content_id,a.book_url,a.book_url_hash,a.book_description,a.book_extended,a.date,a.book_url_ctr,g.group_name  
  FROM 
    (SELECT b.content_id,u.book_url,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url_ctr,b.group      
      FROM bookmark b, urls u, tas t1, tas t2 
      WHERE t1.content_id=t2.content_id 
        AND t1.tag_name='web' 
        AND t2.tag_name='programming' 
        AND b.group in (0) 
        AND b.user_name = 'joshua' 
        AND b.book_url_hash=u.book_url_hash        
        AND t1.content_id=b.content_id      
      ORDER BY b.date DESC, b.content_id
      LIMIT 11 OFFSET 0) AS a   
    LEFT OUTER JOIN tas AS t 
      ON a.content_id=t.content_id, 
    groups AS g  
  WHERE a.group = g.group   
  ORDER BY a.date DESC, a.content_id;
