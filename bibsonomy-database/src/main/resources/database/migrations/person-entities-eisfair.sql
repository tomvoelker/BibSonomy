CREATE TABLE `person` (
  `person_id` varchar(64) NOT NULL,
  `academic_degree` varchar(64) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `post_ctr` int(11) DEFAULT NULL,
  `orcid` char(16) DEFAULT NULL,
  `log_modified_at` datetime DEFAULT NULL,
  `log_modified_by` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;

CREATE TABLE `log_person` (
  `id` int(10) unsigned NOT NULL,
  `person_id` varchar(64) NOT NULL,
  `academic_degree` varchar(64) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `orcid` char(16) DEFAULT NULL,
  `log_modified_at` datetime DEFAULT NULL,
  `log_modified_by` varchar(30) DEFAULT NULL,
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'set to 1 for delete actions',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;

CREATE TABLE `pub_person` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'sequential number. Also ensures the order of changes in the log table. TODO: replace auto-increment',
  `simhash1` char(32) DEFAULT NULL COMMENT '(interHash)',
  `simhash2` char(32) DEFAULT NULL COMMENT '(intraHash)',
  `relator_code` char(4) DEFAULT NULL COMMENT 'marc21 relator code (prefix M + 3 marc21 chars) - see http://www.loc.gov/marc/relators/relacode.html. Particulary relevant are:\n Mths=Thesis advisor,\n Mrev=Reviewer,\n Moth=Other,\n Maut=Author.\nIn addition, we use\n Bmnm=main name (only one tuple with this value per person_id) - usually marks the current real name (with hashes set to null)',
  `person_index` tinyint(4) NOT NULL COMMENT 'tuple refers to the nth author/editor as appearing in the bibtex fields (n=author_index).',
  `person_id` varchar(64) NOT NULL,
  `qualifying` tinyint(4) DEFAULT NULL COMMENT 'set to\n0 for any publication\n1 for the first work associated to some newly created person entity\n2 for a person without a publication\n10 for a bachelor thesis @mastersthesis or @phdthesis with (lowercase) type field containing “bachelor”\n20 for master thesis @mastersthesis with or without, or @phdthesis with (lowercase) type field containing “master”\n30 for phdthesis\none single tuple per person id (the one with the highest value by the scoring above) is increased by +50. Whenever an entry is added or removed this is updated.',
  `log_changed_at` datetime DEFAULT NULL,
  `log_changed_by` varchar(30) DEFAULT NULL COMMENT 'user_name of the user, who changed the association last',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'set to 1 for tuples keeping track of explicitly falsified associations, otherwise 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8;

CREATE INDEX `pub_person_simhash1_relator_code_person_index_idx` ON `pub_person` (`simhash1`, `relator_code`, `person_index`)

CREATE TABLE `log_pub_person` (
  `id` int(10) unsigned NOT NULL COMMENT 'sequential number. Also ensures the order of changes in the log table',
  `simhash1` char(32) DEFAULT NULL COMMENT '(interHash)',
  `simhash2` char(32) DEFAULT NULL COMMENT '(intraHash)',
  `relator_code` char(4) DEFAULT NULL COMMENT 'marc21 relator code (prefix M + 3 marc21 chars) - see http://www.loc.gov/marc/relators/relacode.html. Particulary relevant are:\n Mths=Thesis advisor,\n Mrev=Reviewer,\n Moth=Other,\n Maut=Author.\nIn addition, we use\n Bmnm=main name (only one tuple with this value per person_id) - usually marks the current real name (with hashes set to null)',
  `person_index` tinyint(4) NOT NULL COMMENT 'tuple refers to the nth author/editor as appearing in the bibtex fields (n=author_index).',
  `person_id` varchar(64) NOT NULL,
  `qualifying` tinyint(4) DEFAULT NULL COMMENT 'set to\n0 for any publication\n1 for the first work associated to some newly created person entity\n2 for a person without a publication\n10 for a bachelor thesis @mastersthesis or @phdthesis with (lowercase) type field containing “bachelor”\n20 for master thesis @mastersthesis with or without, or @phdthesis with (lowercase) type field containing “master”\n30 for phdthesis\none single tuple per person id (the one with the highest value by the scoring above) is increased by +50. Whenever an entry is added or removed this is updated.',
  `log_changed_at` datetime DEFAULT NULL,
  `log_changed_by` varchar(30) DEFAULT NULL COMMENT 'user_name of the user, who changed the association last',
  `deleted` tinyint(4) NOT NULL COMMENT 'set to 1 for tuples keeping track of explicitly falsified associations, otherwise 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `person_name` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) NOT NULL,
  `person_id` varchar(64) NOT NULL,
  `is_main` tinyint(1) DEFAULT '0',
  `log_changed_at` datetime DEFAULT NULL,
  `log_changed_by` varchar(30) DEFAULT NULL COMMENT 'user_name of the user, who changed the tuple last',
  PRIMARY KEY (`id`),
  CONSTRAINT `person_name.person_id` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;

CREATE TABLE `log_person_name` (
  `id` int(10) unsigned NOT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) NOT NULL,
  `person_id` varchar(64) NOT NULL,
  `is_main` tinyint(1) DEFAULT '0',
  `log_changed_at` datetime DEFAULT NULL,
  `log_changed_by` varchar(30) DEFAULT NULL COMMENT 'user_name of the user, who changed the tuple last',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;