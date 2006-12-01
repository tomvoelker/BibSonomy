package org.bibsonomy.ibatis.params.bibtex;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByBibhash;
import org.bibsonomy.ibatis.util.BibTexUtils;

/**
 * Can be used to get all bibtex entries by its hash.
 * 
 * @author mgr
 *
 */



public class BibtexByHash extends ByBibhash{
	
	public String getBibtexEntries() {
		return BibTexUtils.getBibtexSelect("b");
	}

	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}