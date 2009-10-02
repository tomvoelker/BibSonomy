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

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * Provides parsing of BibTeX entries represented by {@link String}s into
 * {@link BibTex} objects.
 * 
 * FIXME: before using this in BibSonomy, it must be properly tested! Currently,
 * it puts too many fields into 'misc'.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class PostBibTeXParser extends SimpleBibTeXParser {

	/**
	 * Parses the given BibTeX entry and puts fields which are not part of the
	 * {@link BibTex} class into the Post.
	 * 
	 * @param bibtex -
	 *            the string which contains one (!) BibTeX-Entry.
	 * 
	 * @return The post which contains all data of the BibTeX-Entry.
	 */
	public Post<BibTex> parseBibTeXPost(final String bibtex) {
		/*
		 * FIXME: must be implemented! e.g., tags must be copied into post and
		 * so on ...
		 */
		return null;
	}

	/**
	 * Builds a BibTeX-String from the BibTex contained in the post and parses
	 * this string into a Post. Then, all fields in the new post which were
	 * contained in the string are copied into the new post.
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
	 */
	public void updateWithParsedBibTeX(final Post<BibTex> post) {
		/*
		 * FIXME: must be implemented!
		 * 
		 * The code below is just an example how it could work.
		 */
		final Post<BibTex> copyPost = getParsedCopy(post);
		/*
		 * all fields which toBibtexString adds must be added here!
		 */
		post.setResource(copyPost.getResource());
		post.setTags(copyPost.getTags());
		post.setDescription(copyPost.getDescription());
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
	 */
	public Post<BibTex> getParsedCopy(final Post<BibTex> post) {
		/*
		 * parseBibTeXPost must ensure to add all fields which 
		 * BibTexUtils.toBibtexString(post) puts into the string. 
		 */
		return parseBibTeXPost(BibTexUtils.toBibtexString(post));
	}

}
