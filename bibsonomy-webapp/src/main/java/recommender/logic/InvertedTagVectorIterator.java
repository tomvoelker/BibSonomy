package recommender.logic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import recommender.model.InvertedTagVector;
import recommender.model.TagVector;

/**
 * wandelt einen nach key sortierten TagVectorEntryIterator-Stream in einen
 * InvertedTagVectorIterator-Stream
 * 
 * @author Jens Illig
 */
public class InvertedTagVectorIterator implements Iterator<InvertedTagVector>, Iterable<InvertedTagVector> {
	private static final Logger log = Logger.getLogger(InvertedTagVectorIterator.class);
	private Iterator<TagVector.Entry> it;
	private InvertedTagVector.Factory factory;
	private TagVector.Entry nextEntry;
	
	public InvertedTagVectorIterator(Iterator<TagVector.Entry> it, InvertedTagVector.Factory factory) {
		this.it =it;
		this.factory = factory;
		if (it.hasNext()) {
			nextEntry = it.next();
		} else {
			nextEntry = null;
		}
	}

	public boolean hasNext() {
		return (nextEntry != null);
	}

	public InvertedTagVector next() {
		if (nextEntry == null) {
			throw new NoSuchElementException();
		}
		InvertedTagVector rVal = factory.produce(nextEntry.getKey());
		rVal.setNewTagId(nextEntry.getTagId(), nextEntry.getValue());
		while (it.hasNext()) {
			nextEntry = it.next();
			if (nextEntry.getKey().equals(rVal.getKey())) {
				rVal.setNewTagId(nextEntry.getTagId(), nextEntry.getValue());
			} else {
				return rVal;
			}
		}
		nextEntry = null;
		return rVal;
	}

	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
	public static InvertedTagVector buildVectorFromEntries(Iterator<TagVector.Entry> it, InvertedTagVector.Factory factory) {
		InvertedTagVectorIterator tvit = new InvertedTagVectorIterator(it,factory);
		if (tvit.hasNext()) {
			InvertedTagVector tv = tvit.next();
			if (tvit.hasNext()) {
				log.warn("multiple TagVectors in TagVector.Entry-Stream but only one requested");
			}
			return tv;
		} 
		return null;
	}

	public Iterator<InvertedTagVector> iterator() {
		return this;
	}
	
}
