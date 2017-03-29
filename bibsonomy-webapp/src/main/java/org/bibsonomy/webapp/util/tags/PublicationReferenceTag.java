/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.tags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author wla
 */
public class PublicationReferenceTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 3722057578923267058L;

	/**
	 * text
	 */
	private String text;

	/**
	 * Matcher groups: 
	 * 1: resource type (bookmark, url, bibtex, publication), optional 
	 * 2: intrahash 33 oder 32 characters, required 
	 * 3: userName, optional, not required for gold standard posts
	 */
	private static final Pattern linkPattern = Pattern.compile("\\[\\[(?:(bookmark|url|bibtex|publication)(?:\\/))?([0-9a-fA-F]{32,33})(?:\\/(.*?))?\\]\\]");
	private static final int RES_TYPE_GROUP_ID = 1;
	private static final int HASH_GROUP_ID = 2;
	private static final int USER_NAME_GROUP_ID = 3;

	private static final Set<String> BIBTEX_RESOURCE_TYPES = new HashSet<String>(Arrays.asList(new String[] { "bibtex", "publication" }));

	private static String BIBTEX = "bibtex";
	private static String BOOKMARK = "url";

	@Override
	protected int doStartTagInternal() throws Exception {
		Matcher matcher = linkPattern.matcher(text);
		String originalText = text;
		/*
		 * Required to store proceeded links for prevent double creation of html
		 * anchor. This can happen, if this discussion object has two or more
		 * links to the same post. We need to store proceeded links because we
		 * must use strig.replace method, which replaces all occurrences of the
		 * pattern. We can't use replace first, because the replacement also 
		 * contains the pattern
		 */
		final Set<String> replacedLinks = new HashSet<String>();

		boolean changed = false;
		
		while (matcher.find()) {
			if (replacedLinks.contains(matcher.group(0))) {
				continue;
			}
			changed = true;
			// FIXME: use org.bibsonomy.services.URLGenerator to build URL
			StringBuilder url = new StringBuilder("<a class=\"postlink\"href=\"/");

			url.append(getResourceString(matcher.group(RES_TYPE_GROUP_ID)));

			url.append("/");
			url.append(matcher.group(HASH_GROUP_ID));

			String userName = matcher.group(USER_NAME_GROUP_ID);
			if (present(userName)) {
				url.append("/");
				url.append(userName);
			}
			url.append("\">" + matcher.group(0) + "</a>");
			text = text.replace(matcher.group(0), url);
			replacedLinks.add(matcher.group(0));
		}
		
		if (changed) {
			text += "<div class=\"originalText\" style=\"display:none\">" + originalText + "</div>";
		}
		
		try {
			pageContext.getOut().print(text);
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}

	private String getResourceString(String resType) {
		if (!present(resType) || BIBTEX_RESOURCE_TYPES.contains(resType)) {
			return BIBTEX;
		}
		return BOOKMARK;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}
