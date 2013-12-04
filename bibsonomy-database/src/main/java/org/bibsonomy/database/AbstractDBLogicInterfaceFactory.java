package org.bibsonomy.database;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.services.filesystem.FileLogic;

/**
 * Provides access to BibTeXReader and FileLogic for DBLogic usages.
 * @author niebler
 */
public abstract class AbstractDBLogicInterfaceFactory implements LogicInterfaceFactory {
	protected BibTexReader bibtexReader = null;
	protected FileLogic fileLogic;
	protected DBSessionFactory dbSessionFactory;

	/**
	 * @return the bibtexReader
	 */
	public BibTexReader getBibtexReader() {
		return this.bibtexReader;
	}

	public FileLogic getFileLogic() {
		return this.fileLogic;
	}

	/**
	 * @param bibtexReader the bibtexReader to set
	 */
	public void setBibtexReader(BibTexReader bibtexReader) {
		this.bibtexReader = bibtexReader;
	}

	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
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
