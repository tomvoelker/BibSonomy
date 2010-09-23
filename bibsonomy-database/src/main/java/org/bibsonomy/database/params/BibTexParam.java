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
}