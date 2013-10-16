ALTER TABLE user ADD user_password_salt CHAR(16) AFTER user_password;
ALTER TABLE pendingUser ADD user_password_salt CHAR(16) AFTER user_password;
ALTER TABLE log_user ADD user_password_salt CHAR(16) AFTER user_password;
