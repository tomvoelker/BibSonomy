package org.bibsonomy.marc.extractors;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;

/**
 * @author jensi
 * @version $Id$
 */
public class CompositeAttributeExtractor implements AttributeExtractor {
	
	private static final List<AttributeExtractor> extractors;
	static {
		extractors = new ArrayList<AttributeExtractor>();
		extractors.add(new TypeExtractor());
		extractors.add(new AbstractExtractor());
		extractors.add(new AuthorExtractor());
		extractors.add(new TitleExtractor());
		extractors.add(new EditorExtractor());
		extractors.add(new JournalExtractor());
		extractors.add(new EditionExtractor());
		extractors.add(new AddressExtractor());
		extractors.add(new PagesExtractor());
		extractors.add(new YearExtractor());
		extractors.add(new PublisherExtractor());
		extractors.add(new HebisIdExtractor());
		extractors.add(new VolumeExtractor());
		extractors.add(new SeriesExtractor());
		extractors.add(new ISBNExtractor());
		extractors.add(new URLExtractor());
		extractors.add(new OrganizationExtractor());
		extractors.add(new DayExtractor());
		extractors.add(new MonthExtractor());
		extractors.add(new NumberExtractor());
		//must be placed in the chain after TypeExtractor
		extractors.add(new NoteExtractor());
		extractors.add(new EmergencyAuthorExtractor());
		extractors.add(new EmergencyRepairingExtractor());
	}
	
	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		for (AttributeExtractor ex : extractors) {
			try {
				ex.extraxtAndSetAttribute(target, src);
			} catch (IllegalArgumentException e) {
				//System.err.println(e.toString());
			}
		}
	}

}
