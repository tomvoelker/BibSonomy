/* issue #2522, alter column `callback_url` datatype to TEXT - was VARCHAR(255) */
ALTER TABLE `oauth_provider_tokens` 
CHANGE COLUMN `callback_url` `callback_url` TEXT NULL DEFAULT NULL ;