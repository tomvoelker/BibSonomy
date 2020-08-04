ALTER TABLE `user` ADD COLUMN `person_posts_style` tinyint(4) NOT NULL DEFAULT '0' AFTER `useExternalPicture`;
ALTER TABLE `user` ADD COLUMN `person_posts_layout` varchar(255) NOT NULL DEFAULT '' AFTER `person_posts_style`;
ALTER TABLE `user` ADD COLUMN `person_posts_per_page` int(5) NOT NULL DEFAULT 50 AFTER `person_posts_layout`;
ALTER TABLE `pendingUser` ADD COLUMN `person_posts_style` tinyint(4) NOT NULL DEFAULT '0' AFTER `useExternalPicture`;
ALTER TABLE `pendingUser` ADD COLUMN `person_posts_layout` varchar(255) NOT NULL DEFAULT '' AFTER `person_posts_style`;
ALTER TABLE `pendingUser` ADD COLUMN `person_posts_per_page` int(5) NOT NULL DEFAULT 50 AFTER `person_posts_layout`;
