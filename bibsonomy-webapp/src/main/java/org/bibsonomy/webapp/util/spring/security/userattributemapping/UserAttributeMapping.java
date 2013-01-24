package org.bibsonomy.webapp.util.spring.security.userattributemapping;

import org.bibsonomy.model.User;

/**
 * Extracts attributes and pupolates a user object
 * @author jensi
 *
 * @param <FROM> Type of source object
 * @param <X> extra info
 */
public interface UserAttributeMapping<FROM, X> {
	/**
	 * Extracts attributes and pupolates a user object
	 * @param user object to populate
	 * @param src object to extract from
	 * @return extra info that comes from src object and needs to be known to come from there
	 */
	public X populate(User user, FROM src);
}
