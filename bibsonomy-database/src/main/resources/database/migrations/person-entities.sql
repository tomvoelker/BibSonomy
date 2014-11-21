CREATE TABLE pub_person (
	id int(10) unsigned COMMENT 'sequential number. Also ensures the order of changes in the log table',
	simhash1 char(32) COMMENT '(interHash)',
	simhash2 char(32) COMMENT '(intraHash)',
	relator_code char(4) COMMENT 'marc21 relator code (prefix M + 3 marc21 chars) - see http://www.loc.gov/marc/relators/relacode.html. Particulary relevant are:
 Mths=Thesis advisor,
 Mrev=Reviewer,
 Moth=Other,
 Maut=Author.
In addition, we use
 Bmnm=main name (only one tuple with this value per person_id) - usually marks the current real name (with hashes set to null)',
	qualifying tinyint COMMENT 'set to
0 for any publication
1 for the first work associated to some newly created person entity
2 for a person without a publication
10 for a bachelor thesis @mastersthesis or @phdthesis with (lowercase) type field containing “bachelor”
20 for master thesis @mastersthesis with or without, or @phdthesis with (lowercase) type field containing “master”
30 for phdthesis
one single tuple per person id (the one with the highest value by the scoring above) is increased by +50. Whenever an entry is added or removed this is updated.',
	person_first_name varchar(64) COMMENT 'usually the first name as appearing in the publication. includes middle_name',
	person_last_name varchar(64) COMMENT 'usually the last name as appearing in the publication. Tuples with all simhashes set to null store additional names (and allow persons without publications to be found by the same means). For those null-tuples, the field may contains the full name if first/last distinction is not appropriate',
	person_id int,
	change_date timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	changed_by varchar(30) COMMENT 'user_name of the user, who changed the association last',
	deleted tinyint COMMENT 'set to 1 for tuples keeping track of deleted associations, otherwise 0',
	primary key (id)
);

CREATE TABLE log_pub_person (
	id int(10) unsigned COMMENT 'sequential number. Also ensures the order of changes in the log table',
	simhash1 char(32) COMMENT '(interHash)',
	simhash2 char(32) COMMENT '(intraHash)',
	relator_code char(4) COMMENT 'marc21 relator code (prefix M + 3 marc21 chars) - see http://www.loc.gov/marc/relators/relacode.html. Particulary relevant are:
 Mths=Thesis advisor,
 Mrev=Reviewer,
 Moth=Other,
 Maut=Author.
In addition, we use
 Bmnm=main name (only one tuple with this value per person_id) - usually marks the current real name (with hashes set to null)',
	qualifying tinyint COMMENT 'set to
0 for any publication
1 for the first work associated to some newly created person entity
2 for a person without a publication
10 for a bachelor thesis @mastersthesis or @phdthesis with (lowercase) type field containing “bachelor”
20 for master thesis @mastersthesis with or without, or @phdthesis with (lowercase) type field containing “master”
30 for phdthesis
one single tuple per person id (the one with the highest value by the scoring above) is increased by +50. Whenever an entry is added or removed this is updated.',
	person_first_name varchar(64) COMMENT 'usually the first name as appearing in the publication. includes middle_name',
	person_last_name varchar(64) COMMENT 'usually the last name as appearing in the publication. Tuples with all simhashes set to null store additional names (and allow persons without publications to be found by the same means). For those null-tuples, the field may contains the full name if first/last distinction is not appropriate',
	person_id int,
	change_date timestamp NOT NULL,
	changed_by varchar(30) COMMENT 'user_name of the user, who changed the association last',
	deleted tinyint COMMENT 'set to 1 for tuples keeping track of deleted associations, otherwise 0',
	log_date timestamp NOT NULL default CURRENT_TIMESTAMP,
	primary key (id)
);

CREATE TABLE person (
	person_id int,
	academic_degree varchar(64),
	user_name varchar(30),
	sameAs relation to user account,
	post_ctr int COMMENT 'nr of posts (=#content_ids), the person is connected to (for popular authors)'
	orcid char(16) default null,
	change_date timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	changed_by varchar(30),
	primary key (person_id)
);

CREATE TABLE log_person (
	person_id int,
	academic_degree varchar(64),
	user_name varchar(30),
	sameAs relation to user account,
	orcid char(16) default null,
	change_date timestamp NOT NULL,
	changed_by varchar(30),
	log_date timestamp NOT NULL default CURRENT_TIMESTAMP,
	deleted tinyint COMMENT 'set to 1 for tuples keeping track of deleted associations, otherwise 0',
	primary key (person_id, log_modified_at)
);