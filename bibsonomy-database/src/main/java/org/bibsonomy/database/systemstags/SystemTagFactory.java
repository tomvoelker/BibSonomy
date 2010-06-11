package org.bibsonomy.database.systemstags;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.systemstags.executable.ExecutableSystemTag;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemTagFactory {
	/*
	 * FIXME: rename class since this is no longer a real factory
	 * it is used as a bean (singleton) as a register of all systemTags
	 */
	
    private static final SystemTagFactory singleton = new SystemTagFactory();

    // The map that contains all executable systemTags
	private Map<String, ExecutableSystemTag> executableSystemTagMap;
	
	// The set that contains all searchSystemTags
	private final Set<String> searchSystemTagSet;
	
	// The DBSessionFactory (we need it for the forGroup tag)
	private DBSessionFactory dbSessionFactory;
	
	/**
	 * Constructor
	 */
	@SuppressWarnings("unchecked")
	private SystemTagFactory() {
		/*
		 * FIXME: shouldn't we configure this from the outside?
		 */
		final ClassPathXmlApplicationContext springBeanFactory = new ClassPathXmlApplicationContext("systemtags-context.xml");
		this.executableSystemTagMap = ((HashMap<String, ExecutableSystemTag>)springBeanFactory.getBean("executableSystemTagMap"));
		this.searchSystemTagSet = ((Set<String>)springBeanFactory.getBean("searchSystemTagSet"));
	}
	
	/**
 	 * @return the singleton 
	 */
	public static SystemTagFactory getInstance() {
		return singleton;
	}
	
	/**
	 * Returns a new instance of the required systemTag
	 * @param tagName = the tag describing the systemTag e. g. send:xyz or for:xyz
	 */
	public ExecutableSystemTag getExecutableSystemTag(String tagType) {
		if (isExecutableSystemTag(tagType)) {
			return this.executableSystemTagMap.get(tagType).newInstance();
		}
		return null;
	}

	/**
	 * Determins whether a tag (given by name) is an executable systemTag
	 * @param tagName
	 * @return
	 */
	public boolean isExecutableSystemTag(String tagType) {
		return this.executableSystemTagMap.containsKey(tagType);
	}
	
	/**
	 * Determins whether a tag (given by name) is a systemTag
	 * @param tagName
	 * @return
	 */
	public boolean isSearchSystemTag(String tagType) {
		return this.searchSystemTagSet.contains(tagType);
	}
	
	/**
	 * @return The session factory of this system tag factory.
	 */
	public DBSessionFactory getDbSessionFactory() {
		return this.dbSessionFactory;
	}

	/**  
	 * @param sessionFactory
	 */
	public void setDbSessionFactory(DBSessionFactory sessionFactory) {
		this.dbSessionFactory = sessionFactory;
	}

	/**
	 * @return map with executables system tags
	 */
	public Map<String, ExecutableSystemTag> getExecutableSystemTagMap() {
		return this.executableSystemTagMap;
	}

	/**
	 * @param executableSystemTagMap
	 */
	public void setExecutableSystemTagMap(final HashMap<String, ExecutableSystemTag> executableSystemTagMap) {
		this.executableSystemTagMap = executableSystemTagMap;
	}
}
