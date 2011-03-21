package org.bibsonomy.webapp.util.auth;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

/**
 * @author dbenz
 * @version $Id$
 */
public class EncryptionTest {
	
	@Test
	public void testEncryption() {
		final String text = "This is to be encrypted...";
		BasicTextEncryptor enc = new BasicTextEncryptor();
		enc.setPassword("hallo");
		System.out.println(text);
		final String crypted = enc.encrypt(text);
		System.out.println(crypted);
		System.out.println(enc.decrypt(crypted));
		enc = new BasicTextEncryptor();
		enc.setPassword("hallo");
		System.out.println(enc.decrypt(crypted));
		enc = new BasicTextEncryptor();
		enc.setPassword("halloo");
		
		final String test1 = "HwWtDV5lSRLELKJd/vkmfWU3e/bpCqIKgqJiugRa0CP7QGKl4NGBPw==";
		final String test2 = "s7ZQxev49d412w4+j3jNjnbUQCt7yudUkc6Lv//Slbz5boVosq8+XQ==";
		System.out.println(enc.decrypt(test1));
		System.out.println(enc.decrypt(test2));
	}
	
}
