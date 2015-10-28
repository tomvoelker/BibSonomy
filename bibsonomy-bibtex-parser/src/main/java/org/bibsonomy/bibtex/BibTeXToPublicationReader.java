package org.bibsonomy.bibtex;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.data.Data;

import bibtex.parser.ParseException;

/**
 * reader to parse publications from BibTeX
 *
 * @author dzo
 */
public class BibTeXToPublicationReader implements BibTexReader {
	private static final Log log = LogFactory.getLog(BibTeXToPublicationReader.class);
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.util.BibTexReader#read(org.bibsonomy.model.ImportResource)
	 */
	@Override
	public Collection<BibTex> read(final ImportResource importRes) {
		final Data data = importRes.getData();
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		
		
		final List<BibTex> list = new LinkedList<>();
		
		try {
			final BibTex parseBibTeX = parser.parseBibTeX(data.getReader());
			list.add(parseBibTeX);
			
			return list;
		} catch (final ParseException | IOException e) {
			log.error("error while reading BibTeX.", e);
			throw new RuntimeException(e); // TODO: maybe we want to create and throw a specific exception
		}
	}

}
