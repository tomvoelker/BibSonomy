package org.bibsonomy.recommender.tags.simple.termprocessing;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bibsonomy.util.TagStringUtils;

/**
 * Extracts terms from given list of words, using a stopword remover and the
 * cleanTags function from the Discovery Challenge (e.g., remove everything
 * but letters and numbers). 
 * 
 * 
 * @author jil
 * @version $Id$
 */
public class TermProcessingIterator implements Iterator<String> {
	private final Iterator<String> words;
	private String next;
	private final StopWordRemover stopwordRemover = StopWordRemover.getInstance();

	/**
	 * @param words
	 */
	public TermProcessingIterator(Iterator<String> words) {
		this.words = words;
		fetchNext();
	}
	
	private void fetchNext() {
		/*
		 * skip empty tags
		 */
		while ((next == null || next.trim().equals("")) && words.hasNext()) {
			/*
			 * clean tag according to challenge rules
			 */
			next = TagStringUtils.cleanTag(words.next());
			/*
			 * ignore stop words and tags to be ignored according to the challenge rules
			 */
			if (stopwordRemover.process(next) == null || TagStringUtils.isIgnoreTag(next)) {
				next = null;
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return (next != null);
	}

	@Override
	public String next() {
		final String rVal;
		if (next != null) {
			rVal = next;
			next = null;
			fetchNext();
		} else {
			throw new NoSuchElementException();
		}
		return rVal;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
