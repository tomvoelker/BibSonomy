package org.bibsonomy.webapp.util.spring.security.encoding;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.util.StringUtils;


/**
 * TODO: document me
 * 
 * @author dzo
 * @version $Id$
 */
public class Md5PasswordEncoder extends org.springframework.security.authentication.encoding.Md5PasswordEncoder {
	
	@Override
	protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
		final String md5Password = StringUtils.getMD5Hash(password);
		if (present(salt) && present(salt.toString())) {
			return md5Password + salt.toString();
		}
		
		return md5Password;
	}
}
