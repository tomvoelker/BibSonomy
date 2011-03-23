package org.bibsonomy.webapp.util.auth;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

/**
 * Simple test for encryption library.
 * 
 * @author dbenz
 * @version $Id$
 */
public class EncryptionTest {

	private static final Log LOGGER = LogFactory.getLog(EncryptionTest.class);

	private static final String TEST_TEXT = "This is to be encrypted...";
	private static final String TEST_PASSWORD = "hallo";
	private static final String WRONG_PASSWORD = "halloooo";

	/**
	 * test encryption
	 */
	@Test
	public void testBasicTextEncryptor() {
		/*
		 * encrypt
		 */
		LOGGER.info("Starting encryption test; trying to encrypt '" + TEST_TEXT + "'");
		final String text = TEST_TEXT;
		BasicTextEncryptor enc = new BasicTextEncryptor();
		enc.setPassword(TEST_PASSWORD);
		final String crypted = enc.encrypt(text);
		LOGGER.info("Encrypted text: " + crypted);
		/*
		 * decrypt
		 */
		LOGGER.info("Decrypt again. Result: " + enc.decrypt(crypted));
		/*
		 * decrypt with wrong password
		 */
		LOGGER.info("Trying to decrypt with wrong password...");
		enc = new BasicTextEncryptor();
		enc.setPassword(WRONG_PASSWORD);
		try {
			enc.decrypt(crypted);
			fail("Encryption with wrong password should not be possible!");
		} catch (EncryptionOperationNotPossibleException ex) {
			// this should be the case
		}
	}

}
