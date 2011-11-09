package org.bibsonomy.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.Database;
import no.priv.garshol.duke.Processor;
import no.priv.garshol.duke.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.entity.datasource.UserDataSource;
import org.bibsonomy.entity.matcher.UserEntityMatcher;
import org.bibsonomy.entity.matcher.UserEntityMatcher.UserMatch;
import org.bibsonomy.model.User;
import org.xml.sax.SAXException;

/**
 * class for matching user objects based on their real names
 * 
 * @author fei
 */
public class UserRealnameResolver {
	private final Log log = LogFactory.getLog(UserRealnameResolver.class);
	
	/** path to duke's config file */
	private static final String CONFIG_PATH = "classpath:UsernameResolver.xml";

	/** number of entries to process per block while building the entity index */
	private static final int INDEX_BLOCK_SIZE = 1000;
	
	/** path to duke's index */
	private String indexPath;

	/** duke's main configuration */
	private Configuration config;
	
	/**
	 * duke's user entity lookup index
	 */
	private Database userIndex; 
	
	public void init() {
		try {
			config = ConfigLoader.load(CONFIG_PATH);
			config.setPath(this.indexPath);
			this.userIndex = config.createDatabase(false);
			this.userIndex.openSearchers();
		} catch (IOException e) {
			log.error("Error accessing config file '+CONFIG_PATH+'", e);
		} catch (SAXException e) {
			log.error("Error parsing config file '+CONFIG_PATH+'", e);
		}
	}
	
	/** 
	 * build a new user resolution index
	 */
	public void buildIndex() {
		try {
			final Processor proc = new Processor(config, this.userIndex);
			proc.close();
			proc.index(config.getDataSources(), INDEX_BLOCK_SIZE);
		} catch (CorruptIndexException e) {
			log.error("Corrupt duke index.", e);
		} catch (IOException e) {
			log.error("Error accessing duke index.", e);
		}
		
	}
	
	/**
	 * resolve given users against the entity linkage index
	 * 
	 * @param others
	 * @return
	 */
	public Map<String, Collection<User>> resolveUsers(Collection<User> others)  {
		/**
		 * matcher for resolving identities  
		 */
		UserEntityMatcher matcher = new UserEntityMatcher();

		/** 
		 * main record linking engine
		 */
		Processor proc;
		
		//
		// match others against the record linkage index
		//
		try {
			proc = new Processor(config, this.userIndex);
			proc.addMatchListener(matcher);
			UserDataSource newEntries = new UserDataSource(others);
			Collection<DataSource> linkGroup = new ArrayList<DataSource>();
			linkGroup.add(newEntries);
			proc.linkRecords(linkGroup);
		} catch (CorruptIndexException e) {
			log.error("Corrupt duke index.", e);
		} catch (IOException e) {
			log.error("Error accessing duke index.", e);
		}

		//
		// build output data structure
		//
		Map<String, Collection<User>> retVal = new HashMap<String, Collection<User>>();
		for (Map.Entry<String,SortedSet<UserMatch>> match : matcher.getMatching().entrySet()) {
			String extId = match.getKey();
			Collection<User> matches = new ArrayList<User>();
			for (UserMatch other : match.getValue()) {
				User importUser = new User();
				importUser.setName(other.getProperty("user_name"));
				importUser.setRealname(other.getId());
				matches.add(importUser);
			}
			retVal.put(extId, matches);
		}
		
		return retVal;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public String getIndexPath() {
		return indexPath;
	}
}
