package org.bibsonomy.recommender.tag.util.termprocessing;

import java.util.Iterator;

import org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator;
import org.bibsonomy.util.TagStringUtils;

/**
 * extends {@link TermProcessingIterator} by the
 * cleanTags function from the Discovery Challenge (e.g., remove everything
 * but letters and numbers). 
 *
 * @author jil, dzo
 */
public class TagTermProcessorIterator extends TermProcessingIterator {

	/**
	 * @param words
	 */
	public TagTermProcessorIterator(Iterator<String> words) {
		super(words);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator#cleanWord(java.lang.String)
	 */
	@Override
	protected String cleanWord(String word) {
		return TagStringUtils.cleanTag(word);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator#acceptWord(java.lang.String)
	 */
	@Override
	protected boolean acceptsWord(String word) {
		return super.acceptsWord(word) && !TagStringUtils.isIgnoreTag(word);
	}

}
