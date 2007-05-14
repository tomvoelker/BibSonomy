package org.bibsonomy.testutil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;
import org.junit.Assert;

/**
 * Methods to create objects from the model like {@link Bookmark} or
 * {@link BibTex}.
 * 
 * @author Christian Schenk
 */
public class ModelUtils {
	private static final Logger log = Logger.getLogger(ModelUtils.class);

	// FIXME
	private static void setResourceDefaults(final Resource resource) {
		// resource.setContentId(1);
		resource.setCount(0);
		// resource.setDate(null);
		// resource.setGroupId(ConstantID.GROUP_KDE.getId());
		// resource.setGroupName("kde");
		// resource.setUrl("");
		// resource.setUserName("kde");
	}

	/**
	 * Creates a bookmark with all properties set.
	 */
	// FIXME
	public static Bookmark getBookmark() {		
		final Bookmark rVal = new Bookmark();
		setResourceDefaults(rVal);
		rVal.setTitle("test");
		//rVal.setExtended("test");
		rVal.setUrl("http://www.bibonomy.org");
		return rVal;
	}

	/**
	 * Creates a BibTex with all properties set.
	 */
	public static BibTex getBibTex() {
		final BibTex rVal = new BibTex();
		setBeanPropertiesOn(rVal);
		setResourceDefaults(rVal);
		rVal.recalculateHashes();
		return rVal;
	}

	private static void setBeanPropertiesOn(Object val) {
		try {
			final BeanInfo bi = Introspector.getBeanInfo(val.getClass());
			for (PropertyDescriptor d : bi.getPropertyDescriptors()) {
				try {
					final Method setter = d.getWriteMethod();
					final Method getter = d.getReadMethod();
					if ((setter != null) && (getter != null)) {
						setter.invoke(val, new Object [] { getDummyValue(d.getPropertyType(), d.getName()) });
					}
				} catch (Exception ex) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + val.getClass().getName() + "'");
		}
	}
	
	public static void assertPropertyEquality(Object should, Object is, Set<String> excludeProperties) {
		try {
			Assert.assertTrue(should.getClass().isAssignableFrom(is.getClass()));
			final BeanInfo bi = Introspector.getBeanInfo(should.getClass());
			for (PropertyDescriptor d : bi.getPropertyDescriptors()) {
				Exception catched = null;
				try {
					if ((excludeProperties == null) || (excludeProperties.contains(d.getName()) == false)) {
						final Method getter = d.getReadMethod();
						final Class type = d.getPropertyType();
						if ((getter != null) && ((type == String.class) || (type.isPrimitive() == true) || (Number.class.isAssignableFrom(type) == true))) {
							log.debug("comparing property " + d.getName());
							Assert.assertEquals(d.getName(), getter.invoke(should, (Object[]) null), getter.invoke(is, (Object[]) null));
						}
					}
				} catch (IllegalArgumentException ex) {
					catched = ex;
				} catch (IllegalAccessException ex) {
					catched = ex;
				} catch (InvocationTargetException ex) {
					catched = ex;
				}
				if (catched != null) {
					ExceptionUtils.logErrorAndThrowRuntimeException(log, catched, "could not invoke setter '" + d.getName() + "'");
				}
			}
		} catch (IntrospectionException ex) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, ex, "could not introspect object of class '" + should.getClass().getName() + "'");
		}
	}

	private static Object getDummyValue(Class type, String name) {
		if (type == String.class) {
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
			} catch (MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
		}
		log.debug("no dummy value for type '" + type.getName() + "'");
		return null;
	}

	public static User getUser() {
		User u = new User();
		setBeanPropertiesOn(u);
		u.setName("jaeschke");
		return u;
	}
}