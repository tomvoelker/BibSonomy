ALTER TABLE `user` ADD COLUMN `person_posts_style` tinyint(4) NOT NULL AFTER `useExternalPicture`;
ALTER TABLE `user` ADD COLUMN `person_posts_layout` varchar(255) NOT NULL AFTER `person_posts_style`;
ALTER TABLE `pendingUser` ADD COLUMN `person_posts_style` tinyint(4) NOT NULL AFTER `useExternalPicture`;
ALTER TABLE `pendingUser` ADD COLUMN `person_posts_layout` varchar(255) NOT NULL AFTER `person_posts_style`;