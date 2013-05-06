package org.bibsonomy.marc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bibsonomy.marc.extractors.AddressExtractor;
import org.bibsonomy.marc.extractors.AuthorExtractor;
import org.bibsonomy.marc.extractors.BibTeXKeyExtractor;
import org.bibsonomy.marc.extractors.EditionExtractor;
import org.bibsonomy.marc.extractors.JournalExtractor;
import org.bibsonomy.marc.extractors.PagesExtractor;
import org.bibsonomy.marc.extractors.PublicationExtractor;
import org.bibsonomy.marc.extractors.TitleExtractor;
import org.bibsonomy.marc.extractors.YearExtractor;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;
import org.bibsonomy.model.util.data.Data;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.marc.Record;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReader implements BibTexReader {

	private List<AttributeExtractor> extractors;

	public MarcToBibTexReader() {
		extractors = new ArrayList<AttributeExtractor>();
		extractors.add(new TitleExtractor());
		extractors.add(new AuthorExtractor());
		extractors.add(new JournalExtractor());
		extractors.add(new EditionExtractor());
		extractors.add(new AddressExtractor());
		extractors.add(new PagesExtractor());
		extractors.add(new YearExtractor());
		extractors.add(new PublicationExtractor());
		
		//must be the last element in chain because the previous entries must be set
		extractors.add(new BibTeXKeyExtractor());
	}
	
	@Override
	public Collection<ImportResource> read(Data data) {
		List<ImportResource> rVal = new ArrayList<ImportResource>();
		MarcReader reader = new MarcStreamReader(data.getInputStream(), "UTF-8");
		while (reader.hasNext()) {
			Record r = reader.next();
			ExtendedMarcRecord er = new ExtendedMarcRecord(r);
			ImportResource b = new ImportResource();
			for (AttributeExtractor ex : extractors) {
				ex.extraxtAndSetAttribute(b, er);
			}
			rVal.add(b);

			//System.out.println(r.toString());
		}
		return rVal;
	}

}
