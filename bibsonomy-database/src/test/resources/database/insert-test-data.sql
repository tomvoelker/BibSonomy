--
-- BibSonomy Test Database
--
-- This SQL script fills in some test data for the unit tests of the database 
-- module. It expects the existence of all tables; their create statements
-- are found in src/main/resources/bibsonomy_db_schema.sql
--
-- 



-- 
-- Data for table `classifier_settings`
--

INSERT INTO `classifier_settings` (`key`, `value`) VALUES 
('algorithm', 'weka.classifiers.lazy.IBk'),
('mode', 'D');

-- 
-- Data for table `DBLP`
-- 

INSERT INTO `DBLP` VALUES 
('1815-12-10 00:00:00');



-- 
-- Data for table `bibhash`
-- 

INSERT INTO `bibhash` (`hash`, `ctr`, `type`) VALUES
('36a19ee7b7923b062a99a6065fe07792', 1, 0),
('8711751127efb070ee910a5d145a168b', 1, 0),
('92e8d9c7588eced69419b911b31580ee', 1, 0),
('9abf98937435f05aec3d58b214a2ac58', 1, 0),
('b386bdfc8ac7b76ca96e6784736c4b95', 1, 0),
('96c7cf1a752564f8ae0b6540e131af73', 1, 1),
('ac6aa3ccb181e61801cefbc1401d409a', 1, 1),
('e2fb0763068b21639c3e36101f64aefe', 1, 1),
('097248439469d8f5a1e7fad6b02cbfcd', 2, 1),
('1b298f199d487bc527a62326573892b8', 1, 2),
('522833042311cc30b8775772335424a7', 1, 2),
('65e49a5791c3dae2356d26fb9040fe29', 1, 2),
('b71d5283dc7f4f59f306810e73e9bc9a', 1, 2),
('598347d95a3ef39a3987a39b40708f79', 2, 0),
('a5936835f9eeab91eb09d84948306178', 2, 1),
('15a1bdcbff44431651957f45097dc4f4', 2, 2),
('319872adc49bfeae3f799d29a18b0634', 1, 1),
('11db3d75b9e07960658984f9b012d6d7', 1, 2),
('08cdf0d0dcce9d07fd8d41ac6267cadf', 1, 2),
('564c7c31f6a34e5dc3a1b0c17fe68c13', 1, 1),
('b77ddd8087ad8856d77c740c8dc2864a', 1, 2),
('0b539e248a02e3edcfe591c64346c7a0',1,1),
('3dc3dbb9d263a95a53b7134718b2b7f2',1,1),
('d63038ea59383b94bb52fc4a9b76d1f5',1,2),
('dd8930d9b1db11a87305923e069b77b6',1,2);


-- 
-- Data for table `bibtex`
-- 

