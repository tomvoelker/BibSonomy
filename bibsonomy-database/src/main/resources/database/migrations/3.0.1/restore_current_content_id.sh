#! /bin/bash
# adapt these three variables
# delete password after use
PW=
USER=
SOCKET=

for resource in bibtex bookmark gold_standard; do

echo
echo "-----------------------" 	
echo "Now updating $resource";

date;
echo "First Statement";
echo 
# those logged resources that have been logged last, i.e. where the next version is still in the respective active table $resource
# the join selects those posts x where there is no logged post y such that y has x's new_content as content_id (i.e. those where no successor can be found)
STATEMENT="UPDATE log_${resource} lba 
LEFT JOIN log_${resource} lbb ON (lba.new_content_id = lbb.content_id)
SET lba.current_content_id = lba.new_content_id
WHERE lbb.content_id IS NULL AND lba.new_content_id != 0;"

echo $STATEMENT
mysql -u $USER -p$PW bibsonomy -S $SOCKET -e "$STATEMENT"

echo
date;
echo
echo "Second Statement"

# those logged resources that have a logged successor for which the current_content_id has already been set
# we ignore deleted resources (new_content_id = 0)
STATEMENT="UPDATE log_${resource} lba 
JOIN log_${resource} lbb ON (lba.new_content_id = lbb.content_id)
SET lba.current_content_id = lbb.current_content_id
WHERE lbb.current_content_id != 0 AND lba.new_content_id != 0;"

echo $STATEMENT
a=1
while [ $a -gt 0 ]; do
    a=`mysql -u $USER -p$PW bibsonomy -S $SOCKET -e "${STATEMENT}; SELECT ROW_COUNT();" | head -n 4 | tail -n 1`;
echo    
date
echo $a;
done

done

