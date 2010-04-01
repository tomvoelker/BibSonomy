/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.tagparser.TagString3Lexer;
import org.bibsonomy.model.util.tagparser.TagString3Parser;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class TagUtils {

	private static final Tag emptyTag = new Tag("system:unfiled");
	
	private static final Tag importedTag = new Tag("imported");
	
	/**
	 * Get the maximum user count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum user count
	 */
	public static int getMaxUserCount(List<Tag> tags) {
		int maxUserCount = 0;
		for (final Tag tag : tags) {
			if (tag.getUsercount() > maxUserCount) {
				maxUserCount = tag.getUsercount();
			}
		}
		return maxUserCount;
	}

	/**
	 * Get the maximum global count of all tags contained in a list
	 * 
	 * @param tags a list of tags
	 * @return the maximum global count
	 */
	public static int getMaxGlobalcountCount(List<Tag> tags) {
		int maxGlobalCount = 0;
		for (final Tag tag : tags) {
			if (tag.getGlobalcount() > maxGlobalCount) {
				maxGlobalCount = tag.getGlobalcount();
			}
		}
		return maxGlobalCount;
	}
	
	
	/**
	 * If a post has no tag attached, the empty tag should be added in the TAS table. 
	 * 
	 * @return The empty tag ({@value #emptyTag}).
	 */
	public static Tag getEmptyTag() {
		return emptyTag;
	}
	
	/**
	 * If the corresponding post has been imported
	 * @return The imported tag ({@value #importedTag})
	 */
	public static Tag getImportedTag() {
		return importedTag;
	}
	
	/**
	 * Converts a collection of tags into a string of tags using the given delimiter. 
	 * 
	 * @param tags 
	 * 			- a list of tags
	 * @param delim
	 * 			- a delimiter String by which the tags are to be separated 
	 * @return a delimiter-separated string of tags
	 * 			
	 */
	public static String toTagString (final Collection<Tag> tags, final String delim) {
		// check for special cases
		if (tags == null || tags.size() < 1) {
			return "";
		}
		if (delim == null) {
			throw new RuntimeException("Using NULL as delimiter is not allowed when building tag string.");
		}
		// concat tag names
		final StringBuilder sb = new StringBuilder();
		for (final Tag tag : tags) {
			sb.append(tag.getName());
			sb.append(delim);
		}
		// return string
		if (delim.length() == 0) {
			return sb.toString();
		}
		return sb.delete(sb.length() - delim.length(), sb.length()).toString();
	}
	
	
	/**
	 * Parses the incoming tag string and returns a set of tags
	 * 
	 * @param tagString
	 * @return a set of tags
	 * @throws RecognitionException
	 */
	public static Set<Tag> parse(final String tagString) throws RecognitionException {
		final Set<Tag> tags = new TreeSet<Tag>();

		if (tagString != null) {
			/*
			 * prepare parser
			 */
			final CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(new TagString3Lexer(new ANTLRStringStream(tagString)));
			final TagString3Parser parser = new TagString3Parser(tokens, tags);
			/*
			 * parse
			 */
			parser.tagstring();
		}
		return tags;
	}	
}