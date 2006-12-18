package org.bibsonomy.ibatis.params;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 */
public class BibTexParam extends GenericParam {

	private String requBibtex;

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public String getRequBibtex() {
		return this.requBibtex;
	}

	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}
}