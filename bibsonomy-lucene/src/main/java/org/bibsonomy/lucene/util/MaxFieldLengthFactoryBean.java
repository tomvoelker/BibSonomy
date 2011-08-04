package org.bibsonomy.lucene.util;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class MaxFieldLengthFactoryBean implements FactoryBean<IndexWriter.MaxFieldLength> {
	/** keyword identifying unlimited field length in the lucene index */
	private static final String KEY_UNLIMITED = "UNLIMITED";
	
	/** keyword identifying limited field length in the lucene index */
	private static final Object KEY_LIMITED = "LIMITED";
	
	private String maxFieldLength;

	@Override
	public MaxFieldLength getObject() throws Exception {
		if (KEY_UNLIMITED.equals(this.maxFieldLength)) {
			return IndexWriter.MaxFieldLength.UNLIMITED;
		}
		
		if (KEY_LIMITED.equals(this.maxFieldLength)) {
			return IndexWriter.MaxFieldLength.LIMITED;
		}
		
		try {
			return new IndexWriter.MaxFieldLength(Integer.parseInt(this.maxFieldLength));
		} catch (final NumberFormatException e) {
			return IndexWriter.MaxFieldLength.LIMITED;
		}		
	}

	@Override
	public Class<?> getObjectType() {
		return IndexWriter.MaxFieldLength.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @param maxFieldLength the maxFieldLength to set
	 */
	public void setMaxFieldLength(final String maxFieldLength) {
		this.maxFieldLength = maxFieldLength;
	}
}
