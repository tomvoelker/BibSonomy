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


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.util.Sets;
import org.bibsonomy.wiki.tags.UserTag;


/**
 * This is a simple tagcloud-tag.
 * Usage: <tagcloud />
 *
 */
public class TagcloudTag extends UserTag {
	
	private static final String TAG_NAME = "tagcloud";
	
	private static final String TAGSTYLE = "tagstyle";
	
	private static final String TAGSTYLE_TAGCLOUD = "tagcloud";
	
	private static final String TAGSTYLE_TAGLIST = "taglist";
	
	private static final String ORDER = "order";
	
	private static final String ORDER_ALPHA = "alpha";
	
	private static final String ORDER_FREQ = "freq";
	
	private static final String MINFREQ = "minfreq";
	
	private static final String TYPE = "type";
	
	private static final String TYPE_BOTH = "both";
	
	private static final String TYPE_BOOKMARKS = "bookmarks";
	
	private static final String TYPE_PUBLICATIONS = "publications";
	
	private static final String TAGS = "tags";
	
	private static final Map<String, String> defaultOrder = new HashMap<String, String>();

	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(TAGSTYLE, ORDER, MINFREQ, TYPE, TAGS);

	static {
		defaultOrder.put(TAGSTYLE_TAGCLOUD, ORDER_ALPHA);
		defaultOrder.put(TAGSTYLE_TAGLIST, ORDER_ALPHA);
	}
	
	/*
	 * used by computeTagFontSize.
	 * 
	 * - scalingFactor: Controls difference between smallest and largest tag
	 * (size of largest: 90 -> 200% font size; 40 -> ~170%; 20 -> ~150%; all for
	 * offset = 10) - offset: controls size of smallest tag ( 10 -> 100%) -
	 * default: default tag size returned in case of an error during computation
	 */
	private static final int TAGCLOUD_SIZE_SCALING_FACTOR = 45;
	private static final int TAGCLOUD_SIZE_OFFSET = 10;
	private static final int TAGCLOUD_SIZE_DEFAULT = 100;
	
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
		 * no value for key tagstyle --> see user settings
		 * 0 = cloud, 1 = list
		 */
		if (!tagAttributes.containsKey(TAGSTYLE)) {
			int tagstyleInt = this.requestedUser.getSettings().getTagboxStyle();
			if (tagstyleInt==0){
				tagAttributes.put(TAGSTYLE, TAGSTYLE_TAGCLOUD);
			} else {
				tagAttributes.put(TAGSTYLE, TAGSTYLE_TAGLIST);
			}
		}
		
		/*
		 * no value for key order --> see user settings
		 * 0 = alph, 1 = freq
		 */
		if (!tagAttributes.containsKey(ORDER)) {
			int tagsortInt = this.requestedUser.getSettings().getTagboxSort();
			if (tagsortInt==0){
				tagAttributes.put(ORDER, ORDER_ALPHA);
			} else {
				tagAttributes.put(ORDER, ORDER_FREQ);
			}
		}
		
		/*
		 * no value for minfreq --> see user settings
		 */
		if (!tagAttributes.containsKey(MINFREQ)) {
			Integer minfreqInt = this.requestedUser.getSettings().getTagboxMinfreq();
			tagAttributes.put(MINFREQ, minfreqInt.toString());
		}
		
		/*
		 * no value for type --> both bookmarks and publications
		 */
		if (!tagAttributes.containsKey(TYPE)) {
			tagAttributes.put(TYPE, TYPE_BOTH);
		}
		
		/*
		 * no value for tags --> empty 
		 */
		if (!tagAttributes.containsKey(TAGS)) {
			tagAttributes.put(TAGS, "");
		}
		
		final String requestedName = this.requestedUser.getName();
		final List<Tag> tagsFinal = new LinkedList<Tag>();
		

