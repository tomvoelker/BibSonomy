package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.HomePage;

/**
 * Can be used to get all BibTex entries of the main Page
 * 
 * @author mgr
 * 
 */
public class HomePageBibTex extends HomePage {

	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}
}