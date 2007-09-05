PRIMARY KEY  (`content_id`),
KEY `user_name_idx` (`user_name`),
KEY `book_url_hash_idx` (`book_url_hash`),
KEY `date_idx` (`date`)

PRIMARY KEY  (`tag_id`),
KEY `tag_name_idx` (`tag_name`),
KEY `content_id_idx` (`content_id`),
KEY `tag_name_content_id_idx` (`tag_name`,`content_id`),
KEY `content_type_tag_name_content_id_idx` (`content_type`,`tag_name`,`content_id`),
KEY `content_type_tag_name_content_id_date_idx` (`content_type`,`tag_name`,`content_id`,`date`),
KEY `tag_name_date_idx` (`tag_name`,`date`),
KEY `user_name_idx` (`user_name`),
KEY `user_name_tag_name_idx` (`user_name`,`tag_name`),
KEY `user_name_tag_name_date_idx` (`user_name`,`tag_name`,`date`)


create index `group_date_content_id_idx` on bookmark (`group`, `date` desc, `content_id`);
create index `user_name_group_date_content_id_idx` on bookmark (`user_name`, `group`, `date` desc,`content_id`);
create index `book_url_hash_group_date_content_id_idx` on bookmark (`book_url_hash`,`group`,`date` desc,`content_id`);

create index `content_type_group_tag_name_date_content_id_idx` on tas (`content_type`,`group`,`tag_name`,`date` desc,`content_id`);
create index `user_name_tag_name_content_type_group_date_content_id_idx` on tas (`user_name`,`tag_name`,`content_type`,`group`,`date` desc,`content_id`);

create index `group_tag_name_idx` on tas (`group`,`tag_name`);
create index `user_name_group_tag_name_idx` on tas (`user_name`, `group`, `tag_name`);
create index `user_name_tag_name_content_id_idx` on tas (`user_name`, `tag_name`, `content_id`);


drop index `group_date_content_id_idx` on bookmark ;
drop index `user_name_group_date_content_id_idx` on bookmark ;
drop index `book_url_hash_group_date_content_id_idx` on bookmark ;

drop index `content_type_group_tag_name_date_content_id_idx` on tas ;
drop index `user_name_tag_name_content_type_group_date_content_id_idx` on tas ;

drop index `group_tag_name_idx` on tas ;
drop index `user_name_group_tag_name_idx` on tas ;
drop index `user_name_tag_name_content_id_idx` on tas ;
