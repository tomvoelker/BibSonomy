package org.bibsonomy.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.BibTexReader;

/**
 * Provides access to BibTeXReader and FileLogic for DBLogic usages.
 * @author niebler
 */
public abstract class AbstractDBLogicInterfaceFactory implements LogicInterfaceFactory {
	protected BibTexReader bibtexReader = null;
	protected DBSessionFactory dbSessionFactory;

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

	/**
	 * @param dbSessionFactory
	 *            the {@link DBSessionFactory} to use
	 */
	public void setDbSessionFactory(final DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	public DBSessionFactory getDbSessionFactory() {
		return this.dbSessionFactory;
	}

	/**
	 * Returns a new database session.
	 */
	protected DBSession openSession() {
		return this.getDbSessionFactory().getDatabaseSession();
	}
}
