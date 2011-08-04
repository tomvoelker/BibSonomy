package org.bibsonomy.lucene.param.typehandler;

import static org.bibsonomy.lucene.util.LuceneBase.CFG_LIST_DELIMITER;

import java.util.Collection;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 * @version $Id$
 * @param <T> 
 */
public abstract class LuceneCollectionFormatter<T> extends AbstractTypeHandler<Collection<T>> {
	
	@Override
	public String getValue(final Collection<T> collection) {		
		final StringBuilder retVal = new StringBuilder("");
		for (final T item : collection) {
			retVal.append(CFG_LIST_DELIMITER).append(convertItem(item));
		}
			
		return retVal.toString().trim();
	}
	
	@Override
	public Collection<T> setValue(final String str) {
		final Collection<T> retVal = this.createCollection();
		
		final String[] tokens = str.split(CFG_LIST_DELIMITER);
		
		for (final String token : tokens) {
			retVal.add(this.createItem(token));
		}
		return retVal;
	}

	protected abstract Collection<T> createCollection();
	protected abstract T createItem(String token);
	protected abstract String convertItem(T item);
}