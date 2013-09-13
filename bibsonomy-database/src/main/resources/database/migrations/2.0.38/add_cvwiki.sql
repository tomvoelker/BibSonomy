CREATE TABLE `user_wiki` (
  `user_name` varchar(30) NOT NULL,
  `user_wiki` text,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO user_wiki SELECT user_name, "== personal data ==
{| class=\"wikitable\" style=\"text-align:left;padding:15px\"
|style=\"width: 5em\", rowspan=\"5\" | <image style=\"float: center\" />
! name || <name />
|-
!style=\"width: 10em\"| location || <location />
|-
! date of birth || <birthday />
|-
! profession || <profession />
|-
! institution || <institution />
|}

==scientific interests==
<interests />

==hobby==
<hobbies />

==my publications==
<publications tags=\"myown\" layout=\"plain\" />

==my bookmarks==
<bookmarks />
", CURRENT_TIMESTAMP() AS date FROM user;

UPDATE user_wiki, groupids SET user_wiki.user_wiki="==Grouppage of the group <name/>==
<groupimage />

==Members==
<members />

==Recently added bookmarks==
<bookmarks tags=\"myown\" limit=\"3\" />

==Recently added publications==
<publications order=\"asc\" keys=\"year\" limit=\"3\" />" WHERE user_wiki.user_name=groupids.group_name