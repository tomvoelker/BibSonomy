package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

/**
 * @author jensi
 * @version $Id$
 */
public interface NameSpacedNameMapping<T> {
	/**
	 * @param remoteId name to be mapped
	 * @return mapped name
	 */
	public String map(T remoteId);
}
