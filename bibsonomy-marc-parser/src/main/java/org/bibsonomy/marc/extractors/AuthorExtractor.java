package org.bibsonomy.marc.extractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;

/**
 * extracts a BibTex author attribute out of a MarcRecord
 * 
 * @author lha
 * @version $Id$
 */
public class AuthorExtractor extends AbstractParticipantExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		ArrayList<PersonName> authors = new ArrayList<PersonName>();
		
		//get fields containing firstname information
		boolean mainAuthorFound = true;
		if (!extractAndAddAuthorPersons(authors, src, "100", (Set<String>) null)) {
			if (!extractAndAddAuthorCorporations(authors, src, "110", (Set<String>) null)) {
				if (!extractAndAddAuthorMeetings(authors, src, "111", (Set<String>) null)) {
					mainAuthorFound = false;
				}
			}
		}
		if (!extractAndAddAuthorPersons(authors, src, "700", authorRelatorCodes)) {
			if (!mainAuthorFound && !extractAndAddAuthorCorporations(authors, src, "710", authorRelatorCodes)) {
				if (!mainAuthorFound && !extractAndAddAuthorMeetings(authors, src, "711", authorRelatorCodes)) {
					mainAuthorFound = false;
				}
			}
		}
		
		
		if (authors.size() == 0) {
			// wen need an author (or we will get NPEs by various exporters such as endnote)
			authors.add(new PersonName("", "noauthor"));
		}
		target.setAuthor(authors);
		
	}
	
	
	
	
	private static final List<Set<String>> authorRelatorCodes = new ArrayList<Set<String>>();
	static {
		// freely chosen from http://www.loc.gov/marc/relators/relaterm.html
		Set<String> creatingAuthors = new HashSet<String>();
		Set<String> performingAuthors = new HashSet<String>();
		creatingAuthors.addAll(Arrays.asList("aut", "cre", "cmp", "drt"));
		performingAuthors.addAll(Arrays.asList("cnd", "prf", "cmp", "sng", "spk", "stl"));
		authorRelatorCodes.add(creatingAuthors);
		authorRelatorCodes.add(performingAuthors);
	}





}
