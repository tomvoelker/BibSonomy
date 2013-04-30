package org.bibsonomy.marc;

import org.bibsonomy.model.BibTex;

/**
 * @author jensi
 * @version $Id$
 */
public interface AttributeExtractor {
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src);
}
