package org.bibsonomy.recommender.tags.simple.termprocessing;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.NoSuchElementException;

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

	public TermProcessingIterator(Iterator<String> words) {
		this.words = words;
		fetchNext();
	}
	
	private void fetchNext() {
		/*
		 * skip empty tags
		 */
		while ((next == null || next.trim().equals("")) && words.hasNext()) {
			next = cleanTag(words.next());
			if (stopwordRemover.process(next) == null) {
				next = null;
			}
		}
	}
	
	public boolean hasNext() {
		return (next != null);
	}

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

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Cleans the given tag according to the Discovery Challenge rules.
	 * 
	 * @param tag
	 * @return
	 */
	private static String cleanTag(final String tag) {
		return Normalizer.normalize(tag.toLowerCase().replaceAll("[^0-9\\p{L}]+", ""), Normalizer.Form.NFKC);
	}

}
