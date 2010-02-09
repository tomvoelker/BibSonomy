package org.bibsonomy.database.systemstags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.database.systemstags.xml.SystemTagType;
import org.bibsonomy.database.systemstags.xml.SystemTagsCollection;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.Tag;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemTagFactory {
	private static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.database.systemstags.xml";
	// TODO path
	private static final String BIBSONOMY_SYSTEMTAGS_XML = "../bibsonomy-webapp/src/main/webapp/WEB-INF/systemtags.xml";
	private Map<String, SystemTagType> systemTagMap;
	private Map<String, SystemTag> executableSystemTagMap;

	/*
	 * useable in the xml configuration --> see systemtags.xml
	 */
	public static final String GROUPING = "GROUPING";

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
	 * @return map with system tags
	 */
	public Map<String, SystemTagType> getSystemTagMap() {
		if (systemTagMap == null) {
			renewSystemTagMap(BIBSONOMY_SYSTEMTAGS_XML);
		}
		return systemTagMap;
	}

	/**
	 * Sets the map with the executable system tags.
	 * 
	 * @param executableSystemTagMap
	 */
	public void setExecutableSystemTagMap(HashMap<String, SystemTag> executableSystemTagMap) {
		executableSystemTagMap = executableSystemTagMap;
	}

	/**
	 * @param systemtagsFile
	 *            new configuration file
	 * 
	 * @return map with system tags according to the systemtags file
	 */
	public Map<String, SystemTagType> renewSystemTagMap(String systemtagsFile) {
		systemTagMap = new HashMap<String, SystemTagType>();
		importConfiguration(systemtagsFile);
		return systemTagMap;
	}

	/**
	 * returns system tag object, if corresponding tag is existent and value
	 * matches the requested format
	 * 
	 * @param tag
	 *            tag name
	 * @param value
	 *            tag value
	 * @return system tag object
	 */
	public SystemTagType createTag(String tag, String value) {
		if (value.startsWith(":")) {
			value = value.substring(1);
		}
		final SystemTagType tagType = getSystemTagMap().get(tag);
		if (present(tagType)) {
			if (tagType.getFormat() == null || value.matches(tagType.getFormat())) {
				return tagType;
			}
		}

		return null;
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
			if( getExecutableSystemTagMap().get(name)!=null ) {
				final SystemTag retVal = getExecutableSystemTagMap().get(name).newInstance();
				retVal.setTag(tag);
				retVal.setDbSessionFactory(sessionFactory);
			}
		}
		return null;
	}

	/**
	 * @param tag
	 * @return true if tag is existent
	 */
	public boolean isSystemTag(String tag) {
		final String name = SystemTagsUtil.extractName(tag);
		
		if (present(name)) {
			return getExecutableSystemTagMap().containsKey(name) || getSystemTagMap().containsKey(name);
		}
		return false;
	}


	private void importConfiguration(String systemtagsFile) {
		try {
			final JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE_DECLARATION, SystemTagFactory.class.getClassLoader());
			
			final Unmarshaller unmarshaller = jc.createUnmarshaller();
			final SystemTagsCollection collection = (SystemTagsCollection) ((JAXBElement<?>) unmarshaller.unmarshal(new File(systemtagsFile))).getValue();
			systemTagMap = new HashMap<String, SystemTagType>();
			final List<SystemTagType> systemtag = collection.getSystemtag();
			for (final SystemTagType tag : systemtag) {
				systemTagMap.put(tag.getName(), tag);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes all occurrences of system tags sys:&lt;name&gt;:&lt;argument&gt;,
	 * system:&lt;name&gt;:&lt;argument&gt; and &lt;name&gt;:&lt;argument&gt;
	 * 
	 * @param tags collection of tags to alter 
	 * @param name name of system tag to remove. If name==null, every system tag will be removed
	 * @return number of occurrences removed.
	 */
	public int removeSystemTag(final Set<Tag> tags, final String name) {
		int nr = 0;
		
		final Collection<Tag> toRemove = new HashSet<Tag>();
		// traverse all tags
		for (final Tag tag : tags ) {
			if( isSystemTag(tag.getName()) && (present(name) || name.equals(SystemTagsUtil.extractName(tag.getName()))) ) {
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
