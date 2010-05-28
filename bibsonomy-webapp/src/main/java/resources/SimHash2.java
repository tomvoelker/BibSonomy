package resources;

/**
 * NEW intra user hash
 *
 */
@Deprecated
public class SimHash2 extends SimHash {
	
	public static String getHash (Bibtex b) {
		// calculate an appropriate hash
		return Resource.hash(removeNonNumbersOrLettersOrDotsOrSpace(b.getTitle())     + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getAuthor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEditor())    + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getYear())      + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getEntrytype()) + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getJournal())   + " " + 
				removeNonNumbersOrLettersOrDotsOrSpace(b.getBooktitle()) + " " +
				removeNonNumbersOrLetters(b.getVolume())                 + " " +
				removeNonNumbersOrLetters(b.getNumber())
		);
	}
}

/*

notwendige Anpassungen:

 - entsprechende zusätzliche Spalten (Volume/Number) im ResourceHandler mit herausgeben
   (in getBibtexSelect() columns[] anpassen)
 - Bibtex.java anpassen: getSimHash(), setHashesToNull(), später getHash()
 - writing of hashes in insertBibIntoDB() in BibtexHandler is already done for 0 to 4
 - update queries in ResourceHandler to use correct hashes for query
 - change urlrewriteFilter! (not neccessary?!)
 - change in ResourceHandler: if (requPage.equals(PAGE_USERBIBTEX)) {
 
 - veeery important: create correct indexes in bibtex table(s)!
 
 changing in Bibtex.getHash() from 0 to 2 should imply changing column in resource handler and 
 pre-0 to pre-2 in JSPs?!
 
 have a look at SIM_HASH and INTRA_HASH - where are they used and should they be changed?
  

*/