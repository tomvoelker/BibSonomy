package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByBibtexDownload;

/**
 * Can be used to get all bibtex entries, which are pick by the current user.
 * 
 * @author mgr
 *
 */





public class BibtexByDownload extends ByBibtexDownload{
	
	
	
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
	
	
}