package org.bibsonomy.database.systemstags;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.database.systemstags.xml.Attribute;
import org.bibsonomy.database.systemstags.xml.SystemTagType;
import org.bibsonomy.database.systemstags.xml.SystemTagsCollection;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemTagFactory {
	private static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.database.systemstags.xml";
	// TODO path
	private static final String BIBSONOMY_SYSTEMTAGS_XML = "../bibsonomy-webapp/src/main/webapp/WEB-INF/systemtags.xml";
	private static Map<String, SystemTagType> systemTagMap;
	private static Map<String, SystemTag> executableSystemTagMap;

	/*
	 * useable in the xml configuration --> see systemtags.xml
	 */
	public static final String GROUPING = "GROUPING";


	/**
	 * Initializer
	 */
	static {
		final ClassPathXmlApplicationContext springBeanFactory = 
			new ClassPathXmlApplicationContext("systemtags-context.xml");
		setExecutableSystemTagMap((HashMap<String,SystemTag>)springBeanFactory.getBean("executableSystemTagMap"));
	}
	
	/**
	 * @return map with executables system tags
	 */
	public static Map<String, SystemTag> getExecutableSystemTagMap() {
		if (executableSystemTagMap == null) {
			executableSystemTagMap = new HashMap<String, SystemTag>();
		}
		return executableSystemTagMap;
	}

	/**
	 * @return map with system tags
	 */
	public static Map<String, SystemTagType> getSystemTagMap() {
		if (systemTagMap == null) {
			renewSystemTagMap(BIBSONOMY_SYSTEMTAGS_XML);
		}
		return systemTagMap;
	}

	public static void setExecutableSystemTagMap(HashMap<String, SystemTag> executableSystemTagMap) {
		SystemTagFactory.executableSystemTagMap = executableSystemTagMap;
	}

	/**
	 * @param systemtagsFile
	 *            new configuration file
	 * 
	 * @return map with system tags according to the systemtags file
	 */
	public static Map<String, SystemTagType> renewSystemTagMap(String systemtagsFile) {
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
	public static SystemTagType createTag(String tag, String value) {
		if (value.startsWith(":")) {
			value = value.substring(1);
		}
		SystemTagType tagType = getSystemTagMap().get(tag);
		if (tagType != null) {
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
	 * @param dbLogic Database interface on which tag should operate
	 * @param sessionFactory session factory for creating new database sessions
	 * @param tag
	 * @return null, if given tag doesn't match to a known system tag.
	 */
	public static SystemTag createExecutableTag(LogicInterface dbLogic, DBSessionFactory sessionFactory, Tag tag) {
		if (!SystemTagFactory.isSystemTag(tag.getName()))
			return null;
		String name = SystemTagFactory.extractName(tag.getName());
		SystemTag retVal = null;
		if( name!=null ) {
			if( getExecutableSystemTagMap().get(name)!=null ) {
				retVal = getExecutableSystemTagMap().get(name).newInstance();
				retVal.setTag(tag);
				retVal.setLogicInterface(dbLogic);
				retVal.setDbSessionFactory(sessionFactory);
			}
		}
		return retVal;
	}

	
	/**
	 * @param tag
	 * @return true if tag is existent
	 */
	public static boolean isSystemTag(String tag) {
		final String name = extractName(tag);
		
		if( name!=null ) {
			return  getExecutableSystemTagMap().containsKey(name) || getSystemTagMap().containsKey(name);
		}
		return false;
	}

	public static String getAttributeValue(SystemTagType sTag, String attributeName) {
		for (Attribute attribute : sTag.getAttribute()) {
			if (attribute.getName().equals(attributeName)) {
				return attribute.getValue();
			}
		}
		return null;
	}
	
	//------------------------------------------------------------------------
	// helpers
	//------------------------------------------------------------------------
	/**
	 * Extract system tag's argument.
	 * @return tag's argument, if found.
	 */
	public static String extractArgument(String tagName) {
		final Pattern sysPrefix = Pattern.compile("^\\s*(sys:|system:)?.*:(.*)");
		Matcher action = sysPrefix.matcher(tagName);
		if( action.lookingAt() )
			return action.group(2);
		return null;
	}

	/**
	 * Extract system tag's name.
	 * @return tag's name, if found, null otherwise.
	 */
	public static String extractName(String tagName) {
		final Pattern sysPrefix = Pattern.compile("^\\s*(sys:|system:)?(.*):.*");
		Matcher action = sysPrefix.matcher(tagName);
		if( action.lookingAt() )
			return action.group(2);
		return null;
	}	
	
	/**
	 * Removes all occurrences of system tags sys:&lt;name&gt;:&lt;argument&gt;,
	 * system:&lt;name&gt;:&lt;argument&gt; and &lt;name&gt;:&lt;argument&gt;
	 * 
	 * @param tags collection of tags to alter 
	 * @param name name of system tag to remove. If name==null, every system tag will be removed
	 * @return number of occurrences removed.
	 */
	public static int removeSystemTag(Set<Tag> tags, String name) {
		int nr = 0;
		
		Collection<Tag> toRemove = new HashSet<Tag>();
		// traverse all tags
		for( Tag tag : tags ) {
			if( isSystemTag(tag.getName()) && (name!=null||name.equals(extractName(tag.getName()))) ) {
				toRemove.add(tag);
				nr++;
			}
		}
		// remove collected occurrences
		tags.removeAll(toRemove);
		
		// all done.
		return nr;
	}

	private static void importConfiguration(String systemtagsFile) {
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
}
