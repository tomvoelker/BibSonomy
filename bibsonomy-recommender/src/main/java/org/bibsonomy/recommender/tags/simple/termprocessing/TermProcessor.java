package org.bibsonomy.recommender.tags.simple.termprocessing;

/**
 * @author jil
  */
public interface TermProcessor {
	
	/**
	 * @param term
	 * @return the processed term
	 */
	public String process(final String term);
}
