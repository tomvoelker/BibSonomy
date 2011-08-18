package org.bibsonomy.importer.filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Generates a proper bibtex key from the first authors last name, the year 
 * and the first relevant word of the title.
 * 
 * @author rja
 * @version $Id$
 */
public class BibTeXKeyGeneratorFilter implements PostFilterChainElement {


	private final HashSet<String> stopWords = new HashSet<String>();

	/** Looks for the property "stopWordFileName" and if it exists, loads the 
	 * specified stopwords from the file.
	 * 
	 * @param prop
	 * @throws IOException
	 */
	public BibTeXKeyGeneratorFilter(final Properties prop) throws IOException {
		/*
		 * load stopwords from a file
		 */
		final String key = BibTeXKeyGeneratorFilter.class.getName() + ".stopWordFileName";
		if (prop.containsKey(key)) {
			final String stopWordFileName = prop.getProperty(key);

			final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordFileName), "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.add(line.trim());
			}
			reader.close();
		}

	}
	public void filterPost(final Post<BibTex> post) {
		final String title = post.getResource().getTitle();
		final String firstAuthorsLastName = post.getResource().getAuthor().get(0).getLastName();
		final String year = post.getResource().getYear();
		post.getResource().setBibtexKey(firstAuthorsLastName.toLowerCase() + year + getFirstRelevantWordFromTitle(title).toLowerCase());

	}

	/** Extracts the first relevant word from the title. Relevant means, it is longer than
	 * three characters and not contained in the provided stopword list. 
	 * 
	 * @param title
	 * @return The first relevant word from the title or "" if none could be found.
	 */
	private String getFirstRelevantWordFromTitle(final String title) {
		final String[] parts = title.split("\\s");
		for (final String s: parts) {
			/*
			 * replace all non-characters
			 */
			final String word = s.replaceAll("[^\\p{L}]", "");
			if (word.length() > 3 && !stopWords.contains(word.toLowerCase())) {
				/*
				 * found an appropriate word ... 
				 */
				return word;
			}
		}
		return "";
	}

}
