package org.bibsonomy.webapp.util.importer;

import java.util.Collection;

import org.bibsonomy.model.User;

/**
 * base class for different user relation importers
 * 
 * @author fei
 * @version $Id$
 * @param <U> type of the imported user
 */
public abstract class AbstractFriendsImporter<U> {

	/**
	 * user adapter for mapping imported friends to bibsonomy user objects
	 * 
	 * @author fei
	 *
	 * @param <U> type of the imported user
	 */
	public interface UserAdapter<U> {
		/**
		 * @param user the imported user object
		 * @return name representation of the imported user
		 */
		public User getUser(final U user);
	}

	/**
	 * get the user adaptor for the imported user
	 * 
	 * @return a suitable user adaptor 
	 */
	abstract public UserAdapter<U> getUserAdapter();
	
	/**
	 * retrieve a list of users for the given login user
	 * 
	 * @param loginUser BibSonomy's login user
	 * @return list of imported friend objects
	 */
	abstract public Collection<U> getFriends(User loginUser);
}
