package org.bibsonomy.database;

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

	/**
	 * @return the bibtexReader
	 */
	public BibTexReader getBibtexReader() {
		return this.bibtexReader;
	}

	public FileLogic getFileLogic() {
		return fileLogic;
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

	
	
}