INSERT INTO `bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`,      `day`,      `booktitle`,                 `howPublished`,      `institution`,  `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `group`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `entrytype`, `title`, `author`, `editor`, `year`, `privnote`, `scraperid`, `change_date`, `rating`) VALUES
(10, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '1815-12-10 00:00:00', 'testuser1',   'http://www.testurl.org',        '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '9abf98937435f05aec3d58b214a2ac58', '097248439469d8f5a1e7fad6b02cbfcd', 'b77ddd8087ad8856d77c740c8dc2864a', '', 'test entrytype', 'test title',         'test author',  'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:24:55', 0),
(11, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test spam booktitle',       'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 1, '1815-12-10 00:00:00', 'testspammer', 'test url',                      '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'b386bdfc8ac7b76ca96e6784736c4b95', '96c7cf1a752564f8ae0b6540e131af73', '65e49a5791c3dae2356d26fb9040fe29', '', 'test entrytype', 'test spam title',    'test spammer', 'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:34:34', 0),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 3, '1815-12-10 00:00:00', 'testuser1',   'http://www.testurl.org',        '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '92e8d9c7588eced69419b911b31580ee', '097248439469d8f5a1e7fad6b02cbfcd', '522833042311cc30b8775772335424a7', '', 'test entrytype', 'test title',         'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-05 18:11:55', 0),
(13, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 1, '1815-12-10 00:00:00', 'testuser2',   'http://private.bibtex.url.com', '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '8711751127efb070ee910a5d145a168b', 'ac6aa3ccb181e61801cefbc1401d409a', '1b298f199d487bc527a62326573892b8', '', 'test entrytype', 'test private title', 'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0),
(14, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 2, '1815-12-10 00:00:00', 'testuser2',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0),
(20, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '2009-10-08 14:35:01', 'testuser3',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', '891518b4900cd1832d77a0c8ae20dd14', '', 'inproceedings1', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2009-10-08 14:35:01', 0),
(100, NULL, '216', NULL, NULL, 'nov', NULL, 'Proceedings of the OWLED*06 Workshop on OWL: Experiences and Directions', NULL, NULL, NULL, NULL, NULL, NULL, 'CEUR-WS.org', 'elsenbroich2006abductive', 0, '2011-08-08 09:24:38', 'jaeschke', 'http://www.cs.man.ac.uk/~okutz/case-for-abduction.pdf', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, '  issn = {1613-0073}', 'We argue for the usefulness of abductive reasoning in the context of ontologies. We discuss several applicaton scenarios in which various forms of abduction would be useful, introduce corresponding abductive reasoning tasks, give examples, and begin to develop the formal apparatus needed to employ abductive inference in expressive description logics.', '598347d95a3ef39a3987a39b40708f79', 'a5936835f9eeab91eb09d84948306178', '15a1bdcbff44431651957f45097dc4f4', '', 'inproceedings', 'A case for abductive reasoning over ontologies', 'Corinna Elsenbroich and Oliver Kutz and Ulrike Sattler', 'Bernardo Cuenca Grau and Pascal Hitzler and Conor Shankey and Evan Wallace', '2006', NULL, 0, '2011-08-08 09:24:38', 0),
(201, NULL, '216', NULL, NULL, 'nov', NULL, 'Proceedings of the OWLED*06 Workshop on OWL: Experiences and Directions', NULL, NULL, NULL, NULL, NULL, NULL, 'CEUR-WS.org', 'elsenbroich2006abductive', -2147483648, '2011-08-08 09:24:38', 'testspammer', 'http://www.cs.man.ac.uk/~okutz/case-for-abduction.pdf', NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, '  issn = {1613-0073}', 'We argue for the usefulness of abductive reasoning in the context of ontologies. We discuss several applicaton scenarios in which various forms of abduction would be useful, introduce corresponding abductive reasoning tasks, give examples, and begin to develop the formal apparatus needed to employ abductive inference in expressive description logics.', '598347d95a3ef39a3987a39b40708f79', 'a5936835f9eeab91eb09d84948306178', '15a1bdcbff44431651957f45097dc4f4', '', 'inproceedings', 'A case for abductive reasoning over ontologies', 'Corinna Elsenbroich and Oliver Kutz and Ulrike Sattler', 'Bernardo Cuenca Grau and Pascal Hitzler and Conor Shankey and Evan Wallace', '2006', NULL, 0, '2011-08-08 09:24:38', 0),
(1073741826,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Uni Sonstewo',NULL,'muller1948anderes',0,'2015-07-06 14:19:30','testuserP',NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,'',NULL,'790d334c23e27b2e4c9f20b4bc87ab4b','3dc3dbb9d263a95a53b7134718b2b7f2','dd8930d9b1db11a87305923e069b77b6','','phdthesis','Was anderes','Müller, Heinrich Georg',NULL,'1948',NULL,0,'2015-07-06 12:19:30',0),
(1073741827,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'University of Nowhere',NULL,'muller2004wurst',0,'2015-07-06 14:15:12','testuserP',NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,NULL,'',NULL,'9132de6d174bcfd8018d0b299642f12d','0b539e248a02e3edcfe591c64346c7a0','d63038ea59383b94bb52fc4a9b76d1f5','','phdthesis','Wurst aufs Brot','Müller, Heinrich Georg',NULL,'2004',NULL,0,'2015-07-06 12:21:34',0);
-- TODO add
-- hotho diss
-- hotho paper
-- dummy master thesis by some other hotho
-- dynamically add hotho habil nad test its preference


-- 
-- Data for repository tests
--

INSERT INTO `repository` (`inter_hash`, `intra_hash`, `user_name`, `repository_name`) VALUES
('9abf98937435f05aec3d58b214a2ac58', 'b77ddd8087ad8856d77c740c8dc2864a', 'testuser1', 'test repo1'),
('9abf98937435f05aec3d58b214a2ac58', 'b77ddd8087ad8856d77c740c8dc2864a', 'testuser1', 'test repo2'),
('36a19ee7b7923b062a99a6065fe07792', '891518b4900cd1832d77a0c8ae20dd14', 'testuser3', 'test repo1'),
('36a19ee7b7923b062a99a6065fe07792', 'b71d5283dc7f4f59f306810e73e9bc9a', 'testuser2', 'test_repo1'),
('36a19ee7b7923b062a99a6065fe07792', '891518b4900cd1832d77a0c8ae20dd14', 'testuser3', 'test_repo4'),
('36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'testuser3', 'test_repo4');

--
-- Data for synchronization tests
--
-- FIXME: testuser4 missing

INSERT INTO `user` (`user_name`,`user_email`,`user_password`,`user_homepage`,`user_realname`,`spammer`,`openurl`,`reg_date`,`ip_address`,`id`,`tmp_password`,`tmp_request_date`,`tagbox_style`,`tagbox_sort`,`tagbox_minfreq`,`tagbox_max_count`,`is_max_count`,`tagbox_tooltip`,`list_itemcount`,`spammer_suggest`,`birthday`,`gender`,`profession`,`institution`, `interests`,`hobbies`,`place`,`profilegroup`,`api_key`,`updated_by`,`updated_at`,`role`,`lang`,`to_classify`,`log_level`, `simple_interface`) VALUES 
('syncServer',   'testuser1@bibsonomy.org',   'cc03e747a6afbbcbf8be7668acfebee5', 'http://www.bibsonomy.org/user/testuser1',   'Test User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1, NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1, '15cb586b630cc343cd60684807bf4785', 'wla','1815-12-10 00:00:00', 4,  'en', 0, 1, 3);

-- bibtex posts
INSERT INTO `bibtex` (`content_id`, `user_name`, `simhash1`, `simhash2`, `change_date`, `date`) VALUES
-- post 1 "no changes" created and modified before last synchronization
(101, 'Syncuser1', '69f46427bfed611701eef5aed85f3a28', '6a486c3b5cf17466f984f8090077274c', '2011-01-31 14:32:00', '2011-01-10 14:32:00'),
-- post 2 "deleted on server" is not in the server database
-- post 3 "deleted on client" created and modified before last synchronization
(103, 'Syncuser1', 'c4bcb6611057cbee895fde8474e86a92', 'b1629524db9c09f8b75af7ba83249980', '2011-01-10 14:55:00', '2011-01-10 14:33:00'),
-- post 4 "changed on server" created before, changed after the last scnchronization
(104, 'Syncuser1', '319872adc49bfeae3f799d29a18b0634', '11db3d75b9e07960658984f9b012d6d7', '2011-03-16 17:30:00', '2010-09-16 14:35:00'),
-- post 5 "changed on client" created and modified before last synchronization
(105, 'Syncuser1', '2f0fc12a47ba98a11a2746376b118e48', '133de67269c9bfa71bde2b7615f0c1b3', '2010-02-05 17:23:00', '2009-12-31 23:59:00'),
-- post 6 "created on server" created and modified after last synchronization
(106, 'Syncuser1', '564c7c31f6a34e5dc3a1b0c17fe68c13', '08cdf0d0dcce9d07fd8d41ac6267cadf', '2011-03-18 11:20:00', '2011-03-18 11:20:00');
-- post 7 "created on client" is not in the server database


INSERT INTO `sync_services` (`uri`, `service_id`, server, ssl_dn) VALUES
('http://www.bibsonomy.org/', 1, FALSE, 'bibsonomy test ssl dn'),
('http://www.test.de/', 2, TRUE, 'test.de ssl dn'),
('http://www.test.de/', 0, FALSE, 'test.de ssl dn'),
('client://android', 4, FALSE, 'test.de ssl dn'),
('http://localhost:41253/', 10, TRUE, 'localhost ssl dn');

-- synchronization data table
INSERT INTO `sync_data` (`service_id`, `user_name`, `content_type`, `last_sync_date`, `status`, `device_id`, `device_info`, `info`) VALUES 
(1, 'syncuser1', 2, '2011-02-02 23:00:00', 'done', '', NULL, ''),
(0, 'syncserver', 2, '2011-02-02 23:00:00', 'done',  '', NULL,''),
(4, 'syncuser1', 0, '2011-12-20 17:22:46', 'done', '123456789012', 'NexusOne', ''),
(4, 'syncuser1', 0, '2011-12-20 17:22:46', 'done', '123456789013', 'Nexus Galaxy', ''),
(0, 'syncserver', 1, '2011-02-02 23:10:00', 'done', '', NULL, '');

INSERT INTO `sync` (`user_name`, `service_id`, `credentials`, `content_type`, `direction`) VALUES
('syncuser1', '1', '#Tue May 10 13:27:07 CEST 2011\nuserName=syncServer\napiKey=15cb586b630cc343cd60684807bf4785', 0, 'both'),
('sync2', '10', '#Tue May 10 13:27:07 CEST 2011\nuserName=syncServer\napiKey=15cb586b630cc343cd60684807bf4785', 0, 'both')
;


-- bookmark table data
INSERT INTO `bookmark` (`content_id`, `user_name`, `book_url_hash`, `book_description`, `change_date`, `date`) VALUES
(111, 'Syncuser1', '6232752de0376fb6692917faf2e0a41e', 'no changes', '2010-12-23 17:42:00', '2010-11-01 12:55:00'),
(113, 'Syncuser1', '35b3ed178e437da1e93e2cac75333c67', 'deleted on client', '2011-01-04 13:30:00', '2011-01-01 00:01:00'),
(114, 'Syncuser1', 'bcf7feb2dd4acba08f79b31991ed51bb', 'changed on server', '2011-03-18 11:54:00', '2009-01-01 05:54:00'),
(115, 'Syncuser1', 'c4bb293ee64fecf340db99b39f401008', 'changed on client', '2010-11-01 18:44:00', '2010-01-12 15:28:00'),
(116, 'Syncuser1', 'c7c8d5f682a6f32b7b3be9f3986a1cba', 'created on server', '2011-03-18 11:55:00', '2011-03-18 11:55:00');

-- end of synchronization data

-- 
-- Data for table `bibtexurls`
-- 

INSERT INTO `bibtexurls` (`content_id`, `url`, `text`, `group`, `date`) VALUES
(10, 'http://www.testurl.org', 'test text', 0, '2008-07-09 11:03:24');


-- 
-- Data for table `gold_standard`
-- 
-- publications
INSERT INTO `gold_standard` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `booktitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `group`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `entrytype`, `title`, `author`, `editor`, `year`, `privnote`, `scraperid`, `change_date`, `rating`, `content_type`) VALUES
(1005, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'bibtexKey1', 0, '1815-12-10 00:00:00', 'testuser1',   'http://www.testurl.org',        '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'misc={test}', 'test bibtexAbstract', '9abf98937435f05aec3d58b214a2ac58', '097248439469d8f5a1e7fad6b02cbfcd', 'b77ddd8087ad8856d77c740c8dc2864a', '', 'test entrytype', 'test title',         'test author',  'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:24:55', 0, 2),
(1015, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'bibtexKey2', 0, '1815-12-10 00:00:00', 'testuser2',   'http://private.bibtex.url.com', '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'misc={test}', 'test bibtexAbstract', '8711751127efb070ee910a5d145a168b', 'ac6aa3ccb181e61801cefbc1401d409a', '1b298f199d487bc527a62326573892b8', '', 'test entrytype', 'test private title', 'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0, 2);
-- bookmarks
INSERT INTO `gold_standard` (`content_id`, `title`, `url`, `description`, `date`, `user_name`, `simhash1`, `content_type`) VALUES
(1025, 'Universität Kassel', 'http://www.uni-kassel.de', 'Nordhessen', '2011-01-01 00:00:00', 'testuser1', '20592a292e53843965c1bb42bfd51876', 1);

-- 
-- Data for insert into similar_persons (match_id, person1_id, person2_id, mode, item1_id, item2_id) values (0, "johirth.0", "johirth.1", "test", "01234567891011121314151617181920", "01234567891011121314151617181921");table `gold_standard_relations`
-- 
INSERT INTO `gold_standard_relations` (`publication`, `reference`, `user_name`, `date`, `relation_kind`) VALUES
('097248439469d8f5a1e7fad6b02cbfcd', 'ac6aa3ccb181e61801cefbc1401d409a', 'testuser1', '2008-03-20 16:24:55', '0'),
('ac6aa3ccb181e61801cefbc1401d409a', '097248439469d8f5a1e7fad6b02cbfcd', 'testuser1', '2008-03-20 16:24:55','0');


-- 
-- Data for table `bookmark`
-- 

INSERT INTO `bookmark` (`content_id`, `book_url_hash`, `book_description`, `book_extended`, `group`, `date`, `user_name`, `to_bib`, `change_date`, `rating`) VALUES
(1, '6f372faea7ff92eedf52f597090a6291', 'test bookmark descripton', 'test bookmark extended',    3, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:17:10', 0),
(2, '108eca7b644e2c5e09853619bc416ed0', 'Google',                   'bekannteste Suchmaschine',  0, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:34:17', 0),
(3, '7eda282d1d604c702597600a06f8a6b0', 'Yahoo',                    'Yahoo Suchmaschine',        0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 10:16:55', 0),
(4, 'b7aa3a91885e432c6c95bec0145c3968', 'FriendScout24',            'Seite f√ºr einen "friend"', 2, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:16:46', 0),
(5, '965a65fdc161e354f3828050390e2b06', 'web.de',                   'WEB.de Freemail',           0, '1815-12-10 00:00:00', 'testuser3', 0, '2008-01-18 10:16:39', 0),
(6, '20592a292e53843965c1bb42bfd51876', 'uni-kassel',               'UniK',                      0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 11:29:03', 0),
(7, '16dfed76f9d846056a6a3c0d022c3493', 'finetune',                 'finetune.com',              4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:14:33', 0),
(8, 'e9ea2574c49c3778f166e8b4b6ed63dd', 'apple',                    'apple.com',                 4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:20:57', 0),
(9, '294a9e1d594297e7bb9da9e11229c5d7', 'fireball.com',             'fireball',                  1, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-29 10:36:06', 0),
(10, '85ab919107e4cc79b345e996b3c0b097', 'kde', 					'KDE Page', 				 0, '1815-12-10 00:00:00', 'testuser4', 0, '2008-01-29 12:36:06', 0),
(11, '85ab919107e4cc79b345e996b3c0b097', 'kde', 					'KDE Page', 				 2, '1815-12-10 00:00:00', 'testuser4', 0, '2008-01-29 15:36:06', 0),
(15, '10ab297107e4bb79b345e406b3c2a087', 'kde.uni-kassel.de',		'KDE Uni Kassel Page',		 3, '1815-12-10 00:00:00', 'testuser1', 0, '2010-03-22 16:00:00', 0),
(16, '2574200000e4bb79b100e406b777a044', 'wm2010.com',				'WM2010',					 3, '1815-12-10 00:00:00', 'testuser1', 0, '2010-03-22 16:00:00', 0);


-- 
-- Data for table `collector`
-- 
INSERT INTO `collector` (`user_name`, `content_id`, `date`) VALUES
('testuser1', 10, '2008-06-18 14:27:35'),
('testuser1', 12, '2008-06-18 14:27:35'),
('testuser2', 13, '2008-06-18 14:33:01'),
('testuser2', 14, '2008-06-18 14:33:22');


-- insert into similar_persons (match_id, person1_id, person2_id, mode, item1_id, item2_id) values (0, "johirth.0", "johirth.1", "test", "01234567891011121314151617181920", "01234567891011121314151617181921");
-- Data for table `document`
-- 
INSERT INTO `document` VALUES 
('00000000000000000000000000000000', 10, 'testdocument_1.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000000'),
('00000000000000000000000000000001', 10, 'testdocument_2.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000001');


-- 
-- Data for table `extended_fields_data`
-- 
INSERT INTO `extended_fields_data` VALUES 
('JEL', 'P22', 10, '2007-10-01 01:01:01', '2007-10-01 01:01:01'),
('JEL', 'P26', 10, '2007-10-01 01:01:01', '2007-10-01 01:01:01'),
('JEL', 'M37', 10, '2007-10-01 01:01:01', '2007-10-01 01:01:01'),
('JEL', 'I21', 13, '2007-10-01 01:01:01', '2007-10-01 01:01:01'),
('JEL', 'A12', 13, '2007-10-01 01:01:01', '2007-10-01 01:01:01'),
('JEL', 'A33', 13, '2007-10-01 01:01:01', '2007-10-01 01:01:01');


-- 
-- Data for table `friends`
-- 

INSERT INTO `friends` VALUES 
(1, 'testuser1', 'testuser2', 'sys:network:bibsonomy-friend', NULL, '1815-12-10 00:00:00'),
(3, 'testuser1', 'testuser3', 'sys:network:bibsonomy-friend', NULL, '1815-12-10 00:00:00'),
(2, 'testuser2', 'testuser1', 'sys:network:bibsonomy-friend', NULL, '1815-12-10 00:00:00'),
(4, 'testuser1', 'testuser2', 'sys:network:bibsonomy-follower', NULL, '1815-12-10 00:00:00'),
(5, 'testuser1', 'testuser3', 'sys:network:bibsonomy-follower', NULL, '1815-12-10 00:00:00'),
(6, 'testuser2', 'testuser1', 'sys:network:bibsonomy-follower', NULL, '1815-12-10 00:00:00');


-- 
-- Data for table `groupids`
-- 
INSERT INTO `groupids` (`group_name`, `group`, `privlevel`, `sharedDocuments`) VALUES 
('public',     -2147483648, 1, 0),
('private',    -2147483647, 1, 0),
('friends',    -2147483646, 1, 0),
('public',     0,           1, 0),
('private',    1,           1, 0),
('friends',    2,           1, 0),
('testgroup1', 3,           0, 1),
('testgroup2', 4,           1, 0),
('testgroup3', 5,           2, 0),
('testgroup4', 6,           2, 1);

-- 
-- Data for table `pending_groupids`
-- 
INSERT INTO `pending_groupids` (`group_name`, `request_user_name`, `request_reason`, `group`, `privlevel`, `sharedDocuments`) VALUES 
('testpendinggroup1', 'testrequestuser1', 'my new reason1', 7,           0, 1),
('testpendinggroup2', 'testrequestuser2', 'my new reason2', 8,           1, 0);
 
-- 
-- Data for table `group_memberships`
-- 

INSERT INTO `group_memberships` VALUES 
('testuser1', 3, 3, '2007-01-01 01:01:01', 2, 1),
('testuser2', 3, 3, '2007-01-01 01:01:01', 2, 0),
('testuser1', 4, 3, '2007-01-01 01:01:01', 2, 1),
('testuser1', 5, 3, '2007-01-01 01:01:01', 2, 0),
('testuser1', 6, 3, '2007-01-01 01:01:01', 2, 0),
('testuser2', 6, 3, '2007-01-01 01:01:01', 2, 0),
('testgroup1', 3, 3, '2007-01-01 01:01:01', 3, 0),
('testgroup2', 4, 4, '2007-01-01 01:01:01', 3, 0),
('testgroup3', 5, 5, '2007-01-01 01:01:01', 3, 0),
('testgroup3', 6, 6, '2007-01-01 01:01:01', 3, 0);




-- 
-- Data for table `ids`
-- 

INSERT INTO `ids` VALUES 
(0,  1073741827, 'content_id'),
(1,  1073741830, 'tas id'),
(2,  21,  'relation id'),
(3,  0,  'question id'),
(4,  1,  'cycle id'),
(5,  0,  'extended_fields_id'),
(7,  0,  'scraper_metadata_id'),
(12, 28, 'grouptas id'),
(14, 3,  'message_id'),
(15, 4, 'comment_id'),
(16, 12, 'sync_service_id'),
(17, 7, 'person_change_id'),
(18, 3, 'project_id');


--
-- Data for table `inboxMail`
--

INSERT INTO `inboxMail` VALUES
(1, 1, '6f372faea7ff92eedf52f597090a6291', 'testuser1', 'testuser2', '2009-10-08 14:23:00', 1),
(2, 5, '965a65fdc161e354f3828050390e2b06', 'testuser3', 'testuser2', '2009-10-08 14:23:32', 1),
(3, 10, '85ab919107e4cc79b345e996b3c0b097', 'testuser3', 'testuser2', '2009-10-08 14:23:32', 1);

--
-- Data for table `inboxMail`
--

INSERT INTO `inbox_tas` VALUES
(1, 'tag11'),
(1, 'tag12'),
(2,	'tag21'),
(3, 'tag31');

-- 
-- Data for table `inetAddressStates`
-- 

INSERT INTO `inetAddressStates`(`address`, `status`) VALUES 
('192.168.0.1', 0);


-- 
-- Data for table `log_bibtex`
-- 
INSERT INTO log_bibtex (`content_id`, `new_content_id`, `current_content_id`, `log_date`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `booktitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `group`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `entrytype`, `title`, `author`, `editor`, `year`, `privnote`, `scraperid`, `change_date`, `rating`) VALUES
(17, 18, 20, '2009-10-08 14:13:01', 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '2008-05-19 14:34:29', 'testuser3',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0),
(18, 19, 20, '2009-10-08 14:24:01', 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '2009-10-08 14:13:00', 'testuser3',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'e2fb0763068b21639c3e36101f64aefe', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2009-10-08 14:13:00', 0),
(19, 20, 20, '2009-10-08 14:35:01', 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '2009-10-08 14:24:00', 'testuser3',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2009-10-08 14:24:00', 0),
(1312, 0, 0, '2009-10-08 14:35:03', 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '2009-10-08 14:24:00', 'testuser3',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2009-10-08 14:29:00', 0);

-- 
-- Data for table `log_bookmark`
-- 

INSERT INTO `log_bookmark` (`content_id`, `book_url_hash`, `book_description`, `book_extended`, `group`, `date`, `user_name`, `new_content_id`, `change_date`, `rating`, `log_date`, `current_content_id`) VALUES
(1073742052, 'bbf9b0339a070080a3668c9cb6158ecf', 'IT-News, ct, iX, Technology Review, Telepolis | heise online', 'News und Foren zu Computer, IT, Wissenschaft, Medien und Politik. Preisvergleich von Hardware und Software sowie Downloads beim Heise Zeitschriften Verlag.', 0, '2013-10-23 17:29:05', 'testuser1', 0, '2015-03-08 00:43:45', 0, '2015-04-15 00:15:57', 0);


-- 
-- Data for table `log_collector`
-- 


-- 
-- Data for table `log_friends`
-- 




-- 
-- Data for table `log_groups`
-- 




-- 
-- Data for table `log_tagtagrelations`
-- 




-- 
-- Data for table `log_tas`
-- 

INSERT INTO `log_tas` VALUES
(1073741825,'phd',1073741825,2,'2015-07-06 14:15:12','2015-07-06 14:15:12','2015-07-06 12:21:34'),
(1073741826,'test',1073741825,2,'2015-07-06 14:15:12','2015-07-06 14:15:12','2015-07-06 12:21:34'),
(1073742431, 'lesezeichen', 1073742052, 1, '2013-10-23 17:29:05', '2015-03-08 00:43:45', '2015-04-15 00:15:57'),
(1073742432, 'news', 1073742052, 1, '2013-10-23 17:29:05', '2015-03-08 00:43:45', '2015-04-15 00:15:57'),
(1073742433, 'it', 1073742052, 1, '2013-10-23 17:29:05', '2015-03-08 00:43:45', '2015-04-15 00:15:57');



-- 
-- Data for table `log_user`
-- 



-- 
-- Data for table `log_prediction`
-- 

INSERT INTO `log_prediction` VALUES 
(1, 'testspammer', 1, UNIX_TIMESTAMP(NOW()),'2008-06-18 14:27:35', 'testlogging', 0, 0.2);


--
-- Data for table `pendingUser`
--

INSERT INTO `pendingUser` (`user_name`,`user_email`,`user_password`,`user_homepage`,`user_realname`,`spammer`,`openurl`,`reg_date`,`ip_address`,`id`,`tmp_password`,`tmp_request_date`,`tagbox_style`,`tagbox_sort`,`tagbox_minfreq`,`tagbox_max_count`,`is_max_count`,`tagbox_tooltip`,`list_itemcount`,`spammer_suggest`,`birthday`,`gender`,`profession`,`institution`,`interests`,`hobbies`,`place`,`profilegroup`,`api_key`,`updated_by`,`updated_at`,`role`,`lang`,`to_classify`,`log_level`,`activation_code`) VALUES 
-- user_name     user_email                   user_password   user_homepage                           user_realname   spammer   openurl                       reg_date               ip_address id    tmp_password tmp_request_date tagbox_style tagbox_sort tagbox_minfreq tagbox_tooltip list_itemcount  spammer_suggest birthday gender profession institution interests hobbies place                               profilegroup api_key                             updated_by updated_at             role lang to_classify log_level activation_code
('activationtestuser1',   'testuser1@bibsonomy.org',   'cc03e747a6afbbcbf8be7668acfebee5', 'http://www.bibsonomy.org/user/testuser1',   'Test Activation User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:23:55', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 0,  'en', 0, 1, '6dfab2a50e9629f780306ff34ff3d856'),
('activationtestuser2',   'testuser2@bibsonomy.org',   'cc03e747a6afbbcbf8be7668acfebee5', 'http://www.bibsonomy.org/user/testuser2',   'Test Activation User 2',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-07-08 01:23:55', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 0,  'en', 0, 1, 'ac47d3f92b90c89e46170f7049beda37'),
('testpendinggroup1',   'testpendinggroup1@bibsonomy.org',   'cc03e747a6afbbcbf8be7668acfebee5', 'http://www.bibsonomy.org/user/testuser1',   'Test Pending Group User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:23:55', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 6,  'en', 0, 1, '6d1232a50e9629f780306ff34ff3d856'),
('testpendinggroup2',   'testpendinggroup1@bibsonomy.org',   'cc03e747a6afbbcbf8be7668acfebee5', 'http://www.bibsonomy.org/user/testuser1',   'Test Pending Group User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:23:55', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 6,  'en', 0, 1, 'ac47d3f9fdljc89e46170f7049beda37');


-- 
-- Data for table `picked_concepts`
-- 



--
-- Data for table `prediction`
-- 
-- (user_name, prediction, timestamp, updated_at, algorithm, mode, evaluator, confidence)
INSERT INTO `prediction` VALUES
('testspammer2', 1, UNIX_TIMESTAMP(NOW()), '2008-06-18 14:27:35', 'testlogging', 'D', 0, 0.42);



-- 
-- Data for table `rankings`
-- 




-- 
-- Data for table `scraperMetaData`
-- 


-- 
-- Data for table `spammer_tags`
-- 


-- 
-- Data for table `tags`
-- 

INSERT INTO `tags` (`tag_id`, `tag_name`, `tag_stem`, `tag_ctr`, `tag_ctr_public`, `waiting_content_sim`) VALUES
(1, 'testtag',        '', 2, 2, 0),
(2, 'suchmaschine',   '', 4, 4, 0),
(3, 'google',         '', 1, 1, 0),
(4, 'yahoo',          '', 1, 1, 0),
(5, 'friends',        '', 1, 1, 0),
(6, 'friendscout',    '', 1, 1, 0),
(7, 'web',            '', 1, 1, 0),
(8, 'freemail',       '', 1, 1, 0),
(9, 'uni',            '', 1, 1, 0),
(10, 'kassel',        '', 1, 1, 0),
(11, 'finetune',      '', 1, 1, 0),
(12, 'radio',         '', 1, 1, 0),
(13, 'apple',         '', 1, 1, 0),
(14, 'fireball',      '', 1, 1, 0),
(15, 'testbibtex',    '', 2, 2, 0),
(16, 'spam',          '', 1, 0, 0),
(17, 'bibtexgroup',   '', 1, 1, 0),
(18, 'privatebibtex', '', 1, 1, 0),
(19, 'friendbibtex',  '', 1, 1, 0),
(20, 'amazon',  	  '', 1, 1, 0),
(21, 'tomcat',        '', 1, 1, 0),
(22, 'weltmeisterschaft', '', 1, 1, 0),
(21052613,'phd','',1,0,0),
(21052614,'test','',1,0,0),
(21052615,'andere','',1,0,0),
(21052616,'person','',1,0,0);




-- 
-- Data for table `tagtag`
-- 

-- 
-- Data for table `tagtag_batch`
-- 




-- 
-- Data for table `tagtag_temp`
-- 




-- 
-- Data for table `tagtagrelations`
-- 
INSERT INTO `tagtagrelations` (`relationID`, `date_of_create`, `date_of_last_mod`, `user_name`, `lower`, `upper`, `picked`, `lower_lcase`, `upper_lcase`) VALUES
(4,  '1815-12-10 00:00:00', '2007-12-12 00:00:00', 'testuser1',   'google',   'suchmaschine', 1, 'google',   'suchmaschine'),
(5,  '1815-12-10 00:00:00', '2007-12-12 00:00:05', 'testuser2',   'yahoo',    'suchmaschine', 1, 'yahoo',    'suchmaschine'),
(6,  '1815-12-10 00:00:00', '2008-01-29 10:39:17', 'testuser1',   'fireball', 'suchmaschine', 1, 'fireball', 'suchmaschine'),
(7,  '1815-12-10 00:00:00', '2008-12-12 00:00:00', 'testuser1',   'debian',   'linux',        1, 'debian',   'linux'),
(8,  '1815-12-10 00:00:00', '2008-12-12 00:00:00', 'testuser1',   'ubuntu',   'linux',        1, 'ubuntu',   'linux'),
(9,  '1815-12-10 00:00:00', '2008-12-12 00:00:00', 'testuser3',   'Java',     'Programming',  1, 'java',     'programming'),
(10, '1815-12-10 00:00:00', '2008-12-12 00:00:00', 'testuser3',   'C++',      'programming',  1, 'c++',      'programming'),
(11, '1815-12-10 00:00:00', '2009-02-12 00:12:23', 'testuser3',   'Google',   'Suchmaschine', 1, 'google',   'suchmaschine'),
(12, '1815-12-10 00:00:00', '2009-02-12 00:12:25', 'testuser1',   'java',     'programming',  1, 'java',     'programming'),
(13, '1815-12-10 00:00:00', '2009-02-12 00:12:26', 'testuser1',   'c',        'programming',  1, 'c',        'programming'),
(14, '1815-12-10 00:00:00', '2009-02-12 00:12:28', 'testuser3',   'UBUNTU',   'LINUX',        0, 'ubuntu',   'linux'),
(15, '1815-12-10 00:00:00', '2009-02-12 00:12:31', 'testuser1',   'openSUSE', 'Linux',        1, 'opensuse', 'linux'),
(16, '1815-12-10 00:00:00', '2009-02-12 00:12:38', 'testuser2',   'C',        'programming',  1, 'c',        'programming'),
(17, '1815-12-10 00:00:00', '2009-10-12 20:00:05', 'testuser1',   '.net',     'programming',  1, '.net',     'programming'),
(18, '1815-12-10 00:00:00', '2009-11-22 07:10:59', 'testuser2',   'java',     'programming',  1, 'java',     'programming'),
(19, '1815-12-11 00:10:00', '2009-12-22 07:10:59', 'testuser3',   'java',     'programming',  1, 'java',     'programming'),
(20, '2009-12-12 20:00:05', '2009-12-12 20:00:05', 'testspammer', 'bla',      'blubb',        1, 'bla',      'blubb'),
(21, '2009-12-12 20:00:15', '2009-12-12 20:00:15', 'testspammer', 'c',        'programming',  1, 'c',        'programming'),
(22, '2009-12-12 20:00:18', '2009-12-12 20:00:18', 'testspammer2', 'perl',    'programming',  1, 'perl',     'programming');





-- 
-- Data for table `tas`
-- 

INSERT INTO `tas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(1, 'testtag',        'testtag',        1, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(2, 'google',         'google',         2, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(3, 'suchmaschine',   'suchmaschine',   2, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:19:51'),
(25, 'search',        'search',         2, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(4, 'yahoo',          'yahoo',          3, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 10:21:12'),
(5, 'suchmaschine',   'suchmaschine',   3, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 10:21:47'),
(6, 'friends',        'friends',        4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:31'),
(7, 'friendscout',    'friendscout',    4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:44'),
(8, 'web',            'web',            5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(9, 'freemail',       'freemail',       5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(10, 'suchmaschine',  'suchmaschine',   5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(11, 'uni',           'uni',            6, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 11:30:05'),
(12, 'kassel',        'kassel',         6, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 11:30:05'),
(13, 'finetune',      'finetune',       7, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:22:09'),
(14, 'radio',         'radio',          7, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:22:20'),
(15, 'apple',         'apple',          8, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:20:37'),
(16, 'suchmaschine',  'suchmaschine',   9, 1, 'testuser1',   '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(17, 'fireball',      'fireball',       9, 1, 'testuser1',   '1815-12-10 00:00:00', 1, '2008-01-29 10:39:17'),
(18, 'testbibtex',    'testbibtex',    10, 2, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-03-19 11:21:44'),
(19, 'testtag',       'testtag',       10, 2, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-03-19 11:27:34'),
(20, 'spam',          'spam',          11, 2, 'testspammer', '1815-12-10 00:00:00', 0, '2008-03-20 16:35:21'),
(21, 'bibtexgroup',   'bibtexgroup',   12, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(22, 'privatebibtex', 'privatebibtex', 13, 2, 'testuser2',   '1815-12-10 00:00:00', 1, '2008-03-20 20:35:21'),
(23, 'friendbibtex',  'friendbibtex',  14, 2, 'testuser2',   '1815-12-10 00:00:00', 2, '2008-03-20 20:35:21'),
(24, 'testbibtex',    'testbibtex',    12, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(29, 'tomcat',         'tomcat',        15, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(30, 'amazon',         'amazon',        15, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(31, 'weltmeisterschaft', 'weltmeisterschaft', 16, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:17'),
(32, 'synchronization', 'synchronization', 101, 2, 'syncuser1', '2011-01-10 14:32:00', 0, '2011-01-31 14:32:00'),
(33, 'synchronization', 'synchronization', 103, 2, 'syncuser1', '2011-01-10 14:33:00', 0, '2011-01-10 14:55:00'),
(34, 'synchronization', 'synchronization', 104, 2, 'syncuser1', '2010-09-16 14:35:00', 0, '2011-03-16 17:30:00'),
(35, 'synchronization', 'synchronization', 105, 2, 'syncuser1', '2009-12-31 23:59:00', 0, '2010-02-05 17:23:00'),
(36, 'synchronization', 'synchronization', 106, 2, 'syncuser1', '2011-03-18 11:20:00', 0, '2011-03-18 11:20:00'),
(37, 'synchronization', 'synchronization', 111, 1, 'syncuser1', '2010-11-01 12:55:00', 0, '2010-12-23 17:42:00' ),
(38, 'synchronization', 'synchronization', 113, 1, 'syncuser1', '2011-01-01 00:01:00', 0, '2011-01-04 13:30:00' ),
(39, 'synchronization', 'synchronization', 114, 1, 'syncuser1', '2009-01-01 05:54:00', 0, '2011-03-18 11:54:00' ),
(40, 'synchronization', 'synchronization', 115, 1, 'syncuser1', '2010-01-12 15:28:00', 0, '2010-11-01 18:44:00' ),
(41, 'synchronization', 'synchronization', 116, 1, 'syncuser1', '2011-03-18 11:55:00', 0, '2011-03-18 11:55:00' ),
(42, 'tag', 'tag', 100, 2, 'jaeschke', '2011-08-08 09:24:38', 0, '2011-08-08 09:24:38' ),
(43, 'spam', 'spam', 201, 2, 'testspammer', '2011-08-08 09:24:38', -2147483648, '2011-08-08 09:24:38' ),
(1073741827,'andere','andere',1073741826,2,'testuserP','2015-07-06 14:19:30',0,'2015-07-06 12:19:30'),
(1073741828,'person','person',1073741826,2,'testuserP','2015-07-06 14:19:30',0,'2015-07-06 12:19:30'),
(1073741829,'phd','phd',1073741827,2,'testuserP','2015-07-06 14:15:12',0,'2015-07-06 12:21:34'),
(1073741830,'test','test',1073741827,2,'testuserP','2015-07-06 14:15:12',0,'2015-07-06 12:21:34');


-- 
-- Data for table `temp_bibtex`
-- 

INSERT INTO `temp_bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `bookTitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `entrytype`, `title`, `author`, `editor`, `year`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `ctr`, `rank`, `rating`, `popular_days`) VALUES
(10, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '9abf98937435f05aec3d58b214a2ac58', '097248439469d8f5a1e7fad6b02cbfcd', 'b77ddd8087ad8856d77c740c8dc2864a', '', 1, 1, 0, 1),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '92e8d9c7588eced69419b911b31580ee', '097248439469d8f5a1e7fad6b02cbfcd', '522833042311cc30b8775772335424a7', '', 1, 2, 0, 2);



-- 
-- Data for table `temp_bookmark`
-- 

INSERT INTO `temp_bookmark` (`content_id`, `book_description`, `book_extended`, `book_url_hash`, `date`, `user_name`, `book_url_ctr`, `rank`, `rating`, `popular_days`) VALUES
(1, 'test bookmark descripton	', 'test bookmark extended	', '6f372faea7ff92eedf52f597090a6291', '1815-12-10 00:00:00', 'testuser1', 1, 1, 0, 1);



-- 
-- Data for table `urls`
-- 

INSERT INTO `urls` (`book_url_hash`, `book_url`, `book_url_ctr`) VALUES
('108eca7b644e2c5e09853619bc416ed0', 'http://www.google.de', 1),
('16dfed76f9d846056a6a3c0d022c3493', 'http://www.finetune.com', 1),
('20592a292e53843965c1bb42bfd51876', 'http://www.uni-kassel.de', 1),
('294a9e1d594297e7bb9da9e11229c5d7', 'http://www.fireball.com\r\n', 1),
('6f372faea7ff92eedf52f597090a6291', 'http://www.testurl.org', 1),
('7eda282d1d604c702597600a06f8a6b0', 'http://www.yahoo.de', 1),
('965a65fdc161e354f3828050390e2b06', 'http://www.web.de', 1),
('b7aa3a91885e432c6c95bec0145c3968', 'http://www.friendscout24.de', 1),
('85ab919107e4cc79b345e996b3c0b097', 'http://www.ard.de', 2),
('e9ea2574c49c3778f166e8b4b6ed63dd', 'http://www.apple.com\r\n', 1),
('10ab297107e4bb79b345e406b3c2a087', 'http://www.cs.uni-kassel.de', 1),
('bbf9b0339a070080a3668c9cb6158ecf', 'http://www.heise.de', 0),
('2574200000e4bb79b100e406b777a044', 'http://www.wm2010.com', 1);



-- 
-- Data for table `user` 
--
INSERT INTO `user` (`user_name`,`user_email`,`user_password`,`user_password_salt`,`user_homepage`,`user_realname`,`spammer`,`openurl`,`reg_date`,`ip_address`,`id`,`tmp_password`,`tmp_request_date`,`tagbox_style`,`tagbox_sort`,`tagbox_minfreq`,`tagbox_max_count`,`is_max_count`,`tagbox_tooltip`,`list_itemcount`,`spammer_suggest`,`birthday`,`gender`,`profession`,`institution`, `interests`,`hobbies`,`place`,`profilegroup`,`api_key`,`updated_by`,`updated_at`,`role`,`lang`,`to_classify`,`log_level`, `simple_interface`) VALUES 
-- user_name     user_email                   user_password                      salt user_homepage                                user_realname   spammer   openurl                       reg_date               ip_address id    tmp_password tmp_request_date tagbox_style tagbox_sort tagbox_minfreq tagbox_max_count is_max_count tagbox_tooltip list_itemcount  spammer_suggest birthday gender profession institution interests hobbies place               profilegroup api_key                             updated_by updated_at             role lang to_classify log_level
('testgroup1',  'testgroup1@bibsonomy.org',  'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/group/testgroup1', 'Test Group 1', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testgroup2',  'testgroup2@bibsonomy.org',  'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/group/testgroup2', 'Test Group 2', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testgroup3',  'testgroup3@bibsonomy.org',  'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/group/testgroup3', 'Test Group 3', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testgroup4',  'testgroup4@bibsonomy.org',  'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/group/testgroup4', 'Test Group 4', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testspammer', 'testspammer@bibsonomy.org', 'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/',                 'Test Spammer', 1, 'http://sfxserv.rug.ac.be:8888/rug', '2007-02-02 02:02:02', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testspammer2', 'testspammer@bibsonomy.org', 'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/',                 'Test Spammer', 1, 'http://sfxserv.rug.ac.be:8888/rug', '2007-02-02 02:02:02', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00', 0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testuser1',   'testuser1@bibsonomy.org',   'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testuser1',   'Test User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 0,  'en', 0, 1, 3),
('testuser2',   'testuser2@bibsonomy.org',   'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testuser2',   'Test User 2',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '22222222222222222222222222222222', 'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1, 3),
('testuser3',   'testuser3@bibsonomy.org',   'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testuser3',   'Test User 3',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333333333', 'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 0, 3),
('testuser4',   'testuser4@bibsonomy.org',   'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testuser4',   'Test User 4',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333334333', 'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 0, 3),
('testlimited', 'testlimited@bibsonomy.org', 'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testlimited', 'Limited Test User',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2013-02-18 12:00:00', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                  1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333334333', 'jil',     '1815-12-10 00:00:00', 5,  'en', 0, 0, 3),
('testrequestuser1', 'testrequestuser1@bibsonomy.org', 'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testrequestuser1', 'Request Test User',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2013-02-18 12:00:00', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                   1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333344333', 'jil',     '1815-12-10 00:00:00', 6,  'en', 0, 0, 3),
('testrequestuser2', 'testrequestuser2@bibsonomy.org', 'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testrequestuser2', 'Request Test User',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2013-02-18 12:00:00', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                   1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333444333', 'jil',     '1815-12-10 00:00:00', 6,  'en', 0, 0, 3),
('testuserP',   'testuserP@bibsonomy.org',   'e08a7c49d96c2b475656cc8fe18cee8e', '', 'http://www.bibsonomy.org/user/testuserP',   'Test User P',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 0, 1, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-institution', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'jil',     '1815-12-10 00:00:00', 0,  'en', 0, 1, 3);

--
-- Data for table `user_wiki`
-- 
INSERT INTO `user_wiki` (`user_name`,`user_wiki`) VALUES 
-- user_name       user_wiki
('testgroup1', ""),
('testgroup2', ""),
('testgroup3', ""),
('testspammer', ""),
('testspammer2', ""),
('testuser1', "==TEST1==\nteste mich \n==TEST2==\n bla test"),
('testuser2', ""),
('testuser3', "");
--
-- Data for table `useruser_similarity`
-- 
INSERT INTO `useruser_similarity` (`u1`, `u2`, `sim`, `measure_id`) VALUES
('testuser1', 'testuser2', 0.5, 0),
('testuser1', 'testuser3', 0.2, 0),
('testuser2', 'testuser1', 0.5, 0),
('testuser3', 'testuser1', 1, 0);

--
-- Data for table `openIDUser`
-- 


--
-- Data for table `ldapUser`
-- 


--
-- Data for table `remoteUser`
--
INSERT INTO `samlUser` (`user_name`, `samlUserId`, `identity_provider`, `lastAccess`) VALUES
('testuser1', 'samlUserId1', 'saml', '2012-11-11 11:11:11');

-- 
-- Data for table `weights`
--


--
-- Data for table `discussion`
-- 
INSERT INTO `discussion` (`discussion_id`, `type`, `interHash`,`text`,`user_name`,`date`,`rating`,`anonymous`,`group`) VALUES
(1, 1, '097248439469d8f5a1e7fad6b02cbfcd', 'crap!', 'testuser1', '2011-04-08 14:34:36', 4.0, 1, 0),
(2, 2, '097248439469d8f5a1e7fad6b02cbfcd', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(3, 2, '097248439469d8f5a1e7fad6b02cbfcd', 'SPAM', 'testuser1', '2011-04-18 14:34:38', NULL, 0, 2),
(4, 2, '097248439469d8f5a1e7fad6b02cbfcd', 'This is a multiple group comment', 'testuser1', '2011-04-20 14:34:39', NULL, 0, 3),
(4, 2, '097248439469d8f5a1e7fad6b02cbfcd', 'This is a multiple group comment', 'testuser1', '2011-04-20 14:34:39', NULL, 0, 4),
(4, 2, '097248439469d8f5a1e7fad6b02cbfcd', 'This is a multiple group comment', 'testuser1', '2011-04-20 14:34:39', NULL, 0, 5),
(2, 2, '0c000000d00000f00cef0c00f000e00a', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'0c0000cdc00000b000cbe0fe0ab0acd0','This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'0a00d00000fc00000a0000a0000f0ad0', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'0d0b00c0000a000f00a00ad00ff612fc', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'0ffa0a0ad000a00cbccf000adb0fdde0', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'0e0a00c000000f00d0d000b00eefe00b', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1),
(2, 2,'00dc000febca00a0f0f00ce0de000000', 'This is a test comment!', 'testuser1', '2011-04-08 14:34:37', NULL, 0, 1);
--
-- Data for table `review_ratings_cache`
-- 
INSERT INTO `review_ratings_cache` (`interHash`,`number_of_ratings`,`rating_arithmetic_mean`) VALUES
('097248439469d8f5a1e7fad6b02cbfcd', 1, 4);

-- 
-- Data for table `grouptas`
-- FIXME: this data is broken! it contains posts which are in the tas table but there have 
-- a public or private group ..
-- 

INSERT INTO `grouptas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(1, 'testtag',        'testtag',        1, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(25,'testtag',        'testtag',        1, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-18 10:20:07'),
(26,'testtag',        'testtag',        1, 1, 'testuser1',   '1815-12-10 00:00:00', 5, '2008-01-18 10:20:07'),
(2, 'google',         'google',         2, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:17'),
(3, 'suchmaschine',   'suchmaschine',   2, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:19:51'),
(27,'google',         'google',         2, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-18 10:20:17'),
(28,'suchmaschine',   'suchmaschine',   2, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-18 10:19:51'),
(4, 'yahoo',          'yahoo',          3, 1, 'testuser2',   '1815-12-10 00:00:00', 3, '2008-01-18 10:21:12'),
(5, 'suchmaschine',   'suchmaschine',   3, 1, 'testuser2',   '1815-12-10 00:00:00', 3, '2008-01-18 10:21:47'),
(6, 'friends',        'friends',        4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:31'),
(7, 'friendscout',    'friendscout',    4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:44'),
(8, 'web',            'web',            5, 1, 'testuser3',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:14'),
(9, 'freemail',       'freemail',       5, 1, 'testuser3',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:14'),
(10, 'suchmaschine',  'suchmaschine',   5, 1, 'testuser3',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:14'),
(11, 'uni',           'uni',            6, 1, 'testuser2',   '1815-12-10 00:00:00', 3, '2008-01-18 11:30:05'),
(12, 'kassel',        'kassel',         6, 1, 'testuser2',   '1815-12-10 00:00:00', 3, '2008-01-18 11:30:05'),
(13, 'finetune',      'finetune',       7, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:22:09'),
(14, 'radio',         'radio',          7, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:22:20'),
(15, 'apple',         'apple',          8, 1, 'testuser1',   '1815-12-10 00:00:00', 4, '2008-01-21 13:20:37'),
(16, 'suchmaschine',  'suchmaschine',   9, 1, 'testuser1',   '1815-12-10 00:00:00', 5, '2008-01-29 10:39:17'),
(17, 'fireball',      'fireball',       9, 1, 'testuser1',   '1815-12-10 00:00:00', 5, '2008-01-29 10:39:17'),
(18, 'testbibtex',    'testbibtex',    10, 2, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-03-19 11:21:44'),
(19, 'testtag',       'testtag',       10, 2, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-03-19 11:27:34'),
(20, 'spam',          'spam',          11, 2, 'testspammer', '1815-12-10 00:00:00', 3, '2008-03-20 16:35:21'),
(21, 'bibtexgroup',   'bibtexgroup',   12, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(22, 'privatebibtex', 'privatebibtex', 13, 2, 'testuser2',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21'),
(23, 'friendbibtex',  'friendbibtex',  14, 2, 'testuser2',   '1815-12-10 00:00:00', 2, '2008-03-20 20:35:21'),
(24, 'testbibtex',    'testbibtex',    12, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21');


INSERT INTO `person` (`person_change_id`, `person_id`, `academic_degree`, `user_name`, `post_ctr`, `orcid`, `dnb_person_id`, `gender`, `log_changed_at`, `log_changed_by`, homepage) VALUES
(5,'h.muller','',NULL,0,'',NULL,NULL,NULL,NULL,''),
(20, 'w.test.1','',NULL,0,'',NULL,'m',NULL,NULL, "hisPage"),
(21, 'w.test.2','', null, 0,'',NULL, "m",NULL,NULL,''),
(22, 'w.test.3','', null, 0, '',NULL,"m",NULL,NULL,''),
(23, 'w.test.4', '',NULL,0,'',NULL,'F',NULL,NULL,"myPage")
;


INSERT INTO `person_name` VALUES
(6,'Heinrich Georg','Müller','h.muller',0,'2015-07-06 14:23:05','testuserP'),
(7,'Henner','Schorsche','h.muller',1,'2015-07-06 14:23:05','testuserP'),
(31, 'Willi', 'Test', 'w.test.1', 1,'2015-07-06 14:23:05','testuserP'),
(32, 'Willi', 'Test', 'w.test.2', 1,'2015-07-06 14:23:05','testuserP'), 
(33, 'Willi', 'Test', 'w.test.3', 1,'2015-07-06 14:23:05','testuserP'), 
(34, 'Will', 'Test', 'w.test.4', 1,'2015-07-06 14:23:05','testuserP');

INSERT INTO `pub_person` VALUES
(3,'0b539e248a02e3edcfe591c64346c7a0','d63038ea59383b94bb52fc4a9b76d1f5','Maut',0,'h.muller',0,'2015-07-06 14:19:55','testuserP',0),
(23, '0c000000d00000f00cef0c00f000e00a', '0c000000d00000f00cef0c00f000e00a', 'Maut', 0, 'w.test.1', 0,'2015-07-06 14:19:55','testuserP', 0),
(24, '0c0000cdc00000b000cbe0fe0ab0acd0', '0c0000cdc00000b000cbe0fe0ab0acd0', 'Maut', 0, 'w.test.1', 0,'2015-07-06 14:19:55','testuserP', 0),
(25, '0a00d00000fc00000a0000a0000f0ad0', '0a00d00000fc00000a0000a0000f0ad0', 'Maut', 0, 'w.test.1', 0,'2015-07-06 14:19:55','testuserP', 0),
(26, '0d0b00c0000a000f00a00ad00ff612fc', '0d0b00c0000a000f00a00ad00ff612fc', 'Maut', 0, 'w.test.2', 0,'2015-07-06 14:19:55','testuserP', 0),
(27, '0ffa0a0ad000a00cbccf000adb0fdde0', '0ffa0a0ad000a00cbccf000adb0fdde0', 'Maut', 0, 'w.test.2', 0,'2015-07-06 14:19:55','testuserP', 0),
(28, 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'Maut', 0, 'w.test.2', 0,'2015-07-06 14:19:55','testuserP', 0),
(29, '0e0a00c000000f00d0d000b00eefe00b', '0e0a00c000000f00d0d000b00eefe00b', 'Maut', 0, 'w.test.3', 0,'2015-07-06 14:19:55','testuserP', 0),
(30, '00dc000febca00a0f0f00ce0de000000', '00dc000febca00a0f0f00ce0de000000', 'Maut', 0, 'w.test.4', 0,'2015-07-06 14:19:55','testuserP', 0);

INSERT INTO `person_match` (match_id, person1_id, person2_id, state) VALUES (1, "w.test.1", "w.test.2", 0),
(2, "w.test.1", "w.test.3", 0), (3, "w.test.2", "w.test.3", 0), (4, "w.test.1", "w.test.4", 0);

INSERT INTO `bibtex` (content_id, simhash0, simhash1, simhash2, simhash3, author, title, change_date,date,user_name) VALUES 
(34, '0c000000d00000f00cef0c00f000e00a', '0c000000d00000f00cef0c00f000e00a', '0c000000d00000f00cef0c00f000e00a', '0c000000d00000f00cef0c00f000e00a', 'Willi Test and Maria Mueller', 'title1', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(35, '0c0000cdc00000b000cbe0fe0ab0acd0', '0c0000cdc00000b000cbe0fe0ab0acd0', '0c0000cdc00000b000cbe0fe0ab0acd0', '0c0000cdc00000b000cbe0fe0ab0acd0', 'Willi Test and Johann Hilfe', 'title2', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(36, '0a00d00000fc00000a0000a0000f0ad0', '0a00d00000fc00000a0000a0000f0ad0', '0a00d00000fc00000a0000a0000f0ad0', '0a00d00000fc00000a0000a0000f0ad0', 'Willi Test', 'same', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(37, '0d0b00c0000a000f00a00ad00ff612fc', '0d0b00c0000a000f00a00ad00ff612fc', '0d0b00c0000a000f00a00ad00ff612fc', '0d0b00c0000a000f00a00ad00ff612fc', 'Willi Test and Mario Mueller', 'title4', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(38, '0ffa0a0ad000a00cbccf000adb0fdde0', '0ffa0a0ad000a00cbccf000adb0fdde0', '0ffa0a0ad000a00cbccf000adb0fdde0', '0ffa0a0ad000a00cbccf000adb0fdde0', 'Willi Test and Ted Hansen', 'title5', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(39, 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'eb0000af0a0c00b0b0ac0e0a0a00d0c0', 'Willi Test', 'same', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(40, '0e0a00c000000f00d0d000b00eefe00b', '0e0a00c000000f00d0d000b00eefe00b', '0e0a00c000000f00d0d000b00eefe00b', '0e0a00c000000f00d0d000b00eefe00b', 'Willi Test and Ted Hansen', 'title7', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP'),
(41, '00dc000febca00a0f0f00ce0de000000', '00dc000febca00a0f0f00ce0de000000', '00dc000febca00a0f0f00ce0de000000', '00dc000febca00a0f0f00ce0de000000', 'Willi Test and Johann Hilfe', 'title8', '2008-01-18 10:20:07','1815-12-10 00:00:00','testuserP');

INSERT INTO `bibhash` VALUES ('0c000000d00000f00cef0c00f000e00a', 1, 0),
('0c0000cdc00000b000cbe0fe0ab0acd0', 1, 0),
('0a00d00000fc00000a0000a0000f0ad0', 1, 0),
('0d0b00c0000a000f00a00ad00ff612fc', 1, 0),
('0ffa0a0ad000a00cbccf000adb0fdde0', 1, 0),
('eb0000af0a0c00b0b0ac0e0a0a00d0c0', 1, 0),
('0e0a00c000000f00d0d000b00eefe00b', 1, 0),
('00dc000febca00a0f0f00ce0de000000', 1, 0);

INSERT INTO `tas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(50, 'testtag',        'testtag',        34, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(51, 'testtag',        'testtag',        35, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(52, 'testtag',        'testtag',        36, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(53, 'testtag',        'testtag',        37, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(54, 'testtag',        'testtag',        38, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(55, 'testtag',        'testtag',        39, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(56, 'testtag',        'testtag',        40, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07'),
(57, 'testtag',        'testtag',        41, 1, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-01-18 10:20:07');