		/*
		 * order the tags: alpha or frequency / bookmarks or publications or both
		 */
		String orderValue = tagAttributes.get(ORDER);
		String typeValue = tagAttributes.get(TYPE);
		int tagMax = 20000;
		List<Tag> tags = new LinkedList<Tag>();	
		
			
		if ((orderValue.equals(ORDER_ALPHA)) && (typeValue.equals(TYPE_BOOKMARKS))) {
				//Alphabet and Bookmarks
				tags = this.logic.getTags(Bookmark.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.ALPH, null, null, 0, tagMax);
		}
		else if ((orderValue.equals(ORDER_ALPHA)) && (typeValue.equals(TYPE_PUBLICATIONS))) {
				//Alphabet and Publications
				tags = this.logic.getTags(BibTex.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.ALPH, null, null, 0, tagMax);
		}
		else if ((orderValue.equals(ORDER_ALPHA)) && (typeValue.equals(TYPE_BOTH))) {
				//Alphabet and both
				tags = this.logic.getTags(Resource.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.ALPH, null, null, 0, tagMax);
			}
		else if ((orderValue.equals(ORDER_FREQ)) && (typeValue.equals(TYPE_BOOKMARKS))) {
				//Frequency and Bookmarks
				tags = this.logic.getTags(Bookmark.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.FREQUENCY, null, null, 0, tagMax);
			}
		else if ((orderValue.equals(ORDER_FREQ)) && (typeValue.equals(TYPE_PUBLICATIONS))) {
				//Frequency and Publications
				tags = this.logic.getTags(BibTex.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.FREQUENCY, null, null, 0, tagMax);
			}
		else {
				//Frequency and both
				tags = this.logic.getTags(Resource.class, GroupingEntity.USER, requestedName, null, null, null, null, null, Order.FREQUENCY, null, null, 0, tagMax);
		}
		
		int tagMinFrequency = 0;
		int tagMaxFrequency = 0;
		
		
		//if value(s) for tags are given:
		//get the tags that should be displayed
		List<Tag> tagspost = new LinkedList<Tag>();
		if (!tagAttributes.get(TAGS).equals("")) {
			if (typeValue.equals(TYPE_PUBLICATIONS)) {
				List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedName, Arrays.asList(tagAttributes.get(TAGS).split(" ")), null, null,SearchType.LOCAL, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
				if (!posts.isEmpty()) {
					tagspost = addTagsToListBibTex(posts);
				}
			}
			else if (typeValue.equals(TYPE_BOOKMARKS)) {
				List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, requestedName, Arrays.asList(tagAttributes.get(TAGS).split(" ")), null, null,SearchType.LOCAL, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
				if (!posts.isEmpty()) {
					tagspost = addTagsToListBookmarks(posts);
				}
			} else {
				List<Post<BibTex>> postsBibTex = this.logic.getPosts(BibTex.class, GroupingEntity.USER, requestedName, Arrays.asList(tagAttributes.get(TAGS).split(" ")), null, null,SearchType.LOCAL, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
				if (!postsBibTex.isEmpty()) {
					tagspost = addTagsToListBibTex(postsBibTex);
				}
				List<Post<Bookmark>> postsBookmark = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, requestedName, Arrays.asList(tagAttributes.get(TAGS).split(" ")), null, null,SearchType.LOCAL, null, null, null, null, 0, PostLogicInterface.MAX_QUERY_SIZE);
				if (!postsBookmark.isEmpty()) {
					List<Tag> tagspost2 = addTagsToListBookmarks(postsBookmark);
					tagspost.addAll(tagspost2);
				}	
			}
			
			
			//delete all tags that should not be displayed (are not in tagspost)
			List<Tag> tagsNew = new LinkedList<Tag>();
			
			if ((!tagspost.isEmpty()) && (!tags.isEmpty())) {
				for (Tag t: tags) {
					String tagName = t.getName();
					for (Tag s: tagspost) {
						if (s.getName().equals(tagName)) {
							tagsNew.add(t);
						}
					}
				}
			}
			
			//instead of Usercount: the actual amount of the tag in list tagsNew
			TreeMap<Tag, Integer> tagMap = new TreeMap<Tag, Integer>();
			if (!tagsNew.isEmpty()) {
				for (Tag t: tagsNew){
					String tagName = t.getName();
					int counter = 0;
					for (Tag s: tagsNew){
						if (s.getName().equals(tagName)){
							counter++;
						}
					}
					tagMap.put(t, counter);	
				}
			}
			
			
			//from map to list again for further work
			LinkedList<Tag> tagsNewFinal = new LinkedList<Tag>();
			if (!tagMap.isEmpty()) {
				for (Map.Entry<Tag, Integer> entry : tagMap.entrySet()) {
				    Tag key = entry.getKey();
				    int value = entry.getValue();
				    key.setUsercount(value);
				    tagsNewFinal.add(key);
				}
			}
			
			
			if (!tagsNewFinal.isEmpty()) {
				if (orderValue.equals(ORDER_ALPHA)){
					// sort alphabetically
					Collections.sort(tagsNewFinal);
				} else {
					// sort by usercount
					Collections.sort(tagsNewFinal, new Comparator<Tag>() {

				        public int compare(Tag t1, Tag t2) {
				        	Integer t1Count = new Integer(t1.getUsercount());
				        	Integer t2Count = new Integer(t2.getUsercount());
				            return t2Count.compareTo(t1Count);
				        }
				    });	
				}
				    
				
				tagMinFrequency = getMinFreqFromTaglist(tagsNewFinal);
				tagMaxFrequency = getMaxFreqFromTaglist(tagsNewFinal);
			}
				
				/*
				 * only show tags with a frequency higher than minfreq
				 */
				final String minfreqValueString = tagAttributes.get(MINFREQ);
				final int minfreqValue;
				
				minfreqValue = Integer.parseInt(minfreqValueString);
				
				if (!tagsNewFinal.isEmpty()) {
					for (Tag t:tagsNewFinal){
						if (t.getUsercount() >= minfreqValue){
							tagsFinal.add(t);
						}
					}
				}
			
		} else {
			
			if (!tags.isEmpty()) {
				tagMinFrequency = getMinFreqFromTaglist(tags);
				tagMaxFrequency = getMaxFreqFromTaglist(tags);
			}
			
			/*
			 * only show tags with a frequency higher than minfreq
			 */
			final String minfreqValueString = tagAttributes.get(MINFREQ);
			final int minfreqValue;
			
			minfreqValue = Integer.parseInt(minfreqValueString);
			
			if (!tags.isEmpty()) {
				for (Tag t:tags){
					if (t.getUsercount() >= minfreqValue){
						tagsFinal.add(t);
					}
				}
			}
		}

		
		/*
		 * tagcloud or taglist
		 */
		String tagstyle = tagAttributes.get(TAGSTYLE);
		
