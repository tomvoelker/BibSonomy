/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.wiki.tags.user;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.util.TagViewUtils;
import org.bibsonomy.wiki.tags.UserTag;


/**
 * This is a simple tagcloud-tag.
 * Usage: <tags />
 *
 */
public class TagcloudTag extends UserTag {
	
	private static final String TAG_NAME = "tags";
	
	private static final String TAGSTYLE = "style";
	
	private static final String TAGSTYLE_TAGCLOUD = "cloud";
	
	private static final String TAGSTYLE_TAGLIST = "list";
	
	private static final String ORDER = "order";
	private static final String ORDER_ALPHA = "alpha";
	private static final String ORDER_FREQ = "freq";
	
	private static final String MINFREQ = "minfreq";
	
	private static final String TYPE = "type";
	private static final String TYPE_BOOKMARKS = "bookmarks";
	private static final String TYPE_PUBLICATIONS = "publications";
	
	private static final String TAGS = "tags";

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(TAGSTYLE, ORDER, MINFREQ, TYPE, TAGS);
	
	/**
	 * set tag name
	 */
	public TagcloudTag() {
		super(TAG_NAME);
	}
	
	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
	
	@Override
	protected String renderUserTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		final Map<String, String> tagAttributes = this.getAttributes();
		
		/*
		 * no value for key order --> see user settings
		 * 0 = alph, 1 = freq
		 */
		if (!tagAttributes.containsKey(ORDER)) {
			int tagsortInt = this.requestedUser.getSettings().getTagboxSort();
			if (tagsortInt == 0){
				tagAttributes.put(ORDER, ORDER_ALPHA);
			} else {
				tagAttributes.put(ORDER, ORDER_FREQ);
			}
		}
		
		final String requestedName = this.requestedUser.getName();
		int tagMax = 20000;
		
		final Class<? extends Resource> resourceType = getResourceClass(tagAttributes.get(TYPE));
		final Order order = getOrder(tagAttributes.get(ORDER));
		final List<String> requestedTags;
		final String tagsString = tagAttributes.get(TAGS);
		if (present(tagsString)) {
			requestedTags = Arrays.asList(tagsString.split(" "));
		} else {
			requestedTags = null;
		}
		final List<Tag> tags = this.logic.getTags(resourceType, GroupingEntity.USER, requestedName, requestedTags, null, null, null, null, order, null, null, 0, tagMax);
		
		final int minfreqValue;
		if (!tagAttributes.containsKey(MINFREQ)) {
			/*
			 * no value for minfreq --> see user settings
			 */
			minfreqValue = this.requestedUser.getSettings().getTagboxMinfreq();
		} else {
			final String minfreqValueString = tagAttributes.get(MINFREQ);
			minfreqValue = Integer.parseInt(minfreqValueString);
		}
		
		final Iterator<Tag> tagIterator = tags.iterator();
		while (tagIterator.hasNext()) {
			final Tag tag = tagIterator.next();
			if (tag.getUsercount() < minfreqValue) {
				tagIterator.remove();
			}
		}
		
		renderedHTML.append("<div id='cv-tags'>");
		if (!tags.isEmpty()) {
			final int tagMinFrequency = getMinFreqFromTaglist(tags);
			final int tagMaxFrequency = getMaxFreqFromTaglist(tags);
			
			/*
			 * tagcloud or taglist
			 */
			final String tagstyle;
			if (tagAttributes.containsKey(TAGSTYLE)) {
				tagstyle = tagAttributes.get(TAGSTYLE);
			} else {
				int tagstyleInt = this.requestedUser.getSettings().getTagboxStyle();
				if (tagstyleInt == 0) {
					tagstyle = TAGSTYLE_TAGCLOUD;
				} else {
					tagstyle = TAGSTYLE_TAGLIST;
				}
			}
			
			if (tagstyle.equals(TAGSTYLE_TAGLIST)){
				// taglist
				renderedHTML.append("<ul class='list-group'>");
				
				for (final Tag tag : tags){
					renderedHTML.append("<li class='list-group-item'>");
					renderedHTML.append(this.renderSingleTag(tag, tagMinFrequency, tagMaxFrequency));
					renderedHTML.append("</li>");
				}
				
				renderedHTML.append("</ul>");
			} else {
				// tagcloud
				renderedHTML.append("<ul class='list-inline tagcloud'>");
				
				for (final Tag tag : tags){
					final String tagSize = TagViewUtils.getTagSize(Integer.valueOf(tag.getUsercount()), Integer.valueOf(tagMaxFrequency));
					renderedHTML.append("<li class='" + tagSize + "'>");
					renderedHTML.append(this.renderSingleTag(tag, tagMinFrequency, tagMaxFrequency));
					renderedHTML.append("</li>");
				}
				
				renderedHTML.append("</ul>");
			}
		}
		renderedHTML.append("</div>");
		return renderedHTML.toString();
	}
	
	
	/**
	 * @param tag
	 * @param tagMinFrequency 
	 * @param tagMaxFrequency 
	 */
	private String renderSingleTag(Tag tag, int tagMinFrequency, int tagMaxFrequency) {
		final String tagName = tag.getName();
		final String link = this.urlGenerator.getUserUrlByUserNameAndTagName(this.requestedUser.getName(), tagName);
		final int tagCount = tag.getUsercount();
		final int fontSize = TagViewUtils.computeTagFontsize(Integer.valueOf(tagCount), Integer.valueOf(tagMinFrequency), Integer.valueOf(tagMaxFrequency), "user").intValue();
		return "<a href='" + link + "' title='" + tagCount + " posts' style='font-size:" + fontSize + "%' >" + StringEscapeUtils.escapeHtml(tagName) + "</a>";
	}

	/**
	 * @param string
	 * @return
	 */
	private static Order getOrder(String string) {
		if (ORDER_FREQ.equals(string)) {
			return Order.FREQUENCY;
		}
		return Order.ALPH;
	}

	/**
	 * @param typeValue
	 * @return
	 */
	private static Class<? extends Resource> getResourceClass(String typeValue) {
		if (TYPE_BOOKMARKS.equals(typeValue)) {
			return Bookmark.class;
		}
		
		if (TYPE_PUBLICATIONS.equals(typeValue)) {
			return BibTex.class;
		}
		return Resource.class;
	}
	
	/**
	 * returns the lowest frequency of the user's tags
	 * @param tags a list of a user's tags
	 * @return the frequency
	 */
	private static int getMinFreqFromTaglist(List<Tag> tags){
		int minFreq = Integer.MAX_VALUE;
		for (final Tag tag : tags) {
			minFreq = Math.min(minFreq, tag.getUsercount());
		}
		return minFreq;
	}
	
	/**
	 * returns the highest frequency of the user's tags
	 * @param tags a list of a user's tags
	 * @return the frequency
	 */
	private static int getMaxFreqFromTaglist(List<Tag> tags){
		int maxFreq = Integer.MIN_VALUE;
		for (final Tag tag : tags) {
			maxFreq = Math.max(maxFreq, tag.getUsercount());
		}
		return maxFreq;
	}
}