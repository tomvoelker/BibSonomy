package org.bibsonomy.database.params;

import org.bibsonomy.model.BibTexExtra;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraParam extends BibTexParam {

	private BibTexExtra bibtexExtra;

	public BibTexExtraParam() {
		this.bibtexExtra = new BibTexExtra();
	}

	public BibTexExtra getBibtexExtra() {
		return this.bibtexExtra;
	}

	public void setBibtexExtra(BibTexExtra extra) {
		this.bibtexExtra = extra;
	}
}