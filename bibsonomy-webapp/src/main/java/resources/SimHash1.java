package resources;

@Deprecated
public class SimHash1 extends SimHash {
	
	public static String getHash (Bibtex b) {	
		if (removeNonNumbersOrLetters(b.getAuthor()).equals("")) {
			// no author set --> take editor
			return Resource.hash(getNormalizedTitle(b.getTitle()) + " " +
					getNormalizedEditor(b.getEditor())            + " " +
					getNormalizedYear(b.getYear()));				
		} else {
			// author set
			return Resource.hash(getNormalizedTitle(b.getTitle()) + " " + 
					getNormalizedAuthor(b.getAuthor())            + " " + 
					getNormalizedYear(b.getYear()));
		}
	}
	
	private static String getNormalizedAuthor (String a) {
		if (a != null) {
			return getStringFromList(normalizePersonList(removeNonNumbersOrLettersOrDotsOrSpace(a))).toLowerCase();
		}
		return "";
	}	
	private static String getNormalizedEditor (String e) {
		if (e != null) {
			return getStringFromList(normalizePersonList(removeNonNumbersOrLettersOrDotsOrSpace(e))).toLowerCase();
		}
		return "";
	}
	private static String getNormalizedYear (String y) {
		if (y != null) {
			return removeNonNumbers(y);
		}
		return "";
	}
	private static String getNormalizedTitle (String t) {
		if (t != null) {
			return removeNonNumbersOrLetters(t).toLowerCase();	
		}
		return "";
	}
	
}