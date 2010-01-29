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

package org.bibsonomy.testutil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * Methods to create objects from the model like {@link Bookmark},
 * {@link BibTex}, {@link User} or {@link Post}.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public final class ModelUtils extends CommonModelUtils {

	private static final Log log = LogFactory.getLog(ModelUtils.class);

	/**
	 * To add meaningful dates to example posts.
	 */
	private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private ModelUtils() {
	}

	private static void setResourceDefaults(final Resource resource) {
		resource.setCount(0);
	}

	/**
	 * Creates a bookmark with all properties set.
	 * @return bookmark object filled with defaults
	 */
	public static Bookmark getBookmark() {
		final Bookmark bookmark = new Bookmark();
		setResourceDefaults(bookmark);
		bookmark.setIntraHash("e44a7a8fac3a70901329214fcc1525aa");
		bookmark.setTitle("bookmarked_by_nobody");
		bookmark.setUrl("http://www.bookmarkedbynobody.com");
		return bookmark;
	}

	
	public static List<Post<Bookmark>> getBookmarks() {
		final List<Post<Bookmark>> bookmarks = new LinkedList<Post<Bookmark>>();
		bookmarks.add(generatePost(Bookmark.class));
		bookmarks.add(getBookmark1());
		bookmarks.add(getBookmark2());
		return bookmarks;
	}

	private static Date parseDate(final String date) {
		try {
			return df.parse(date);
		} catch (ParseException ex) {
			return new Date();
		}
	}
	private static Post<Bookmark> getBookmark1() {
		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("TWiki Javasxml");
		bookmark.setUrl("http://wiki.java.net/bin/view/Javawsxml/Rome05TutorialFeedWriter?TWIKISID=db92d24843fca430dbdece95a2873b7c");
		
		bookmark.recalculateHashes();
		
		final Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("rome"));
		tags.add(new Tag("rss"));
		tags.add(new Tag("atom"));
		tags.add(new Tag("java"));
		
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);
		post.setDate(parseDate("2008-12-12 12:37"));
		post.setDescription("A cool tool for creating RSS and Atom feeds");
		post.setUser(new User("rja"));

		post.setTags(tags);
		return post;
	}

	private static Post<Bookmark> getBookmark2() {
		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle("TWiki Java.net");
		bookmark.setUrl("http://wiki.java.net/");
		
		bookmark.recalculateHashes();
		
		final Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("wiki"));
		tags.add(new Tag("java"));
		tags.add(new Tag("development"));
		
		final Post<Bookmark> post = new Post<Bookmark>();
		post.setResource(bookmark);
		post.setDate(parseDate("2008-12-31 16:57"));
		post.setDescription("The Java.net Wiki");
		post.setUser(new User("rja"));

		post.setTags(tags);
		return post;
	}
	
	/**
	 * Creates a BibTex with all properties set.
	 * @return bibtex object filled with defaults
	 */
	public static BibTex getBibTex() {
		final BibTex bibtex = new BibTex();
		setBeanPropertiesOn(bibtex);
		setResourceDefaults(bibtex);		
		bibtex.setEntrytype("inproceedings");
		bibtex.setAuthor("Hans Testauthor and Liese Testauthorin");
		bibtex.setEditor("Peter Silie");
		bibtex.recalculateHashes();
		return bibtex;
	}

	/**
	 * @return user object filled with defaults
	 */
	public static User getUser() {
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName("jaeschke");
		user.setRole(Role.NOBODY);
		return user;
	}
	
	/**
	 * @return group object filled with defaults
	 */
	public static Group getGroup() {
		final Group group = new Group();
		setBeanPropertiesOn(group);
		return group;
	}

	/**
	 * @return tag object filled with defaults
	 */
	public static Tag getTag() {
		final Tag tag = new Tag();
		setBeanPropertiesOn(tag);
		tag.setSubTags(buildTagList(3, "subtag", 0));
		tag.setSuperTags(buildTagList(3, "supertag", 0));
		return tag;
	}

	/**
	 * @param <T> any resource type
	 * @param resourceType
	 * @return a post object with the given resource type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Resource> Post<T> generatePost(final Class<T> resourceType) {
		final Post<T> post = new Post<T>();

		final Group group = new Group();
		//group.setGroupId(GroupID.PUBLIC.getId()); // the group ID of posts from the "outside" is usually unknown
		group.setDescription(null);
		group.setName("public");
		post.getGroups().add(group);

		Tag tag = new Tag();
		tag.setName(ModelUtils.class.getName());
		post.getTags().add(tag);
		tag = new Tag();
		tag.setName("hurz");
		post.getTags().add(tag);

		post.setContentId(null);
		post.setDescription("trallalla");
		post.setDate(new Date());
		post.setUser(ModelUtils.getUser());
		final T resource;
		if (resourceType == BibTex.class) {
			resource = (T) ModelUtils.getBibTex();
		} else if (resourceType == Bookmark.class) {
			resource = (T) ModelUtils.getBookmark();
		} else {
			throw new UnsupportedResourceTypeException();
		}
		post.setResource(resource);

		return post;
	}

	/**
	 * Checks whether the given post has the required tags.
	 * 
	 * @param post
	 * @param requiredTags
	 * @return true if the post has the requred tags, otherwise false
	 */
	public static boolean hasTags(final Post<?> post, final Set<String> requiredTags) {
		int required = requiredTags.size();
		for (final Tag presentTag : post.getTags()) {
			if (requiredTags.contains(presentTag.getName().toLowerCase()) == true) {
				--required;
				log.debug("found " + presentTag.getName());
			}
		}
		if (required > 0) return false;
		return true;
	}

	/**
	 * Checks whether the post belongs to the given set of groups.
	 * 
	 * @param post
	 * @param mustBeInGroups
	 * @param mustNotBeInGroups
	 * @return true if the post belongs to mustBeInGroups and not mustNotBeInGroups, otherwise false
	 */
	public static boolean checkGroups(final Post<?> post, final Set<Integer> mustBeInGroups, final Set<Integer> mustNotBeInGroups) {
		int required = (mustBeInGroups != null) ? mustBeInGroups.size() : 0;
		for (final Group group : post.getGroups()) {
			if ((mustBeInGroups != null) && (mustBeInGroups.contains(group.getGroupId()) == true)) {
				--required;
				log.debug("found group " + group.getGroupId());
			}
			if ((mustNotBeInGroups != null) && (mustNotBeInGroups.contains(group.getGroupId()) == true)) {
				log.debug("found incorrect group " + group.getGroupId());
				return false;
			}
		}
		if (required > 0) {
			log.warn("not in all groups");
			return false;
		}
		return true;
	}

	/**
	 * Constructs a list of tags.
	 * 
	 * @param count
	 * @param namePrefix
	 * @param detailDepth
	 * @return list of tags
	 */
	public static List<Tag> buildTagList(final int count, final String namePrefix, final int detailDepth) {
		final List<Tag> tags = new ArrayList<Tag>(count);
		for (int i = 1; i <= count; ++i) {
			final Tag tag = new Tag();
			setBeanPropertiesOn(tag);
			tag.setName(namePrefix + i);
			tags.add(tag);
			if (detailDepth > 0) {
				tag.setSubTags(buildTagList(count, namePrefix + "-subtag", detailDepth - 1));
				tag.setSuperTags(buildTagList(count, namePrefix + "-supertag", detailDepth - 1));
			}
		}
		return tags;
	}
}