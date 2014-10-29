ALTER TABLE `user` ADD COLUMN `useExternalPicture` TINYINT(1) NULL DEFAULT '0';
ALTER TABLE `pendingUser` ADD COLUMN `useExternalPicture` TINYINT(1) NULL DEFAULT '0';
ALTER TABLE `log_user` ADD COLUMN `useExternalPicture` TINYINT(1) NULL DEFAULT '0';