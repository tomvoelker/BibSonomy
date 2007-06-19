package org.bibsonomy.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Methods to create objects from the model like {@link Bookmark},
 * {@link BibTex}, {@link User} or {@link Post}.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class ModelUtils {

	private static final Logger log = Logger.getLogger(ModelUtils.class);

	private static void setResourceDefaults(final Resource resource) {
		resource.setCount(0);
	}

	/**
	 * Creates a bookmark with all properties set.
	 */
	public static Bookmark getBookmark() {
		final Bookmark bookmark = new Bookmark();
		setResourceDefaults(bookmark);
		bookmark.setTitle("test");
		bookmark.setUrl("http://www.bibonomy.org");
		return bookmark;
	}

	/**
	 * Creates a BibTex with all properties set.
	 */
	public static BibTex getBibTex() {
		final BibTex bibtex = new BibTex();
		setBeanPropertiesOn(bibtex);
		setResourceDefaults(bibtex);
		bibtex.recalculateHashes();
		return bibtex;
	}

	public static User getUser() {
		final User user = new User();
		setBeanPropertiesOn(user);
		user.setName("jaeschke");
		return user;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Resource> Post<T> generatePost(final Class<T> resourceType) {
		final Post<T> post = new Post<T>();

		final Group group = new Group();
		group.setGroupId(GroupID.PUBLIC.getId());
		group.setDescription(null);
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
			throw new UnsupportedResourceTypeException(resourceType.getName());
		}
		post.setResource(resource);

		return post;
	}

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

	public static void assertPropertyEquality(final Object should, final Object is, final String... excludeProperties) {
		final Set<String> skip;
		if (excludeProperties != null || excludeProperties.length > 0) {
			skip = new HashSet<String>();
			skip.addAll(Arrays.asList(excludeProperties));
		} else {
			skip = null;
		}
		assertPropertyEquality(should, is, skip);
	}

	public static void assertPropertyEquality(final Object should, final Object is, final Set<String> excludeProperties) {
		try {
			assertTrue(should.getClass().isAssignableFrom(is.getClass()));
			final BeanInfo bi = Introspector.getBeanInfo(should.getClass());
			for (final PropertyDescriptor d : bi.getPropertyDescriptors()) {
				Exception catched = null;
				try {
					if ((excludeProperties == null) || (excludeProperties.contains(d.getName()) == false)) {
						final Method getter = d.getReadMethod();
						final Class type = d.getPropertyType();
						if ((getter != null) && ((type == String.class) || (type.isPrimitive() == true) || (Number.class.isAssignableFrom(type) == true))) {
							log.debug("comparing property " + d.getName());
							assertEquals(d.getName(), getter.invoke(should, (Object[]) null), getter.invoke(is, (Object[]) null));
						}
					}
				} catch (final IllegalArgumentException ex) {
					catched = ex;
				} catch (final IllegalAccessException ex) {
					catched = ex;
				} catch (final InvocationTargetException ex) {
					catched = ex;
				}
				if (catched != null) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, catched, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (final IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + should.getClass().getName() + "'");
		}
	}

	private static Object getDummyValue(final Class type, final String name) {
		if (String.class == type) {
			return "test-" + name;
		}
		if ((int.class == type) || (Integer.class == type)) {
			return name.hashCode();
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
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}

	public static HashSet<String> buildLowerCaseHashSet(final String... values) {
		final HashSet<String> rVal = new HashSet<String>();
		for (final String value : values) {
			rVal.add(value.toLowerCase());
		}
		return rVal;
	}

	public static HashSet<String> buildLowerCaseHashSet(final Collection<String> values) {
		return buildLowerCaseHashSet(values.toArray(new String[values.size()]));
	}

	public static boolean hasTags(final Post<?> post, final Set<String> requiredTags) {
		int required = requiredTags.size();
		for (final Tag presentTag : post.getTags()) {
			if (requiredTags.contains(presentTag.getName().toLowerCase()) == true) {
				--required;
				log.debug("found " + presentTag.getName());
			}
		}
		if (required > 0) {
			return false;
		} else {
			return true;
		}
	}

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
		} else {
			return true;
		}
	}
}