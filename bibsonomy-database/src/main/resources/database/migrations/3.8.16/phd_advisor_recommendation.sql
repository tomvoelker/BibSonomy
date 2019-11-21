DROP TABLE IF EXISTS `phd_advisor_recommendation`;
CREATE TABLE `phd_advisor_recommendation`(
    `doctor_id` varchar(64) NOT NULL,
    `simhash2` char(32) NOT NULL,
    `advisor_id` varchar(64) default NULL,
    `confidence` DOUBLE default NULL,
    `rank` TINYINT NOT NULL,
    PRIMARY KEY (`doctor_id`,`simhash2`,`rank`),
    KEY (`simhash2`)
);
