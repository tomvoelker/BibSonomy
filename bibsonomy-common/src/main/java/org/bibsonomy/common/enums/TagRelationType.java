package org.bibsonomy.common.enums;

/**
 * Defines types of relatedness between tags
 * 
 * FIXME: very bad wording - tag relations are something different (see "relations" and "concepts")
 * 
 * @author Dominik Benz

 * @version $Id$
 */
public enum TagRelationType {
	/** tag co-occurrence */
	COOC,
	/** cosine similarity*/
	COSINE;
}
