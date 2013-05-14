package org.bibsonomy.marc.extractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;

/**
 * extracts a BibTex author attribute out of a MarcRecord
 * 
 * @author lha
 * @version $Id$
 */
public class AuthorExtractor implements AttributeExtractor {

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
		
		//set the resulting BibTex information
		target.setAuthor(authors);
		
	}
	
	private boolean extractAndAddAuthorPersons(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, List<Set<String>> authorRelatorCodes) {
		for (Set<String> relcodes : authorRelatorCodes) {
			if (extractAndAddAuthorPersons(authors, src, fieldName, relcodes)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean extractAndAddAuthorCorporations(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, List<Set<String>> authorRelatorCodes) {
		for (Set<String> relcodes : authorRelatorCodes) {
			if (extractAndAddAuthorCorporations(authors, src, fieldName, relcodes)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean extractAndAddAuthorMeetings(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, List<Set<String>> authorRelatorCodes) {
		for (Set<String> relcodes : authorRelatorCodes) {
			if (extractAndAddAuthorMeetings(authors, src, fieldName, relcodes)) {
				return true;
			}
		}
		return false;
	}

	private boolean extractAndAddAuthorPersons(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, Set<String> limitationsToAllowedRelatorCodes) {
		boolean found = false;
		for (DataField df : src.getDataFields(fieldName)) {
			if (limitationsToAllowedRelatorCodes != null && !isMarkedAuthor(src, fieldName, limitationsToAllowedRelatorCodes)) {
				continue;
			}
			final String primaryName = getSubfieldData(df, 'a');
			if (primaryName == null) {
				continue;
			}
			final String primaryNumeration = getSubfieldData(df, 'b');
			final String primaryTitle = getSubfieldData(df, 'c');
			authors.add(getName(primaryName, primaryNumeration, primaryTitle));
			found = true;
		}
		return found;
	}
	
	private String getSubfieldData(DataField df, char c) {
		Subfield sf = df.getSubfield(c);
		if (sf == null) {
			return null;
		}
		return sf.getData();
	}
	
	private boolean extractAndAddAuthorCorporations(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, Set<String> limitationsToAllowedRelatorCodes) {
		boolean found = false;
		for (DataField df : src.getDataFields(fieldName)) {
			if (limitationsToAllowedRelatorCodes != null && !isMarkedAuthor(src, fieldName, limitationsToAllowedRelatorCodes)) {
				continue;
			}
			final String name = getSubfieldData(df, 'a');
			if (name == null) {
				continue;
			}
			StringBuilder lastName = new StringBuilder(name.trim());
			final String subUnit = getSubfieldData(df, 'b');
			if (subUnit != null) {
				lastName.append(" - ").append(subUnit);
			}
			found |= setAsPerson(authors, lastName);
		}
		return found;
	}
	
	private boolean extractAndAddAuthorMeetings(List<PersonName> authors, ExtendedMarcRecord src, String fieldName, Set<String> limitationsToAllowedRelatorCodes) {
		boolean found = false;
		for (DataField df : src.getDataFields(fieldName)) {
			if (limitationsToAllowedRelatorCodes != null && !isMarkedAuthor(src, fieldName, limitationsToAllowedRelatorCodes)) {
				continue;
			}			
			final String name = getSubfieldData(df, 'a');
			if (name == null) {
				continue;
			}
			final StringBuilder lastName = new StringBuilder(name.trim());
			final String date = src.getFirstFieldValue(fieldName, 'd');
			if (date != null) {
				date.replace("(", "").replace(")", "");
			}
			if (ValidationUtils.present(date)) {
				lastName.append(" ").append(date);
			}
			found |= setAsPerson(authors, lastName);
		}
		return found;
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

	private boolean isMarkedAuthor(ExtendedMarcRecord src, String fieldName, Set<String> authorRelatorCodes) {
		final String relatorCode = src.getFirstFieldValue("700", '4');
		if (!ValidationUtils.present(relatorCode)) {
			return false;
		}
		return authorRelatorCodes.contains(relatorCode.trim().toLowerCase());
	}

	public boolean setAsPerson(List<PersonName> authors, StringBuilder lastName) {
		StringUtils.trimStringBuffer(lastName);
		if (lastName.length() > 0) {
			authors.add(new PersonName("", lastName.toString()));
			return true;
		}
		return false;
	}

	/**
	 * creates a name out of given information
	 * 
	 * @param name the name to set, format: lastname, firstname
	 * @param numeration 
	 * @param title
	 * @return the created name object
	 */
	private PersonName getName(String name, String numeration, String title) {
		
		PersonName result = new PersonName();
		//build lastName
		StringBuilder lastName = new StringBuilder();
		if (name.contains(", ")) {
			// forename is available -> get foreName and lastname
			result.setFirstName(name.split(", ")[1]);
			lastName.append(name.split(", ")[0].replace(",", ""));
			
		} else {
			//maybe only lastname is given -> only lastname
			lastName.append(name.replace(",", ""));
		}
		if (ValidationUtils.present(numeration)) {
			lastName.append(' ').append(numeration.trim());
		}
		if (ValidationUtils.present(title)) {
			lastName.append(" <").append(title.trim()).append('>');
		}
		result.setLastName(lastName.toString());
		return result;
	}

}
