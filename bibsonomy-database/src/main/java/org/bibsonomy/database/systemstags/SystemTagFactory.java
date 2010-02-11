package org.bibsonomy.database.systemstags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.Tag;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemTagFactory {
	private Map<String, SystemTag> executableSystemTagMap;

	private DBSessionFactory sessionFactory;
	

	/**
	 * Constructor
	 */
	public SystemTagFactory() {
		/*
		 * FIXME: shouldn't we configure this from the outside?
		 */
		final ClassPathXmlApplicationContext springBeanFactory = new ClassPathXmlApplicationContext("systemtags-context.xml");
		setExecutableSystemTagMap((HashMap<String,SystemTag>)springBeanFactory.getBean("executableSystemTagMap"));
	}
	
	/**
	 * @return map with executables system tags
	 */
	public Map<String, SystemTag> getExecutableSystemTagMap() {
		if (executableSystemTagMap == null) {
			executableSystemTagMap = new HashMap<String, SystemTag>();
		}
		return executableSystemTagMap;
	}


	/**
	 * Sets the map with the executable system tags.
	 * 
	 * @param executableSystemTagMap
	 */
	public void setExecutableSystemTagMap(final HashMap<String, SystemTag> executableSystemTagMap) {
		this.executableSystemTagMap = executableSystemTagMap;
	}

	/**
	 * Check whether given tag matches to a registered system tag and
	 * initialize corresponding instance on success.
	 * 
	 * @param tag
	 * @return null, if given tag doesn't match to a known system tag.
	 */
	public SystemTag createExecutableTag(final Tag tag) {
		if (!isSystemTag(tag.getName()))
			return null;
		final String name = SystemTagsUtil.extractName(tag.getName());
		if (present(name)) {
			if (present(getExecutableSystemTagMap().get(name))) {
				final SystemTag retVal = getExecutableSystemTagMap().get(name).newInstance();
				retVal.setTag(tag);
				retVal.setDbSessionFactory(sessionFactory);
				retVal.setSystemTagFactory(this);
				return retVal;
			}
		}
		return null;
	}

	/**
	 * @param tag
	 * @return true if tag is existent
	 */
	public boolean isSystemTag(final String tag) {
		final String name = SystemTagsUtil.extractName(tag);
		return present(name) && getExecutableSystemTagMap().containsKey(name);
	}


	
	/**
	 * Removes all occurrences of system tags sys:&lt;name&gt;:&lt;argument&gt;,
	 * system:&lt;name&gt;:&lt;argument&gt; and &lt;name&gt;:&lt;argument&gt;
	 * 
	 * @param tags collection of tags to alter 
	 * @param the name of the system tag to be removed. 
	 * @return number of occurrences removed.
	 */
	public int removeSystemTag(final Set<Tag> tags, final String name) {
		int nr = 0;
		
		final Collection<Tag> toRemove = new HashSet<Tag>();
		// traverse all tags
		for (final Tag tag : tags ) {
			if( isSystemTag(tag.getName()) && present(name) && name.equals(SystemTagsUtil.extractName(tag.getName())) ) {
				toRemove.add(tag);
				nr++;
			}
		}
		// remove collected occurrences
		tags.removeAll(toRemove);
		
		// all done.
		return nr;
	}


	/**
	 * removes all SystemTags from a given tag set
	 * @param tags
	 * @return number of tags, that were removed
	 */
	public int removeAllSystemTags(final Set<Tag> tags) {
		final Iterator<Tag> iterator = tags.iterator();
		int nr = 0;
		
		while (iterator.hasNext()) {
			final Tag tag = iterator.next();
			/*
			 *  FIXME: We have two(!) isSystemTag methods, the first tests for executables, the other one for sys: or system:
			 *  Implement one method to check for all systemTags
			*/
			if (isSystemTag(tag.getName()) || SystemTagsUtil.isSystemTag(tag.getName())) {
				iterator.remove();
				nr++;
			}
		}
		return nr;
	}


	/**
	 * @return The session factory of this system tag factory.
	 */
	public DBSessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/** Sets the session factory for DB access.
	 * @param sessionFactory
	 */
	public void setSessionFactory(DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
