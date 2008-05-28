package org.bibsonomy.importer.filter;

import java.util.Iterator;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Removes special characters from tags.
 * 
 * @author rja
 * @version $Id$
 */
public class KeywordSpecialCharacterRemovalFilter implements PostFilterChainElement {

	private String regex;
	
	/**
	 * Extracts the property 
	 * {@link org.bibsonomy.importer.filter.KeywordSpecialCharacterRemovalFilter}{@code .regex}
	 * which is used as argument to {@link String#replaceAll(regex, "")}. 
	 * 
	 * @param prop
	 */
	public KeywordSpecialCharacterRemovalFilter(final Properties prop) {
		regex = prop.getProperty(KeywordSpecialCharacterRemovalFilter.class.getName() + ".regex");
	}
	
	public void filterPost(final Post<BibTex> post) {
		final Iterator<Tag> it = post.getTags().iterator();
		
		while (it.hasNext()) {
			final Tag tag = it.next();
			/*
			 * replace special characters 
			 */
			final String newName = tag.getName().replaceAll(regex, "");
			/*
			 * check, if tag still contains some non-whitespace characters 
			 */
			if (newName.trim().length() > 0) {
				tag.setName(newName);
			} else {
				it.remove();
			}
		}
		
	}

}
