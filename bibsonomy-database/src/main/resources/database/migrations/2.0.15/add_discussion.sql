--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `reviews` (
  `interHash` varchar(32) NOT NULL DEFAULT '',
  `text` varchar(255) DEFAULT NULL,
  `user_name` varchar(30) NOT NULL DEFAULT '',
  `date` timestamp NULL DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `helpful_count` int(11) DEFAULT '0',
  `not_helpful_count` int(11) DEFAULT '0',
  `change_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`interHash`,`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `reviews_helpful`
--

DROP TABLE IF EXISTS `reviews_helpful`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `reviews_helpful` (
  `interHash` varchar(32) DEFAULT NULL,
  `user_name` varchar(16) DEFAULT NULL,
  `mark_user_name` varchar(16) DEFAULT NULL,
  `helpful` tinyint(1) DEFAULT '0',
  `date` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `log_reviews_helpful`
--

DROP TABLE IF EXISTS `log_reviews_helpful`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `log_reviews_helpful` (
  `interHash` varchar(32) DEFAULT NULL,
  `user_name` varchar(16) DEFAULT NULL,
  `mark_user_name` varchar(16) DEFAULT NULL,
  `helpful` tinyint(1) DEFAULT '0',
  `date` timestamp NULL DEFAULT NULL,
  `log_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `log_reviews`
--

DROP TABLE IF EXISTS `log_reviews`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `log_reviews` (
  `interHash` varchar(32) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `change_date` timestamp NULL DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `helpful_count` int(11) DEFAULT NULL,
  `not_helpful_count` int(11) DEFAULT NULL,
  `log_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `review_ratings_cache`
--

DROP TABLE IF EXISTS `review_ratings_cache`;
SET @saved_cs_client     = @@character_set_client;
CREATE TABLE `review_ratings_cache` (
  `interHash` varchar(32) NOT NULL DEFAULT '',
  `number_of_ratings` int(11) DEFAULT NULL,
  `rating_arithmetic_mean` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`interHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;