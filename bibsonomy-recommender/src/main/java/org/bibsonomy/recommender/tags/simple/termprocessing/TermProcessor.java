package org.bibsonomy.recommender.tags.simple.termprocessing;

/**
 * @author jil
 * @version $Id$
 */
public interface TermProcessor {
	
	/**
	 * @param term
	 * @return the processed term
	 */
	public String process(final String term);
}
