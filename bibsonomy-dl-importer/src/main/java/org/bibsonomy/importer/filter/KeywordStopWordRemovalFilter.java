package org.bibsonomy.importer.filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Removes stopwords from the list of tags.
 * 
 * @author rja
 * @version $Id$
 */
public class KeywordStopWordRemovalFilter implements PostFilterChainElement {

	private final HashSet<String> stopWords = new HashSet<String>();
	
	/** 
	 * Loads the file provided by the 
	 * {@link org.bibsonomy.importer.filter.KeywordStopWordRemovalFilter}{@code .stopWordFileName}
	 * property.
	 * 
	 * @param prop
	 * @throws IOException
	 */
	public KeywordStopWordRemovalFilter(final Properties prop) throws IOException {
		/*
		 * load stopwords from a file
		 */
		final String stopWordFileName = prop.getProperty(KeywordStopWordRemovalFilter.class.getName() + ".stopWordFileName");
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stopWordFileName), "UTF-8"));
		
		String line;
		while ((line = reader.readLine()) != null) {
			stopWords.add(line.trim());
		}
		reader.close();
		
	}
	
	/** Removes stopwords from the tags.
	 * 
	 * @see org.bibsonomy.importer.filter.PostFilterChainElement#filterPost(org.bibsonomy.model.Post)
	 */
	public void filterPost(final Post<BibTex> post) {
		final Iterator<Tag> it = post.getTags().iterator();
		
		while (it.hasNext()) {
			final Tag tag = it.next();
			if (stopWords.contains(tag.getName())) {
				it.remove();
			}
		}
		
	}

}
