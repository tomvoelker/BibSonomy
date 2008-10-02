package org.bibsonomy.recommender.tags.simple.termprocessing;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author jil
 * @version $Id$
 */
public class TermProcessingIterator implements Iterator<String> {
	private final Iterator<String> words;
	private String next;
	private String nextLower;
	private final StopWordRemover stopwordRemover = StopWordRemover.getInstance();

	public TermProcessingIterator(Iterator<String> words) {
		this.words = words;
		fetchNext();
	}
	
	private void fetchNext() {
		while ((next == null) && (words.hasNext() == true)) {
			next = words.next();
			nextLower = next.toLowerCase();
			if (stopwordRemover.process(nextLower) == null) {
				next = null;
			}
		}
		if ((nextLower != null) && (nextLower.equals(next) == true)) {
			nextLower = null;
		}
	}
	
	public boolean hasNext() {
		return (next != null);
	}

	public String next() {
		final String rVal;
		if (nextLower != null) {
			rVal = nextLower;
			nextLower = null;
		} else if (next != null) {
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

}
