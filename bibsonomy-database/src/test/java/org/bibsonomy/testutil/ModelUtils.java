package org.bibsonomy.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.DepthEqualityTester.EqualityChecker;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Methods to create objects from the model like {@link Bookmark},
 * {@link BibTex}, {@link User} or {@link Post}.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public final class ModelUtils {

	private static final Logger log = Logger.getLogger(ModelUtils.class);

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
	 * Calls every setter on an object and fills it wiht dummy values.
	 */
	private static void setBeanPropertiesOn(final Object obj) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				try {
					final Method setter = d.getWriteMethod();
					final Method getter = d.getReadMethod();
					if ((setter != null) && (getter != null)) {
						setter.invoke(obj, new Object[] { getDummyValue(d.getPropertyType(), d.getName()) });
					}
				} catch (final Exception ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + obj.getClass().getName() + "'");
		}
	}

	/**
	 * Returns dummy values for some primitive types and classes
	 */
	private static Object getDummyValue(final Class<?> type, final String name) {
		if (String.class == type) {
			return "test-" + name;
		}
		if ((int.class == type) || (Integer.class == type)) {
			return Math.abs(name.hashCode());
		}
		if ((boolean.class == type) || (Boolean.class == type)) {
			return (name.hashCode() % 2 == 0);
		}
		if (URL.class == type) {
			try {
				return new URL("http://www.bibsonomy.org/test/" + name);
			} catch (final MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		}
		if (Privlevel.class == type) {
			return Privlevel.MEMBERS;
		}
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}

	/**
	 * Checks whether every property of two objects (should and is) match.
	 * 
	 * @param should
	 * @param is
	 * @param maxDepth
	 * @param excludePropertiesPattern
	 * @param excludeProperties
	 */
	public static void assertPropertyEquality(final Object should, final Object is, final int maxDepth, final Pattern excludePropertiesPattern, final String... excludeProperties) {
		final EqualityChecker checker = new EqualityChecker() {

			public boolean checkEquals(Object should, Object is, String path) {
				assertEquals(path, should, is);
				return true;
			}

			public boolean checkTrue(boolean value, String path, String checkName) {
				assertTrue(path + " " + checkName, value);
				return true;
			}

		};
		DepthEqualityTester.areEqual(should, is, checker, maxDepth, excludePropertiesPattern, excludeProperties);
	}

	/**
	 * Retruns a HashSet built from an array of strings that are all converted
	 * to lowercase.
	 * 
	 * @param values
	 * @return HashSet
	 */
	public static HashSet<String> buildLowerCaseHashSet(final String... values) {
		final HashSet<String> rVal = new HashSet<String>();
		for (final String value : values) {
			rVal.add(value.toLowerCase());
		}
		return rVal;
	}

	/**
	 * Convenience method for buildLowerCaseHashSet(final String... values).
	 * 
	 * @param values
	 * @return HashSet
	 */
	public static HashSet<String> buildLowerCaseHashSet(final Collection<String> values) {
		return buildLowerCaseHashSet(values.toArray(new String[values.size()]));
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