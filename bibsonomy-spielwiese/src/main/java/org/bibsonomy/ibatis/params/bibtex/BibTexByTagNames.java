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

	public String getBibtexSelect() {
		return BibTexUtils.getBibtexSelect("b");
	}

	public int getSimHash() {
		return ConstantID.SIM_HASH.getId();
	}

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}