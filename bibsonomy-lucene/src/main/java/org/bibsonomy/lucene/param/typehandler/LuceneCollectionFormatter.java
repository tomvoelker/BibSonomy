package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public abstract class LuceneCollectionFormatter extends AbstractTypeHandler {
	private static final String CFG_LIST_DELIMITER = " ";
	
	@Override
	public String getValue(Object obj) {
		Collection<?> collection = (Collection<?>)obj;
		
		String retVal = "";
		for( Object item : collection ) {
			retVal += CFG_LIST_DELIMITER + convertItem(item);
		}
			
		return retVal.trim();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object setValue(String str) {
		Collection<Object> retVal = (Collection<Object>) createCollection();
		
		String[] tokens = str.split(CFG_LIST_DELIMITER);
		
		for( String token : tokens )
			retVal.add(createItem(token));
		
		return retVal;
	}


	protected abstract Collection<? extends Object> createCollection();
	protected abstract Object createItem(String token);
	protected abstract String convertItem(Object item);
}