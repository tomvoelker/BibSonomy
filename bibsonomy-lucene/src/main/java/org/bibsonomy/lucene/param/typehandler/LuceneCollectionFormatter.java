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
	public String getValue(Collection<T> collection) {		
		StringBuilder retVal = new StringBuilder("");
		for (T item : collection) {
			retVal.append(CFG_LIST_DELIMITER).append(convertItem(item));
		}
			
		return retVal.toString().trim();
	}
	
	@Override
	public Collection<T> setValue(String str) {
		Collection<T> retVal = this.createCollection();
		
		String[] tokens = str.split(CFG_LIST_DELIMITER);
		
		for( String token : tokens )
			retVal.add(createItem(token));
		
		return retVal;
	}

	protected abstract Collection<T> createCollection();
	protected abstract T createItem(String token);
	protected abstract String convertItem(T item);
}