package org.bibsonomy.database.systemstags;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.database.systemstags.xml.Attribute;
import org.bibsonomy.database.systemstags.xml.SystemTagType;
import org.bibsonomy.database.systemstags.xml.SystemTagsCollection;
import org.bibsonomy.model.Tag;

/**
 * @author Andreas Koch
 * @version $Id$
 */

public class SystemTagFactory {
	private static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.systemstags.xml";
	// TODO path
	private static final String BIBSONOMY_SYSTEMTAGS_XML = "../bibsonomy-webapp/src/main/webapp/WEB-INF/systemtags.xml";
	private static HashMap<String, SystemTagType> systemTagMap;
	private static HashMap<String, SystemTag> executableSystemTagMap;

	/*
	 * useable in the xml configuration --> see systemtags.xml
	 */
	public static final String GROUPING = "GROUPING";

	/**
	 * @return map with executables system tags
	 */
	public static HashMap<String, SystemTag> getExecutableSystemTagMap() {
		if (executableSystemTagMap == null) {
			executableSystemTagMap = new HashMap<String, SystemTag>();
		}
		return executableSystemTagMap;
	}

	/**
	 * @return map with system tags
	 */
	public static HashMap<String, SystemTagType> getSystemTagMap() {
		if (systemTagMap == null) {
			renewSystemTagMap(BIBSONOMY_SYSTEMTAGS_XML);
		}
		return systemTagMap;
	}

	/**
	 * @param systemtagsFile
	 *            new configuration file
	 * 
	 * @return map with system tags according to the systemtags file
	 */
	public static HashMap<String, SystemTagType> renewSystemTagMap(String systemtagsFile) {
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

	public static SystemTag createExecutableTag(Tag tag) {
		return getExecutableSystemTagMap().get(tag);
	}

	/**
	 * 
	 * @param tag
	 * @return true if tag is existent
	 */
	public static boolean isSystemTag(String tag) {
		if (tag.equals("web")) {
			return true;
		}
		return getExecutableSystemTagMap().containsKey(tag) || systemTagMap.containsKey(tag);
	}

	public static String getAttributeValue(SystemTagType sTag, String attributeName) {
		for (Attribute attribute : sTag.getAttribute()) {
			if (attribute.getName().equals(attributeName)) {
				return attribute.getValue();
			}
		}
		return null;
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
