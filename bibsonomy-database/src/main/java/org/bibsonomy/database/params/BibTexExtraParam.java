package org.bibsonomy.database.params;

import org.bibsonomy.model.extra.BibTexExtra;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtraParam extends BibTexParam {

	private BibTexExtra bibtexExtra;

	/**
	 * @return BibTexExtra
	 */
	public BibTexExtra getBibtexExtra() {
		if (this.bibtexExtra == null) this.bibtexExtra = new BibTexExtra();
		return this.bibtexExtra;
	}

	/**
	 * @param extra
	 */
	public void setBibtexExtra(BibTexExtra extra) {
		this.bibtexExtra = extra;
	}
}