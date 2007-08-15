package webdav.helper;

import resources.Bibtex;

/**
 * Helper to ease handling of {@link resources.Bibtex}-objects.
 * 
 * @author Christian Schenk
 */
public class BibtexHelper {

	/** This is nearly a copy of {@link resources.Bibtex#entrylist} */
	private static final String[] BIBTEX_FIELDS = { "address", "annote", "authors", "bibtexAbstract",
			"bibtexKey", "bookTitle", "chapter", "crossref", "day", "description", "edition", "editors",
			"entrytype", "howPublished", "institution", "journal", "misc", "month", "note", "number",
			"organization", "pages", "publisher", "school", "series", "type", "title", "url", "volume", "year" };

	/**
	 * Builds a query-string.
	 * 
	 * @return A String to use in a query.
	 */
	public static String getBibtexWithTagName() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		for (int i = 0, n = BibtexHelper.BIBTEX_FIELDS.length; i < n; i++) {
			buffer.append("b." + BibtexHelper.BIBTEX_FIELDS[i] + ((i < n - 1) ? ", " : " "));
		}
		buffer.append("FROM tas as t,bibtex as b WHERE t.content_type=2 AND t.content_id=b.content_id AND t.tag_name=?");
		return buffer.substring(0);
	}

	/**
	 * This method was inspired from {@link servlets.BibtexHandler#getBibtex} which wasn't universally
	 * useable and refactoring it was far too dangerous...<br>
	 * Admittedly it's not much better, because the object-array needs to be in the right order or
	 * everything will be messed up, but will probably work for more use-cases.<br>
	 * If we could map the fields (in the database) to the corresponding method-names, we could use
	 * reflection to call the methods. This way we would get rid of this ugly object-array and would
	 * have a much smaller method.
	 * 
	 * @return An instance of {@link resources.Bibtex}
	 */
	public static Bibtex getBibtex(final Object[] result) {
		final Bibtex rVal = new Bibtex();
		rVal.setAddress((String) result[0]);
		rVal.setAnnote((String) result[1]);
		rVal.setAuthor((String) result[2]);
		rVal.setBibtexAbstract((String) result[3]);
		rVal.setBibtexKey((String) result[4]);
		rVal.setBooktitle((String) result[5]);
		rVal.setChapter((String) result[6]);
		rVal.setCrossref((String) result[7]);
		rVal.setDay((String) result[8]);
		rVal.setDescription((String) result[9]);
		rVal.setEdition((String) result[10]);
		rVal.setEditor((String) result[11]);
		rVal.setEntrytype((String) result[12]);
		rVal.setHowpublished((String) result[13]);
		rVal.setInstitution((String) result[14]);
		rVal.setJournal((String) result[15]);
		rVal.setMisc((String) result[16]);
		rVal.setMonth((String) result[17]);
		rVal.setNote((String) result[18]);
		rVal.setNumber((String) result[19]);
		rVal.setOrganization((String) result[20]);
		rVal.setPages((String) result[21]);
		rVal.setPublisher((String) result[22]);
		rVal.setSchool((String) result[23]);
		rVal.setSeries((String) result[24]);
		rVal.setTitle((String) result[25]);
		rVal.setType((String) result[26]);
		rVal.setUrl((String) result[27]);
		rVal.setVolume((String) result[28]);
		rVal.setYear((String) result[29]);
		return rVal;
	}
}