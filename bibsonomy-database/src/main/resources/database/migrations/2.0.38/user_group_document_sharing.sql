-- add userSharedDocuments column to membership table
ALTER TABLE groups ADD COLUMN `userSharedDocuments` tinyint(1) default '0';

-- remove the on update condition on start_date
ALTER TABLE groups CHANGE `start_date` `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- restore the status quo for groups sharing documents
UPDATE groups JOIN groupids USING (`group`) SET groups.userSharedDocuments = TRUE WHERE groupids.sharedDocuments = TRUE;