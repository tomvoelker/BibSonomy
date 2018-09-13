ALTER TABLE `person` ALTER COLUMN gender ENUM('MALE','FEMALE');
UPDATE `person` set gender = 'MALE' where gender = 'm';
UPDATE `person` set gender = 'FEMALE' where gender = 'F';

ALTER TABLE `log_person` ALTER COLUMN gender ENUM('MALE','FEMALE');
UPDATE `log_person` set gender = 'MALE' where gender = 'm';
UPDATE `log_person` set gender = 'FEMALE' where gender = 'F';