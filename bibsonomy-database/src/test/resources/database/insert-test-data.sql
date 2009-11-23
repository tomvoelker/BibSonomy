--
-- BibSonomy Test Database
--
-- This SQL script fills in some test data for the unit tests of the database 
-- module. It expects the existence of all tables; their create statements
-- are found in src/main/resources/bibsonomy_db_schema.sql
--
-- $Id$
-- 



-- 
-- Data for table `classifier_settings`
--

INSERT INTO `classifier_settings` (`key`, `value`) VALUES 
('algorithm', 'weka.classifiers.lazy.IBk'),
('mode', 'D');


-- 
-- Data for table `ContentModifiedTags`
-- 

-- 
-- Data for table `DBLP`
-- 

INSERT INTO `DBLP` VALUES 
('1815-12-10 00:00:00');


-- 
-- Data for table `DBLPFailures`
-- 


-- 
-- Data for table `MostSimTagsByContent`
-- 


-- 
-- Data for table `TagContent`
-- 



-- 
-- Data for table `TagUser`
-- 



-- 
-- Data for table `TmpMostSimTagsByContent`
-- 



-- 
-- Data for table `answers`
-- 



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
('d9eea4aa159d70ecfabafa0c91bbc9f0', 2, 1),
('1b298f199d487bc527a62326573892b8', 1, 2),
('522833042311cc30b8775772335424a7', 1, 2),
('65e49a5791c3dae2356d26fb9040fe29', 1, 2),
('b71d5283dc7f4f59f306810e73e9bc9a', 1, 2),
('b77ddd8087ad8856d77c740c8dc2864a', 1, 2);



-- 
-- Data for table `bibtex`
-- 

