package resources;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BibtexYearComparator implements Comparator<Bibtex>, Serializable {
	
	private static final long serialVersionUID = 4735362385246669491L;

	/** Compares two bibtex objects by the year of the publication
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare (Bibtex o1, Bibtex o2) {
		/*
		 * conmpare years
		 */
		int comp = getYear(o2.getYear()) - getYear(o1.getYear());
		/*
		 * if years are the same ...
		 */
		if (comp == 0) {
			/*
			 * compare by authors and editors;
			 */
			String p1 = o1.getFirstPersonsLastName(o1.getAuthor());
			String p2 = o1.getFirstPersonsLastName(o2.getAuthor());
			if (p1 == null) p1 = o1.getFirstPersonsLastName(o1.getEditor());
			if (p2 == null) p2 = o1.getFirstPersonsLastName(o2.getEditor());
			comp = secureCompareTo(p1, p2);
			if (comp == 0) {
				/* 
				 * both are equal ... 
				 * TODO: the current implementation is not really clean. 
				 * If author1 = author2 then comparison by editor is not done!
				 * 
				 *  if hashes are equal: compare entry types
				 *    otherwise compare title
				 */
				if (o1.getSimHash(Bibtex.SIM_HASH_1).equals(o2.getSimHash(Bibtex.SIM_HASH_1))) {
					return o1.getEntrytype().compareTo(o2.getEntrytype());
				} else {
					return o1.getTitle().compareTo(o2.getTitle());
				}
				//return -1;
			}
		}
		return comp;
	}	

	/**
	 * Tries to find a year (four connected digits) in a string and returns them as int.
	 * If it fails, returns Integer.MAX_VALUE.
	 * 
	 * @param year
	 * @return
	 */
	private int getYear (String year) {
		try {
			return Integer.parseInt(year);
		} catch (NumberFormatException e) {
			/*
			 * try to get four digits ...
			 */
			Pattern p = Pattern.compile("\\d{4}");
			Matcher m = p.matcher(year);
			if (m.find()) {
				return Integer.parseInt(m.group());
			}
		}
		return Integer.MAX_VALUE;
	}
	
	/** Compares two Strings like compareTo but with additional checks, if one of the strings is NULL.
	 * @param s1
	 * @param s2
	 * @return
	 */
	private int secureCompareTo (String s1, String s2) {
		if (s1 == null) {
			if (s2 == null) {
				/*
				 * null = s1 = s2 = null
				 */
				return 0;
			} else {
				/*
				 * null = s1 < s2 != null
				 */
				return -1;
			}
		} else {
			if (s2 == null) {
				/*
				 * null != s1 > s2 = null
				 */
				return 1;
			} else {
				/*
				 * null != s1 ? s2 != null
				 */
				return s1.compareTo(s2);
			}
		}
	}

}