		if (tagstyle.equals(TAGSTYLE_TAGLIST)){
			//taglist
			renderedHTML.append("<div id='tags'>");
			renderedHTML.append("<ul class='list-group'>");
			renderedHTML.append("<li class='list-group-item'>");
			
			if (!tagsFinal.isEmpty()) {
				for (Tag t: tagsFinal){
					final String tagName = t.getName();
					final String link = "http://localhost:8080/user/" + this.requestedUser.getName() + "/" + tagName;
					final int tagCount = t.getUsercount();
					int fontSize = computeTagFontsize(tagCount, tagMinFrequency, tagMaxFrequency, "user");
					renderedHTML.append("<a href='" + link + "' title='" + tagCount + " posts' style='font-size:" + fontSize + "%' >" + tagName + " </a><br>");
				}
			}  else {
				renderedHTML.append("you have no tags in this category");
			}
			
			renderedHTML.append("</ul>");
			renderedHTML.append("</li>");
			renderedHTML.append("</div>");
			
		} else {
			//tagcloud
			renderedHTML.append("<div id='tags'>");
			renderedHTML.append("<ul class='tagcloud tagbox'>");
			
			if (!tagsFinal.isEmpty()) {
				for (Tag t: tagsFinal){
					final String tagName = t.getName();
					final String link = "http://localhost:8080/user/" + this.requestedUser.getName() + "/" + tagName;
					final int tagCount = t.getUsercount();
					String tagSize = getTagSize(tagCount, tagMaxFrequency);
					int fontSize = computeTagFontsize(tagCount, tagMinFrequency, tagMaxFrequency, "user");
					renderedHTML.append("<li class='" + tagSize + "'>");
					renderedHTML.append("<a href='" + link + "' title='" + tagCount + " posts' style='font-size:" + fontSize + "%' >" + tagName + " </a>");
					renderedHTML.append("</li>");
				}
			} else {
				renderedHTML.append("you have no tags in this category");
			}
			
			renderedHTML.append("</ul>");
			renderedHTML.append("</div>");
		}
			return renderedHTML.toString();
	}
	
	
	/**
	 * saves the tags of a list of BibTex posts into a list of tags
	 * @param posts - the list of BibTex posts
	 * @return tagspost - a list of tags
	 */
	private List<Tag> addTagsToListBibTex(List<Post<BibTex>> posts) {
		List<Tag> tagspost = new LinkedList<Tag>();
		if (!posts.isEmpty()) {
			for (Post<BibTex> p: posts) {
				Set<Tag> list = p.getTags();
				for (Tag t: list) {
					tagspost.add(t);
				}
			}
		}
		return tagspost;
	}
	
	/**
	 * saves the tags of a list of BibTex posts into a list of tags
	 * @param posts - the list of BibTex posts
	 * @return tagspost - a list of tags
	 */
	private List<Tag> addTagsToListBookmarks(List<Post<Bookmark>> posts) {
		List<Tag> tagspost = new LinkedList<Tag>();
		if (!posts.isEmpty()) {
			for (Post<Bookmark> p: posts) {
				Set<Tag> list = p.getTags();
				for (Tag t: list) {
					tagspost.add(t);
				}
			}
		}	
		return tagspost;
	}

	/**
	 * returns the css Class for a given tag
	 * 
	 * @param tagCount
	 *        the count of the current Tag
	 * @param maxTagCount
	 *        the maximum tag count
	 * @return the css class for the tag
	 */
	public static String getTagSize(final Integer tagCount, final Integer maxTagCount) {
		/*
		 * catch incorrect values
		 */
		if ((tagCount == 0) || (maxTagCount == 0)) {
			return "tagtiny";
		}

		final int percentage = ((tagCount * 100) / maxTagCount);

		if (percentage < 25) {
			return "tagtiny";
		} else if ((percentage >= 25) && (percentage < 50)) {
			return "tagnormal";
		} else if ((percentage >= 50) && (percentage < 75)) {
			return "taglarge";
		} else if (percentage >= 75) {
			return "taghuge";
		}

		return "";
	}
	
	/**
	 * Computes font size for given tag frequency and maximum tag frequency
	 * inside tag cloud.
	 * 
	 * This is used as attribute font-size=X%. We expect 0 < tagMinFrequency <=
	 * tagFrequency <= tagMaxFrequency. We return a value between 200 and 300 if
	 * tagsizemode=popular, and between 100 and 200 otherwise.
	 * 
	 * @param tagFrequency
	 *        - the frequency of the tag
	 * @param tagMinFrequency
	 *        - the minimum frequency within the tag cloud
	 * @param tagMaxFrequency
	 *        - the maximum frequency within the tag cloud
	 * @param tagSizeMode
	 *        - which kind of tag cloud is to be done (the one for the
	 *        popular tags page vs. standard)
	 * @return font size for the tag cloud with the given parameters
	 */
	public static Integer computeTagFontsize(final Integer tagFrequency, final Integer tagMinFrequency, final Integer tagMaxFrequency, final String tagSizeMode) {
		try {
			Double size = ((tagFrequency.doubleValue() - tagMinFrequency) / (tagMaxFrequency - tagMinFrequency)) * TAGCLOUD_SIZE_SCALING_FACTOR;
			if ("popular".equals(tagSizeMode)) {
				size *= 10;
			}
			size += TAGCLOUD_SIZE_OFFSET;
			size = Math.log10(size);
			size *= 100;
			return size.intValue() == 0 ? TAGCLOUD_SIZE_DEFAULT : size.intValue();
		} catch (final Exception ex) {
			return TAGCLOUD_SIZE_DEFAULT;
		}
	}
	
	/**
	 * returns the lowest frequency of the user's tags
	 * @param tags a list of a user's tags
	 * @return the frequency
	 */
	public int getMinFreqFromTaglist(List<Tag> tags){
		int minFreq = tags.get(0).getUsercount();
		for (Tag t: tags){
			if (t.getUsercount() < minFreq){
				minFreq = t.getUsercount();
			}
		}
		return minFreq;
	}
	
	/**
	 * returns the highest frequency of the user's tags
	 * @param tags a list of a user's tags
	 * @return the frequency
	 */
	public int getMaxFreqFromTaglist(List<Tag> tags){
		int maxFreq = tags.get(0).getUsercount();
		for (Tag t: tags){
			if (t.getUsercount() > maxFreq){
				maxFreq = t.getUsercount();
			}
		}
		return maxFreq;
	}
}
