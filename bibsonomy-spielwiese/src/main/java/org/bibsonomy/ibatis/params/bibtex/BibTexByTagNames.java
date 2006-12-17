package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByTagNames;
import org.bibsonomy.ibatis.util.BibTexUtils;

/**
 * Can be used to search tags by its name.
 * 
 * @author Christian Schenk
 */
public class BibTexByTagNames extends ByTagNames {

	// FIXME is done by iBATIS
	public String getBibtexSelect() {
		return BibTexUtils.getBibtexSelect("b");
	}

	// FIXME not needed anymore
	public int getSimHash() {
		return ConstantID.SIM_HASH.getId();
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}