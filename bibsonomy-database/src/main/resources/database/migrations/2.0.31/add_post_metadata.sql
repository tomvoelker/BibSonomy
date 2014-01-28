--
-- Table structure for table `post_metadata`
--

CREATE TABLE post_metadata (
  `user_name` VARCHAR(30),
  `intra_hash` CHAR(32),
  `inter_hash` CHAR(32),
  `key` VARCHAR(50),
  `value` TEXT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
);
