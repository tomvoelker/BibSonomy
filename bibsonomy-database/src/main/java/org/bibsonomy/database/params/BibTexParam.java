package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.BibTex;

/**
 * Parameters that are specific to BibTex.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexParam extends ResourceParam<BibTex> {

	@Override
	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	/* 
	 * XXX: iBatis can't get generic informations; thinks that the class of
	 * the resource field is org.bibsonomy.model.Resource so we override it
	 * here
	 * 
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.params.SingleResourceParam#getResource()
	 */
	@Override
	public BibTex getResource() {
		return super.getResource();
	}
}