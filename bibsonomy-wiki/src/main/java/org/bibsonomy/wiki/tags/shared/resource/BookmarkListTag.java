/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.wiki.tags.shared.resource;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.util.Sets;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * TODO: abstract resource tag
 * 
 * FIXME: escape all data coming from the database!
 * 
 * @author philipp
 * @author Bernd Terbrack
 */
public class BookmarkListTag extends SharedTag {
	private static final String REQUESTED_TAGS = "tags";
	private static final String LIMIT = "limit";
	private static final String TAG_NAME = "bookmarks";

	private static final Set<String> ALLOWED_ATTRIBUTES = Sets.asSet(REQUESTED_TAGS, LIMIT);

	private int maxQuerySize;
	
	/**
	 * sets the tag
	 * @param maxQuerySize
	 */
	public BookmarkListTag(int maxQuerySize) {
		super(TAG_NAME);
		this.maxQuerySize = maxQuerySize;
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES.contains(attName);
	}

	@Override
	protected String renderSharedTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAttributes = this.getAttributes();
		final Set<String> keysSet = tagAttributes.keySet();

 		final String tags;
 		if (!keysSet.contains(REQUESTED_TAGS)) {
 			tags = "myown"; // TODO: should be MyOwnSystemTag.NAME but adding
 							// dependency to database module only for accessing
 							// the constant?! => Should definitely be MyOwnSystemTag.NAME and the systemTag should be moved to the model
 		} else {
 			tags = tagAttributes.get(REQUESTED_TAGS);
 		}
 		
 		// TODO: Remove duplicates, if rendered for group
 		List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, this.getGroupingEntity(), this.getRequestedName(), Arrays.asList(tags.split(" ")), null, null, SearchType.LOCAL, null, null, null, null, 0, this.maxQuerySize);

 		if (tagAttributes.get(LIMIT) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAttributes.get(LIMIT)));
			} catch (final Exception e) {
				// Do nothing
			}
		}

		renderedHTML.append("<div id='bookmarks'>");
		renderedHTML.append("<ul id='bookmarklist' class='bookmarkList'>");

		for (final Post<Bookmark> post : posts) {
			renderedHTML.append("<div class='entry'><li><span class='entry_title'>");
			renderedHTML.append("<a href='");
			renderedHTML.append(StringEscapeUtils.escapeXml(post.getResource().getUrl()));
			renderedHTML.append("' rel='nofollow'>");
			renderedHTML.append(StringEscapeUtils.escapeHtml(post.getResource().getTitle()));
			renderedHTML.append("</a>");
			renderedHTML.append("</span>");

			final String description = post.getDescription();
			if (present(description)) {
				renderedHTML.append("<a class='cv-bookmark-details' onclick='return toggleDetails(this)'>");
				renderedHTML.append(this.messageSource.getMessage("cv.options.show_details", new Object[] { this.getName() }, this.locale));
				renderedHTML.append("</a>");
				renderedHTML.append("<p class='details'>" + description + "</p>");
			}
			renderedHTML.append("</li></div>");
		}

		renderedHTML.append("</ul>");
		renderedHTML.append("</div>");
		return renderedHTML.toString();
	}
}
