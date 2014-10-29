package org.bibsonomy.entity;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.OverlappingFileLockException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;

import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.DataSource;
import no.priv.garshol.duke.Database;
import no.priv.garshol.duke.Processor;
import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.RecordImpl;
import no.priv.garshol.duke.datasources.InMemoryDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.entity.matcher.UserEntityMatcher;
import org.bibsonomy.entity.matcher.UserMatch;
import org.bibsonomy.model.User;

/**
 * class for matching user objects based on their real names
 * 
 * @author fei
 */
public class UserRealnameResolver {
	private static final Log log = LogFactory.getLog(UserRealnameResolver.class);

	/** number of entries to process per block while building the entity index */
	private static final int INDEX_BLOCK_SIZE = 1000;
	
	private static DataSource createDataSource(final Collection<User> others) {
		final InMemoryDataSource datasource = new InMemoryDataSource();
		for (final User user : others) {
			datasource.add(convert(user));
		}
		return datasource;
	}

	private static Record convert(final User user) {
		final RecordImpl entry = new RecordImpl();
		
		final String userName = user.getName();
		if (userName != null) {
			entry.addValue("user_name", userName);
		}
		
		final String userRealname = user.getRealname();
		if (userRealname != null) {
			entry.addValue("user_realname", userRealname);
		}
		
		final String place = user.getPlace();
		if (place != null) {
			entry.addValue("place", place);
		}
		
		final URL homepage = user.getHomepage();
		if (homepage != null) {
			entry.addValue("user_homepage", homepage.toExternalForm());
		}
		
		return entry;
	}

	/** duke's main configuration */
	private Configuration config;
	
	/**
	 * duke's user entity lookup index
	 */
	private Database userIndex;
	
	/**
	 * inits the user realname resolver by loading the duke index
	 */
	public void init() {
		try {
			this.userIndex = this.config.createDatabase(false);
			this.userIndex.openSearchers();
		} catch (final OverlappingFileLockException e) {
			log.error("Error opening user name index for the Facebook importer: " + e.getMessage());
		} catch (final IOException e) {
			log.error("Error opening index", e);
		} catch (final Exception e) {
			log.warn("error while creating database, disabling index", e);
		}
	}
	
	/** 
	 * build a new user resolution index
	 */
	public void buildIndex() {
		if (this.isDisabled()) {
			return;
		}
		try {
			final Processor proc = new Processor(this.config, this.userIndex);
			proc.index(this.config.getDataSources(), INDEX_BLOCK_SIZE);
		} catch (final CorruptIndexException e) {
			log.error("Corrupt duke index.", e);
		} catch (final IOException e) {
			log.error("Error accessing duke index.", e);
		}
	}
	
	private boolean isDisabled() {
		return this.userIndex == null;
	}

	/**
	 * resolve given users against the entity linkage index
	 * 
	 * @param others
	 * @return TODO
	 */
	public Map<String, Collection<User>> resolveUsers(final Collection<User> others)  {
		if (this.isDisabled()) {
			return Collections.emptyMap();
		}
		
		/*
		 * matcher for resolving identities  
		 */
		final UserEntityMatcher matcher = new UserEntityMatcher();
		
		//
		// match others against the record linkage index
		//
		try {
			/* 
			 * main record linking engine
			 */
			final Processor proc = new Processor(this.config, this.userIndex);
			proc.addMatchListener(matcher);
			final DataSource newEntries = createDataSource(others);
			proc.linkRecords(Collections.singletonList(newEntries));
		} catch (final CorruptIndexException e) {
			log.error("Corrupt duke index.", e);
		} catch (final IOException e) {
			log.error("Error accessing duke index.", e);
		}

		//
		// build output data structure
		//
		final Map<String, Collection<User>> retVal = new HashMap<String, Collection<User>>();
		for (final Map.Entry<String,SortedSet<UserMatch>> match : matcher.getMatching().entrySet()) {
			final String extId = match.getKey();
			final Collection<User> matches = new LinkedList<User>();
			for (final UserMatch other : match.getValue()) {
				final User importUser = new User();
				importUser.setName(other.getProperty("user_name"));
				importUser.setRealname(other.getId());
				matches.add(importUser);
			}
			retVal.put(extId, matches);
		}
		
		return retVal;
	}
	
	/**
	 * called when the index should be closed
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.userIndex.close();
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(final Configuration config) {
		this.config = config;
	}
}
