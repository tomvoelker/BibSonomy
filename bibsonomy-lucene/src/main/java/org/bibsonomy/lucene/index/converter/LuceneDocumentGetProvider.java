package org.bibsonomy.lucene.index.converter;

import org.apache.lucene.document.Document;
import org.bibsonomy.util.GetProvider;

/**
 * Adapter to access a Lucene-{@link Document} via the {@link GetProvider} interface
 *
 * @author jensi
 */
public class LuceneDocumentGetProvider implements GetProvider<String, Object> {
	private final Document doc;
	
	/**
	 * @param doc the {@link Document} to wrap
	 */
	public LuceneDocumentGetProvider(final Document doc) {
		this.doc = doc;
	}
	
	@Override
	public Object get(String arg) {
		return doc.get(arg);
	}

}
