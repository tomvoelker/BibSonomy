package org.bibsonomy.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.BibTexReader;

/**
 * This is a temporary logic interface factory to enable logic interface access
 * from within BibSonomy 1 without having to re-do authentication
 * 
 * XXX: Please remove this class once this is not necessary anymore.
 * 
 * dbe, 20071203
 * 
 * @author Dominik Benz
 */
public class DBLogicNoAuthInterfaceFactory implements LogicInterfaceFactory {

	private DBSessionFactory dbSessionFactory;
	
	private BibTexReader bibtexReader = null;

	@Override
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			/*
			 * In this case we don't fill the user object completely, but set
			 * it's name such that the user is seen as logged in (users which
			 * are not logged in cause a user object with empty name).
			 */
			return new DBLogic(new User(loginName), this.dbSessionFactory, this.bibtexReader);
		}
		// guest access
		return new DBLogic(new User(), this.dbSessionFactory, this.bibtexReader);
	}

	/**
	 * @param dbSessionFactory
	 *            the {@link DBSessionFactory} to use
	 */
	public void setDbSessionFactory(final DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * Returns a new database session.
	 */
	protected DBSession openSession() {
		return dbSessionFactory.getDatabaseSession();
	}
	
	
	/**
	 * @return the bibtexReader
	 */
	public BibTexReader getBibtexReader() {
		return this.bibtexReader;
	}

	/**
	 * @param bibtexReader the bibtexReader to set
	 */
	public void setBibtexReader(BibTexReader bibtexReader) {
		this.bibtexReader = bibtexReader;
	}
}