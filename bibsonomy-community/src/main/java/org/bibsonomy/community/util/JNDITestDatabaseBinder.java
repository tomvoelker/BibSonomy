package org.bibsonomy.community.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.ValidationUtils;
import org.mockejb.jndi.MockContextFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
/**
 * @author bkr
 *
 */
public final class JNDITestDatabaseBinder extends CommunityBase {
	private static final Log log = LogFactory.getLog(JNDITestDatabaseBinder.class);
	
	private static Context ctx = null;

	static {
		// create Mock JNDI context
		try {
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
		} catch (NamingException e) {
			log.error("Error setting initial context!", e);
		}
	}
	
	/**
	 * Don't create instances of this class - use the static methods instead.
	 * @throws NamingException 
	 */
	private JNDITestDatabaseBinder() throws NamingException {
	}

	/**
	 * Main method: read configuration file 'database.properties', create SQL Data Source and
	 * register it via JNDI
	 */
	public static final void bind() {
		bindDatabaseContext("bibsonomy_community", "database.properties");
		bindDatabaseContext("bibsonomy_community_posts", "database_posts.properties");
		bindContextConfiguration(CONTEXT_CONFIG_BEAN, getCommunityConfig("database.properties"));
	}
	
	private static CommunityConfig getCommunityConfig(String fileName) {
		final Properties props;		
		try {
			props = openPropertyFile(fileName);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		CommunityConfig config = new CommunityConfig();

		// get properties
		for( Object key : props.keySet() ) {
			if( !ValidationUtils.present((key.toString()))||!(key.toString()).startsWith(CONTEXT_CONFIG_BEAN) )
				continue;
			else {
				String propertyName = getPropertyName((String)key);
				String propertyValue= props.getProperty((String)key);
				try {
					PropertyUtils.setNestedProperty(config, propertyName, propertyValue);
					log.debug("Set community configuration property "+propertyName+" to "+propertyValue);
				} catch (Exception e) {
					log.warn("Error setting lucene configuration property "+propertyName+" to "+propertyValue+"('"+e.getMessage()+"')");
				}
			}
		}

		return config;
	}
	
	public static final void bind(final String contextName, final String url, final String database, final String username, final String password) {
		bindDatabaseContext(contextName, "jdbc:mysql://"+url+"/"+database, database, username, password);
	}

	private static void bindDatabaseContext(final String contextName, final String url, final String database, final String username, final String password) {
		bindDatabaseContext(contextName, getDataSource(url, database, username, password));
	}

	private static void bindDatabaseContext(final String contextName, final String fileName) {
		bindDatabaseContext(contextName, getDataSource(fileName));
	}

	private static void bindDatabaseContext(String contextName, DataSource ds) {
		try {			
			ctx.bind("java:comp/env/jdbc/" + contextName, ds);
		}
		catch (Exception ex) {
			log.error("Error when trying to bind test database connection '" + contextName + "' via JNDI");
			log.error(ex.getMessage());
		}
	}


	private static void bindContextConfiguration(String contextName, Object obj) {
		try {			
			ctx.bind(CONTEXT_ENV_NAME + "/" + contextName, obj);
		}
		catch (Exception ex) {
			log.error("Error when trying to bind test database connection '" + contextName + "' via JNDI");
			log.error(ex.getMessage());
		}
	}	
	
	/**
	 * Go back to original state
	 */
	public static void unbind() {
		if( ctx!=null )
			MockContextFactory.revertSetAsInitial();
	}

	private static DataSource getDataSource(final String configFile) {

		final Properties props = new Properties();		
		try {
			// read database properties
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(configFile));		
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return getDataSource(props.getProperty("url"), props.getProperty("database"), props.getProperty("username"),props.getProperty("password"));
	}	

	private static Properties openPropertyFile(String fileName) throws IOException {
		final Properties props = new Properties();
		// read properties
		try {
			props.load(new FileInputStream(new File(fileName)));
			log.debug("Loading configuration from file system.");
		} catch( IOException ex ) {
			props.load(JNDITestDatabaseBinder.class.getClassLoader().getResourceAsStream(fileName));		
			log.debug("Loading configuration from class path.");
		}
		return props;
	}		
	
	/**
	 * extract property name 
	 * @return
	 */
	private static String getPropertyName(String propertyKey) {
		if (propertyKey.lastIndexOf('.') > 0) {
	        propertyKey = propertyKey.substring(propertyKey.lastIndexOf('.')+1);
	    }
		
		return propertyKey;
	}
	
	private static DataSource getDataSource(final String url, final String database, final String username, final String password) {

		final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();

		dataSource.setUrl(url);
		dataSource.setDatabaseName(database);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		
		return dataSource;
	}	
}