package org.bibsonomy.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the "To"-Header of E-Mails and extracts the credentials.
 * 
 * Current format of To-Header:
 * 
 * <pre>
 * John Doe <johndoe-299cafad8ce2afb5879c6c85c14cc5259@api.bibsonomy.org>
 * </pre>
 * 
 * maximal 64 characters for the local part!
 * 
 * FIXME: we can't use our usernames - they contain forbidden characters.
 * FIXME: what about des(username + " " + binary(apikey), key)?
 * 
 * Unser Benutzername hat maximal 30 Zeichen (leider UTF-8, d.h., maximal
 * 30 * 4 Zeichen). 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class ToFieldParser {
	// FIXME: we can't allow "-" because we need it as separator
	private static final String EMAIL_ALLOWED_ADDRESS_CHARS = "a-zA-Z0-9\\-\\.";
	private static final Pattern ADDRESS_API_PATTERN = Pattern.compile(
			"([" + EMAIL_ALLOWED_ADDRESS_CHARS + "]+)" +    // user name 
			"-" + 
			"([0-9a-fA-F]{32})" +                           // api key
			"\\+?" +
			"([" + EMAIL_ALLOWED_ADDRESS_CHARS + "]+?)?" +  // group?
			"@"
	);

	/**
	 * FIXME: the group is not a credential!
	 * 
	 * @param to
	 * @return
	 */
	public ToField parseToField(final String to) {
		final Matcher matcher = ADDRESS_API_PATTERN.matcher(to);
		if (matcher.find()) {
			final String username = matcher.group(1);
			final String apikey   = matcher.group(2);
			final String group    = matcher.group(3);

			final ToField toField = new ToField();
			toField.setUsername(username);
			toField.setApikey(apikey);
			toField.setGroup(group);
			return toField;
		} 
		return null;
	}

}
