package org.bibsonomy.database.systemstags;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.systemstags.executable.ExecutableSystemTag;
import org.bibsonomy.database.systemstags.search.SearchSystemTag;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Andreas Koch
 * @version $Id$
 */
public class SystemTagFactory {
    private static final String SYSTEM_TAG_CONFIG_FILE = "systemtags-context.xml";
    
	private static final SystemTagFactory singleton = new SystemTagFactory();

	/**
 	 * @return the singleton 
	 */
	public static SystemTagFactory getInstance() {
		return singleton;
	}
	

    /** The map that contains all executable systemTags */
	private final Map<String, ExecutableSystemTag> executableSystemTagMap;
	
	/** The set that contains all searchSystemTags */
	private final Map<String, SearchSystemTag> searchSystemTagMap;
	
	/** The DBSessionFactory (we need it for the forGroup tag) */
	private DBSessionFactory dbSessionFactory;
	
	/**
	 * Constructor
	 */
	@SuppressWarnings("unchecked")
	private SystemTagFactory() {
		/*
		 * FIXME: shouldn't we configure this from the outside?
		 */
		final ClassPathXmlApplicationContext springBeanFactory = new ClassPathXmlApplicationContext(SYSTEM_TAG_CONFIG_FILE);
		this.executableSystemTagMap = new HashMap<String, ExecutableSystemTag>();
		this.fillExecutableSystemTagMap( (Set<ExecutableSystemTag>)springBeanFactory.getBean("executableSystemTagSet") );
		this.searchSystemTagMap = new HashMap<String, SearchSystemTag>();
		this.fillSearchSystemTagMap( (Set<SearchSystemTag>)springBeanFactory.getBean("searchSystemTagSet") );
	}
	
	
	private void fillExecutableSystemTagMap(Set<ExecutableSystemTag> executableSystemTags) {
		for (ExecutableSystemTag sysTag: executableSystemTags) {
			this.executableSystemTagMap.put(sysTag.getName(), sysTag);
		}
	}

	private void fillSearchSystemTagMap (Set<SearchSystemTag> searchSystemTags) {
		for (SearchSystemTag sysTag: searchSystemTags) {
			this.searchSystemTagMap.put(sysTag.getName(), sysTag);
		}
	}
	
	/**
	 * Returns a new instance of the required systemTag
	 * 
	 * @param tagType = the tag describing the systemTag e. g. send:xyz or for:xyz
	 * @return an executable system tag
	 */
	public ExecutableSystemTag getExecutableSystemTag(final String tagType) {
		if (isExecutableSystemTag(tagType.toLowerCase())) {
			return this.executableSystemTagMap.get(tagType.toLowerCase()).newInstance();
		}
		return null;
	}

	/**
	 * Returns a new instance of the required systemTag
	 * 
	 * @param tagType = the tag describing the systemTag e. g. send:xyz or for:xyz
	 * @return a search system tag
	 */
	public SearchSystemTag getSearchSystemTag(final String tagType) {
		if (isSearchSystemTag(tagType.toLowerCase())) {
			return this.searchSystemTagMap.get(tagType.toLowerCase()).newInstance();
		}
		return null;
	}

	/**
	 * Determines whether a tag (given by name) is an executable systemTag
	 * 
	 * @param tagType
	 * @return <code>true</code> iff it's an executable system tag
	 */
	public boolean isExecutableSystemTag(final String tagType) {
		return this.executableSystemTagMap.containsKey(tagType.toLowerCase());
	}
	
	/**
	 * Determines whether a tag (given by name) is a systemTag
	 * 
	 * @param tagType
	 * @return <code>true</code> iff it's a search system tag
	 */
	public boolean isSearchSystemTag(final String tagType) {
		return this.searchSystemTagMap.containsKey(tagType.toLowerCase());
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
	public void setDbSessionFactory(final DBSessionFactory sessionFactory) {
		this.dbSessionFactory = sessionFactory;
	}
}
