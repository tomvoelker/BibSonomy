package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.HomePageForBibtex;




/**
 * Can be used to get all BibTex entries of the main Page
 * 
 * @author mgr 
 *
 */
public class HomePageBibtex extends HomePageForBibtex{
	
	
	
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
	
	
	
	
}