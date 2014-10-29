package org.bibsonomy.wiki.tags.shared.resource;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	/**
	 * sets the tag
	 */
	public BookmarkListTag() {
		super(TAG_NAME);
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
		List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, this.getGroupingEntity(), this.getRequestedName(), Arrays.asList(tags.split(" ")), null, null, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
		
		if (tagAttributes.get(LIMIT) != null) {
			try {
				posts = posts.subList(0, Integer.parseInt(tagAttributes.get(LIMIT)));
			} catch (final Exception e) {
				// Do nothing
			}
		}

		renderedHTML.append("<div class='align' id='bookmarks'>");
		renderedHTML.append("<ul id='bookmarklist' class='bookmarkList'>");

		for (final Post<Bookmark> post : posts) {
			renderedHTML.append("<div style='margin:1.2em;' class='entry'><li><span class='entry_title'>");
			renderedHTML.append("<a href='" + post.getResource().getUrl()
					+ "' rel='nofollow'>" + post.getResource().getTitle()
					+ "</a>");
			renderedHTML.append("</span>");

			final String description = post.getDescription();
			if (present(description)) {
				// TODO: i18n [show details]
				renderedHTML.append(" <a class='hand' onclick='return toggleDetails(this)' >"
								+ this.messageSource.getMessage(
										"cv.options.show_details",
										new Object[] { this.getName() },
										this.locale) + " </a>");
				renderedHTML.append("<p class='details'>" + description + "</p>");
			}
			renderedHTML.append("</li></div>");
		}

		renderedHTML.append("</ul>");
		renderedHTML.append("</div >");
		return renderedHTML.toString();
	}
}
