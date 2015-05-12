/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import info.bliki.htmlcleaner.Utils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.wiki.tags.SharedTag;
import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple tagcloud-tag.
 * Usage: <tagcloud />
 *
 */
public class TagcloudTag extends UserTag {
	
	private static final String TAG_NAME = "tags";
	
	private static final String TAGSTYLE = "tagstyle";
	
	private static final String TAGSTYLE_TAGCLOUD = "tagcloud";
	
	private static final String TAGSTYLE_TAGLIST = "taglist";
	
	private static final String ORDER = "order";
	
	private static final String ORDER_ALPHA = "alpha";
	
	private static final String ORDER_FREQ = "freq";
	
	private static final String MINFREQ = "minfreq";
	
	private static final Map<String, String> defaultOrder = new HashMap<String, String>();

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(TAGSTYLE, ORDER, MINFREQ);

	static {
		defaultOrder.put(TAGSTYLE_TAGCLOUD, ORDER_ALPHA);
		defaultOrder.put(TAGSTYLE_TAGLIST, ORDER_ALPHA);
	}
	
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
		
		final Map<String, String> tagAttributes = this.getAttributes();
		
		final String requestedName = this.requestedUser.getName();
		Order tagOrder = Order.FREQUENCY;
		int tagMax = 20000;
		//Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, String regex, TagSimilarity relation, Order order, Date startDate, Date endDate, int start, int end
		//resourceType, groupingEntity, groupingName, tags, hash, search, regex, null, tagOrder, cmd.getStartDate(), cmd.getEndDate(), 0, tagMax)
		final List<Tag> tags = this.logic.getTags(Resource.class, GroupingEntity.USER, requestedName, null, null, null, null, null, tagOrder, null, null, 0, tagMax);
		
		
		//final List<Tag> tag = this.requestedUser.getTags();
		final List<String> tagsToString = new LinkedList<String>();
		for (Tag t: tags){
			final String tagName = t.getName();
			tagsToString.add(tagName);
		}
		
		/*
		 * order the tags, alpha or frequency
		 */
		final String orderValue = tagAttributes.get(ORDER);
	
			if (orderValue==ORDER_ALPHA){
				//nach Alphabet sortieren
				Collections.sort(tagsToString);
			}
			else {
				//nach Frequency sortieren
			}
		
		
		return ValidationUtils.present(tagsToString) ? "<div id='tags'>" + tagsToString + "</div>" : "";
	}

}