INSERT INTO `bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `booktitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `group`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `entrytype`, `title`, `author`, `editor`, `year`, `privnote`, `scraperid`, `change_date`, `rating`) VALUES
(10, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '1815-12-10 00:00:00', 'testuser1',   'http://www.testurl.org',        '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '9abf98937435f05aec3d58b214a2ac58', 'd9eea4aa159d70ecfabafa0c91bbc9f0', 'b77ddd8087ad8856d77c740c8dc2864a', '', 'test entrytype', 'test title',         'test author',  'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:24:55', 0),
(11, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test spam booktitle',       'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 0, '1815-12-10 00:00:00', 'testspammer', 'test url',                      '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'b386bdfc8ac7b76ca96e6784736c4b95', '96c7cf1a752564f8ae0b6540e131af73', '65e49a5791c3dae2356d26fb9040fe29', '', 'test entrytype', 'test spam title',    'test spammer', 'test editor', 'test year', 'test privnote', -1, '2008-03-20 16:34:34', 0),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 3, '1815-12-10 00:00:00', 'testuser1',   'http://www.testurl.org',        '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '92e8d9c7588eced69419b911b31580ee', 'd9eea4aa159d70ecfabafa0c91bbc9f0', '522833042311cc30b8775772335424a7', '', 'test entrytype', 'test title',         'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-05 18:11:55', 0),
(13, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 1, '1815-12-10 00:00:00', 'testuser2',   'http://private.bibtex.url.com', '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '8711751127efb070ee910a5d145a168b', 'ac6aa3ccb181e61801cefbc1401d409a', '1b298f199d487bc527a62326573892b8', '', 'test entrytype', 'test private title', 'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0),
(14, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', 2, '1815-12-10 00:00:00', 'testuser2',   'http://friend.bibtex.url.com',  '2', 'test description', 'test annote', 'test note', 'test page',  'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', '36a19ee7b7923b062a99a6065fe07792', 'e2fb0763068b21639c3e36101f64aefe', 'b71d5283dc7f4f59f306810e73e9bc9a', '', 'test entrytype', 'test friend title',  'test author',  'test editor', 'test year', 'test privnote', -1, '2008-05-19 14:34:29', 0);


-- 
-- Data for table `bibtexurls`
-- 

INSERT INTO `bibtexurls` (`content_id`, `url`, `text`, `group`, `date`) VALUES
(10, 'http://www.testurl.org', 'test text', 0, '2008-07-09 11:03:24');


-- 
-- Data for table `bookmark`
-- 

INSERT INTO `bookmark` (`content_id`, `book_url_hash`, `book_description`, `book_extended`, `group`, `date`, `user_name`, `to_bib`, `change_date`, `rating`) VALUES
(1, '6f372faea7ff92eedf52f597090a6291', 'test bookmark descripton', 'test bookmark extended',    0, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:17:10', 0),
(2, '108eca7b644e2c5e09853619bc416ed0', 'Google',                   'bekannteste Suchmaschine',  0, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:34:17', 0),
(3, '7eda282d1d604c702597600a06f8a6b0', 'Yahoo',                    'Yahoo Suchmaschine',        0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 10:16:55', 0),
(4, 'b7aa3a91885e432c6c95bec0145c3968', 'FriendScout24',            'Seite f√ºr einen "friend"', 2, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-18 10:16:46', 0),
(5, '965a65fdc161e354f3828050390e2b06', 'web.de',                   'WEB.de Freemail',           0, '1815-12-10 00:00:00', 'testuser3', 0, '2008-01-18 10:16:39', 0),
(6, '20592a292e53843965c1bb42bfd51876', 'uni-kassel',               'UniK',                      0, '1815-12-10 00:00:00', 'testuser2', 0, '2008-01-18 11:29:03', 0),
(7, '16dfed76f9d846056a6a3c0d022c3493', 'finetune',                 'finetune.com',              4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:14:33', 0),
(8, 'e9ea2574c49c3778f166e8b4b6ed63dd', 'apple',                    'apple.com',                 4, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-21 13:20:57', 0),
(9, '294a9e1d594297e7bb9da9e11229c5d7', 'fireball.com',             'fireball',                  1, '1815-12-10 00:00:00', 'testuser1', 0, '2008-01-29 10:36:06', 0);


-- 
-- Data for table `collector`
-- 
INSERT INTO `collector` (`user_name`, `content_id`, `date`) VALUES
('testuser1', 10, '2008-06-18 14:27:35'),
('testuser1', 12, '2008-06-18 14:27:35'),
('testuser2', 13, '2008-06-18 14:33:01'),
('testuser2', 14, '2008-06-18 14:33:22');


-- 
-- Data for table `document`
-- 
INSERT INTO `document` VALUES 
('00000000000000000000000000000000', 10, 'testdocument_1.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000000'),
('00000000000000000000000000000001', 10, 'testdocument_2.pdf', 'testuser1', '2008-10-01 01:01:01', '00000000000000000000000000000001');


-- 
-- Data for table `extended_fields_data`
-- 



-- 
-- Data for table `extended_fields_map`
-- 


-- 
-- Data for table `friends`
-- 

INSERT INTO `friends` VALUES 
(1, 'testuser1', 'testuser2', '1815-12-10 00:00:00'),
(3, 'testuser1', 'testuser3', '1815-12-10 00:00:00'),
(2, 'testuser2', 'testuser1', '1815-12-10 00:00:00');

-- 
-- Data for table `followers`
-- 

INSERT INTO `followers` VALUES 
(1, 'testuser1', 'testuser2', '1815-12-10 00:00:00'),
(3, 'testuser1', 'testuser3', '1815-12-10 00:00:00'),
(2, 'testuser2', 'testuser1', '1815-12-10 00:00:00');


-- 
-- Data for table `groupids`
-- 

INSERT INTO `groupids` VALUES 
('public',     -2147483648, 1, 0),
('private',    -2147483647, 1, 0),
('friends',    -2147483646, 1, 0),
('public',     0,           1, 0),
('private',    1,           1, 0),
('friends',    2,           1, 0),
('testgroup1', 3,           0, 1),
('testgroup2', 4,           1, 0),
('testgroup3', 5,           2, 0);


-- 
-- Data for table `groups`
-- 

INSERT INTO `groups` VALUES 
('testuser1', 3, 3, '2007-01-01 01:01:01', 7),
('testuser2', 3, 3, '2007-01-01 01:01:01', 7),
('testuser1', 4, 3, '2007-01-01 01:01:01', 7),
('testuser1', 5, 3, '2007-01-01 01:01:01', 7);


-- 
-- Data for table `highwirelist`
-- 

INSERT INTO `highwirelist` VALUES 
('foo', '2007-12-20 20:36:50');


-- 
-- Data for table `ids`
-- 

INSERT INTO `ids` VALUES 
(0,  14, 'content_id'),
(1,  24, 'tas id'),
(2,  0,  'relation id'),
(3,  0,  'question id'),
(4,  1,  'cycle id'),
(5,  0,  'extended_fields_id'),
(7,  0,  'scraper_metadata_id'),
(12, 28, 'grouptas id'),
(14, 3,  'message_id');



--
-- Data for table `inboxMail`
--

INSERT INTO `inboxMail` VALUES
(1, 1, 'hash1', 'testuser1', 'testuser2', '2009-10-08 14:23:00'),
(2, 5, 'hash2', 'testuser3', 'testuser2', '2009-10-08 14:23:32'),
(3, 10, 'hash3', 'testuser3', 'testuser2', '2009-10-08 14:23:32');

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



-- 
-- Data for table `log_bookmark`
-- 



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




-- 
-- Data for table `log_user`
-- 



-- 
-- Data for table `log_prediction`
-- 

INSERT INTO `log_prediction` VALUES 
(1, 'testspammer', 1, UNIX_TIMESTAMP(NOW()),'2008-06-18 14:27:35', 'testlogging', 0, 0.2);



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
-- Data for table `search_bibtex`
-- 

INSERT INTO `search_bibtex` (`content_id`, `content`, `author`, `group`, `date`, `user_name`) VALUES
(10, 'test bibtext search string', 'test author', 0, '1815-12-10 00:00:00', 'testuser1');

-- 
-- Data for table `search_bookmark`
-- 

INSERT INTO `search_bookmark` (`content_id`, `content`, `group`, `date`, `user_name`) VALUES
(2, 'google suchmaschine gmail earth sketchup maps news images bot adwords', 0, '1815-12-10 00:00:00', 'testuser1');



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
(19, 'friendbibtex',  '', 1, 1, 0);



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




-- 
-- Data for table `tas`
-- 

INSERT INTO `tas` (`tas_id`, `tag_name`, `tag_lower`, `content_id`, `content_type`, `user_name`, `date`, `group`, `change_date`) VALUES
(1, 'testtag',        'testtag',        1, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:20:07'),
(2, 'google',         'google',         2, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:20:17'),
(3, 'suchmaschine',   'suchmaschine',   2, 1, 'testuser1',   '1815-12-10 00:00:00', 0, '2008-01-18 10:19:51'),
(4, 'yahoo',          'yahoo',          3, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 10:21:12'),
(5, 'suchmaschine',   'suchmaschine',   3, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 10:21:47'),
(6, 'friends',        'friends',        4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:31'),
(7, 'friendscout',    'friendscout',    4, 1, 'testuser1',   '1815-12-10 00:00:00', 2, '2008-01-18 10:24:44'),
(8, 'web',            'web',            5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(9, 'freemail',       'freemail',       5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(10, 'suchmaschine',  'suchmaschine',   5, 1, 'testuser3',   '1815-12-10 00:00:00', 0, '2008-01-18 10:24:14'),
(11, 'uni',           'uni',            6, 1, 'testuser2',   '1815-12-10 00:00:00', 0, '2008-01-18 11:30:05'),
(12, 'kassel',        'kassel',         6, 1, 'testuser2',   '0000-00-00 00:00:00', 0, '2008-01-18 11:30:05'),
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
(24, 'testbibtex',    'testbibtex',    12, 2, 'testuser1',   '1815-12-10 00:00:00', 3, '2008-03-20 20:35:21');



-- 
-- Data for table `temp_bibtex`
-- 

INSERT INTO `temp_bibtex` (`content_id`, `journal`, `volume`, `chapter`, `edition`, `month`, `day`, `bookTitle`, `howPublished`, `institution`, `organization`, `publisher`, `address`, `school`, `series`, `bibtexKey`, `date`, `user_name`, `url`, `type`, `description`, `annote`, `note`, `pages`, `bKey`, `number`, `crossref`, `misc`, `bibtexAbstract`, `entrytype`, `title`, `author`, `editor`, `year`, `simhash0`, `simhash1`, `simhash2`, `simhash3`, `ctr`, `rank`, `rating`, `popular_days`) VALUES
(10, 'test journal',            'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle',            'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '9abf98937435f05aec3d58b214a2ac58', 'd9eea4aa159d70ecfabafa0c91bbc9f0', 'b77ddd8087ad8856d77c740c8dc2864a', '', 1, 1, 0, 1),
(12, 'test journal for group3', 'test volume', 'test chapter', 'test edition', 'test month', 'test day', 'test booktitle for group3', 'test howPublished', 'test institution', 'test organization', 'test publisher', 'test address', 'test school', 'test series', 'test bibtexKey', '1815-12-10 00:00:00', 'testuser1', 'http://www.testurl.org', '2', 'test description', 'test annote', 'test note', 'test pages', 'test bKey', 'test number', 'test crossref', 'test misc', 'test bibtexAbstract', 'test entrytype', 'test title', 'test author', 'test editor', 'test year', '92e8d9c7588eced69419b911b31580ee', 'd9eea4aa159d70ecfabafa0c91bbc9f0', '522833042311cc30b8775772335424a7', '', 1, 2, 0, 2);



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
('e9ea2574c49c3778f166e8b4b6ed63dd', 'http://www.apple.com\r\n', 1);



-- 
-- Data for table `user`
-- 


-- `user_name`,`user_email`,`user_password`,`user_homepage`,`user_realname`,`spammer`,`openurl`,`reg_date`,`ip_address`,`id`,`tmp_password`,`tmp_request_date`,`tagbox_style`,`tagbox_sort`,`tagbox_minfreq`,`tagbox_tooltip`,`list_itemcount`,`spammer_suggest`
-- `birthday`,`gender`,`profession`,`interests`,`hobbies`,`place`,`profilegroup`
-- `api_key`,`updated_by`,`updated_at`,`role`,`lang`,`to_classify`,`log_level`

 
INSERT INTO `user` (`user_name`,`user_email`,`user_password`,`user_homepage`,`user_realname`,`spammer`,`openurl`,`reg_date`,`ip_address`,`id`,`tmp_password`,`tmp_request_date`,`tagbox_style`,`tagbox_sort`,`tagbox_minfreq`,`tagbox_tooltip`,`list_itemcount`,`spammer_suggest`,`birthday`,`gender`,`profession`,`interests`,`hobbies`,`place`,`profilegroup`,`api_key`,`updated_by`,`updated_at`,`role`,`lang`,`to_classify`,`log_level`) VALUES 
--user_name     user_email                   user_password   user_homepage                           user_realname   spammer   openurl                       reg_date               ip_address id    tmp_password tmp_request_date tagbox_style tagbox_sort tagbox_minfreq tagbox_tooltip list_itemcount  spammer_suggest birthday gender profession interests hobbies place                               profilegroup api_key                             updated_by updated_at             role lang to_classify log_level
('testgroup1',  'testgroup1@bibsonomy.org',  'test123', 'http://www.bibsonomy.org/group/testgroup1', 'Test Group 1', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testgroup2',  'testgroup2@bibsonomy.org',  'test123', 'http://www.bibsonomy.org/group/testgroup2', 'Test Group 2', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testgroup3',  'testgroup3@bibsonomy.org',  'test123', 'http://www.bibsonomy.org/group/testgroup3', 'Test Group 3', 0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testspammer', 'testspammer@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/',                 'Test Spammer', 1, 'http://sfxserv.rug.ac.be:8888/rug', '2007-02-02 02:02:02', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testspammer2', 'testspammer@bibsonomy.org', 'test123', 'http://www.bibsonomy.org/',                 'Test Spammer', 1, 'http://sfxserv.rug.ac.be:8888/rug', '2007-02-02 02:02:02', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           NULL,                               'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testuser1',   'testuser1@bibsonomy.org',   'test123', 'http://www.bibsonomy.org/user/testuser1',   'Test User 1',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           '11111111111111111111111111111111', 'rja',     '1815-12-10 00:00:00', 0,  'en', 0, 1),
('testuser2',   'testuser2@bibsonomy.org',   'test123', 'http://www.bibsonomy.org/user/testuser2',   'Test User 2',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           '22222222222222222222222222222222', 'rja',     '1815-12-10 00:00:00', 1,  'en', 0, 1),
('testuser3',   'testuser3@bibsonomy.org',   'test123', 'http://www.bibsonomy.org/user/testuser3',   'Test User 3',  0, 'http://sfxserv.rug.ac.be:8888/rug', '2007-01-01 01:01:01', '0.0.0.0', NULL, NULL, '1815-12-10 00:00:00',  0, 0, 0, 0, 10,                                                        1,              NULL,    'm', 'test-profession', 'test-interests', 'test-hobbies', 'test-place', 1,           '33333333333333333333333333333333', 'rja',     '1815-12-10 00:00:00', 1,  'en', 1, 0);



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
-- Data for table `weights`
--


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
(12, 'kassel',        'kassel',         6, 1, 'testuser2',   '0000-00-00 00:00:00', 3, '2008-01-18 11:30:05'),
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



