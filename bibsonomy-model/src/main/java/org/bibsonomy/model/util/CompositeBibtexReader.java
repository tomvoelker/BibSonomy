package org.bibsonomy.model.util;

import java.util.Collection;
import java.util.Map;

import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.Data;

/**
 * @author jensi
 * @version $Id$
 */
public class CompositeBibtexReader implements BibTexReader {
	private final Map<String, BibTexReader> bibtexReadersByMimeType;
	
	/**
	 * instantiate
	 * @param bibtexReadersByMimeType
	 */
	public CompositeBibtexReader(final Map<String, BibTexReader> bibtexReadersByMimeType) {
		this.bibtexReadersByMimeType = bibtexReadersByMimeType;
	}
	
	@Override
	public Collection<ImportResource> read(ImportResource importRes) {
		
		final Data data = importRes.getData();
		String type = data.getMimeType();
		if (type == null) {
			throw new IllegalArgumentException("null mimetype");
		}
		final BibTexReader bibReader = this.bibtexReadersByMimeType.get(type);
		if (bibReader == null) {
			throw new UnsupportedOperationException("unsupported import mimetype '" + type + "'");
		}
		return bibReader.read(importRes);
	}

}
