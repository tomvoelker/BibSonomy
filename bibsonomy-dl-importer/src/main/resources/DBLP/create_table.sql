CREATE TABLE `DBLPFailures` (
  `date_of_create` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `snippet` varchar(255) default NULL,
  `user_name` varchar(255) default NULL,
  `failure_type` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 

CREATE TABLE `DBLP` (
  `lastupdate` datetime NOT NULL default '1815-12-10 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into DBLP values(DEFAULT);

-- insert first default date --
insert into DBLP set lastupdate="1815-12-10 00:00:00";