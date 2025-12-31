SET GLOBAL time_zone = 'Europe/Paris';
SET GLOBAL default_storage_engine = 'InnoDB';
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES';
SET GLOBAL sql_mode = CONCAT(@@global.sql_mode, ',NO_ZERO_DATE');
SET GLOBAL sql_mode = CONCAT(@@global.sql_mode, ',NO_AUTO_CREATE_USER');
SET GLOBAL sql_mode = CONCAT(@@global.sql_mode, ',NO_ENGINE_SUBSTITUTION');
SET GLOBAL sql_mode = CONCAT(@@global.sql_mode, ',NO_ZERO_IN_DATE');

CREATE DATABASE IF NOT EXISTS main_db;
CREATE DATABASE IF NOT EXISTS item_recommender_db;
CREATE DATABASE IF NOT EXISTS tag_recommender_db;
CREATE DATABASE IF NOT EXISTS bibsonomy_unit_test;

CREATE USER IF NOT EXISTS 'bibsonomy'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON main_db.* TO 'bibsonomy'@'%';
GRANT ALL PRIVILEGES ON item_recommender_db.* TO 'bibsonomy'@'%';
GRANT ALL PRIVILEGES ON tag_recommender_db.* TO 'bibsonomy'@'%';
GRANT ALL PRIVILEGES ON bibsonomy_unit_test.* TO 'bibsonomy'@'%';
FLUSH PRIVILEGES;
