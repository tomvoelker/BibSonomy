ALTER TABLE `group_memberships` ADD PRIMARY KEY (`group`,`user_name`);

/* set new membership ids */
UPDATE group_memberships SET group_memberships.group_role = 2 WHERE group_memberships.group_role = 7;

-- set the admin user role to the group user dummy for old groups
UPDATE groupids
  JOIN user ON group_name = user.user_name
  JOIN group_memberships ON groupids.`group` = group_memberships.`group` AND group_memberships.user_name = group_name
SET group_role = 0;
