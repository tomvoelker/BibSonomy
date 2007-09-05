
Warum wird bei dieser Anfrage innen ein GROUP BY gemacht? Durch das WHERE Statement wird doch
maximal Zeile ausgewählt.

SELECT tmp.tag_name AS upperconcept, t3.tag_name AS lowerconcept, tmp.rangordnung 
  FROM 
    (SELECT t.tag_name, COUNT(t.tag_name) AS rangordnung 
      FROM tas t, tagtagrelations tr 
      WHERE t.tag_id=tr.tagID2 
      GROUP BY t.tag_name 
      ORDER BY rangordnung DESC 
      LIMIT 20) tmp, 
    tas t2, tas t3, tagtagrelations tr2 
  WHERE t2.tag_name=tmp.tag_name 
    AND t2.tag_ID=tr2.tagID2 
    AND t3.tag_ID=tr2.tagID1 
  GROUP BY tmp.rangordnung DESC, upperconcept, lowerconcept


SELECT t1.tag_name AS upperconcept, t2.tag_name AS lowerconcept
  FROM tas t1, tas t2, tagtagrelations tr 
  WHERE t1.tag_id=tr.tagID2 
    AND t2.tag_id=tr.tagID1
    AND tr.user_name=?
    AND t1.user_name=tr.user_name
    AND t2.user_name=tr.user_name
  GROUP BY upperconcept, lowerconcept
  LIMIT ? OFFSET ?


