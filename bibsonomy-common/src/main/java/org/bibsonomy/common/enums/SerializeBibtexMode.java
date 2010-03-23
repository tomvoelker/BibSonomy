package org.bibsonomy.common.enums;

/**
 * Defines modes while serializing a bibtex object to a bibtex string.
 * 
 * @author dbenz 
 * @version $Id$
 */
public enum SerializeBibtexMode {
	/** include misc fields as they are */
	PLAIN_MISCFIELDS,
	/** use parsed miscfields */
	PARSED_MISCFIELDS;
}
