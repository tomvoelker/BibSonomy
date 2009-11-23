/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.bibtex.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexString;
import bibtex.parser.ParseException;

/**
 * Provides parsing of BibTeX entries represented by {@link String}s into
 * {@link Post} objects.
 *
 * This class is thread-safe.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class PostBibTeXParser extends SimpleBibTeXParser {

	/**
	 * Parses the given BibTeX entry and puts fields which are not part of the
	 * {@link BibTex} class into the Post. See {@link #fillPost(BibTex)} for 
	 * details.
	 * 
	 * @param bibtex -
	 *            the string which contains one (!) BibTeX-Entry.
	 * 
	 * @return The post which contains all data of the BibTeX-Entry.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Post<BibTex> parseBibTeXPost(final String bibtex) throws ParseException, IOException {
		/*
		 * call parseBibTeX
		 */
		final BibTex parsedBibTeX = parseBibTeX(bibtex);
		/*
		 * create post and put resource into post
		 */
		return fillPost(parsedBibTeX);
	}
	
	/**
	 * Puts the resource into a post. Does the following additional steps:
	 * 
	 *  <ul>
	 *  <li>Sets description from misc field-</li>
	 *  <li>Parses and sets tags from misc field-</li>
	 *  <li>Removes additional misc fields intrahash, interhash, and {@link BibTexUtils#ADDITIONAL_MISC_FIELDS}.</li>
	 *  <li>Re-Serializes misc fields.
	 *  </ul>
	 * 
	 * @param bibtex
	 * @return
	 */
	private Post<BibTex> fillPost(final BibTex bibtex) {
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(bibtex);
		/*
		 * get misc fields for next steps
		 */
		final HashMap<String, String> miscFields = bibtex.getMiscFields();
		/*
		 * put description from misc fields into post
		 */
		post.setDescription(miscFields.remove(BibTexUtils.ADDITIONAL_MISC_FIELD_DESCRIPTION));
		/*
		 * parse tags
		 */
		final String keywords = miscFields.remove(BibTexUtils.ADDITIONAL_MISC_FIELD_KEYWORDS);
		try {
			post.setTags(TagUtils.parse(keywords));
		} catch (RecognitionException ex) {
			/*
			 * silently ignore tag parsing errors ....
			 */
		}
		/*
		 * remove other misc fields which should not be stored as misc field 
		 * (but rather as regular field/column)
		 */
		miscFields.remove("intrahash");
		miscFields.remove("interhash");
		for (final String additionalMiscField: BibTexUtils.ADDITIONAL_MISC_FIELDS) {
			miscFields.remove(additionalMiscField);	
		}
		/*
		 * re-write misc field to fix above changes
		 */
		BibTexUtils.serializeMiscFields(bibtex);
		return post;
	}

	/**
	 * Like {@link #parseBibTeXPost(String)} but for multiple entries.
	 * 
	 * @param bibtex - a BibTeX string containing one or more BibTeX entries.
	 * @return A list of posts containing the parsed BibTeX entries as resources.
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<Post<BibTex>> parseBibTeXPosts(final String bibtex) throws ParseException, IOException {
		/*
		 * parse entries
		 */
		final List<BibTex> parsedBibTeXs = parseBibTeXs(bibtex);
		/*
		 * contains resulting posts
		 */
		final List<Post<BibTex>> result = new LinkedList<Post<BibTex>>();
		/*
		 * create the posts
		 */
		for (final BibTex bibTex2: parsedBibTeXs) {
			result.add(fillPost(bibTex2));
		}
		
		return result;
	}
	
	/** 
	 * In addition to org.bibsonomy.bibtex.parser.SimpleBibTeXParser#fillBibtexFromEntry(bibtex.dom.BibtexEntry)
	 * this method handles description, keywords, etc. which are not part of 
	 * {@link BibTex} but of {@link Post}.
	 * 
	 * All additional fields are added as "misc" field to the resulting bibtex.
	 * 
	 * 
	 * @see org.bibsonomy.bibtex.parser.SimpleBibTeXParser#fillBibtexFromEntry(bibtex.dom.BibtexEntry)
	 */
	@Override
	protected BibTex fillBibtexFromEntry(final BibtexEntry entry) {
		final BibTex bibtex = super.fillBibtexFromEntry(entry);
		
		for (final String additionalField: BibTexUtils.ADDITIONAL_MISC_FIELDS) {
			final BibtexString field = (BibtexString) entry.getFieldValue(additionalField); 
			if (field != null) bibtex.addMiscField(additionalField, field.getContent());
			
		}
		
		return bibtex;
	}
	
	/**
	 * Builds a BibTeX-String from the BibTex contained in the post and parses
	 * this string into a Post. Then, all fields in the new post which were
	 * contained in the string are copied back into the new post.
	 * 
	 * Purpose: To ensure that we show only valid and normalized BibTeX entries
	 * (e.g., on /bib/ pages; currently, we also need this for /layout/, since
	 * there all posts are parsed through the JabRef parser) we send all posts
	 * through the parser and thereby normalize them before we store them in the
	 * DB.
	 * 
	 * @see BibTexUtils#toBibtexString(Post) - all fields added there have to be
	 * copied here, to!
	 * @param post
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void updateWithParsedBibTeX(final Post<BibTex> post) throws ParseException, IOException {
		/*
		 * parse the bibtex in the post
		 */
		final Post<BibTex> copyPost = getParsedCopy(post);
		/*
		 * exchange the bibtex in the post by the parsed version
		 */
		post.setResource(copyPost.getResource());
		/*
		 * We don't need to copy those fields back, because they're not touched/
		 * normalized by the parser.
		 */
//		post.setTags(copyPost.getTags());
//		post.setDescription(copyPost.getDescription());
	}

	/**
	 * Parses the given post and returns a copy where all fields which are put
	 * into a BibTeX string in {@link BibTexUtils#toBibtexString(Post)} are put
	 * into the copy. Please note that ONLY THOSE fields are put into the copy
	 * post! I.e., fields like "group" or "user", which never occur in a BibTeX 
	 * string are not copied into the new post!
	 * 
	 * @param post
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Post<BibTex> getParsedCopy(final Post<BibTex> post) throws ParseException, IOException {
		/*
		 * parseBibTeXPost must ensure to add all fields which 
		 * BibTexUtils.toBibtexString(post) puts into the string. 
		 */
		return parseBibTeXPost(BibTexUtils.toBibtexString(post));
	}

}
