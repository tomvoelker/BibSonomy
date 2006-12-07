package org.bibsonomy.ibatis.params.bibtex;
import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.ByBibhash;

/**
 * Can be used to get all bibtex entries by its hash.
 * 
 * @author mgr
 *
 */



public class BibtexByHash extends ByBibhash{
	
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}