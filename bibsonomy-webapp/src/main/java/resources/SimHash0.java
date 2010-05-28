package resources;

@Deprecated
public class SimHash0 extends SimHash {
	
	public static String getHash (Bibtex b) {
		// calculate an appropriate hash
		return Resource.hash(removeNonNumbersOrLettersOrDotsOrSpace(b.getTitle())     + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getAuthor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEditor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getYear())      + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEntrytype()) + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getJournal())   + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getBooktitle()));
	}
}