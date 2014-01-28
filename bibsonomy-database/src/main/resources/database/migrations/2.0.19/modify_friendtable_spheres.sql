-- neuen unique key anlegen
CREATE UNIQUE INDEX `unique_friendship2` ON friends (`user_name`,`f_user_name`,`tag_name`);
-- ... und den alten entfernen
ALTER TABLE friends DROP INDEX `unique_friendship`;
