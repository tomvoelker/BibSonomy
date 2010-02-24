package org.bibsonomy.lucene.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.bibsonomy.lucene.param.LuceneConfig;
import org.bibsonomy.util.ValidationUtils;
import org.mockejb.jndi.MockContextFactory;

/**
 * Helper class for binding a test database resource via JNDI to enable database
 * access without a running application server, which usually provides the JNDI
 * tree.
 * 
 * @author Dominik Benz
 * @version $Id: JNDITestDatabaseBinder.java,v 1.1 2009-09-30 16:58:16 folke Exp
 *          $
 */

public final class JNDITestDatabaseBinder extends LuceneBase {

	/** logging */
	private static final Logger log = Logger
			.getLogger(JNDITestDatabaseBinder.class);

	/** context name for environment variables */
	public static final String CONTEXTNAME = "java:/comp/env/";

	/** name of the property file which configures lucene */
	private static final String LUCENEPROPERTYFILENAME = "lucene.properties";

	/** property key for database url */
	private static final String PROPERTY_DB_URL = "db.url";

	/** property key for database username */
	private static final String PROPERTY_DB_USERNAME = "db.username";

	/** property key for database password */
	private static final String PROPERTY_DB_PASSWORD = "db.password";

	/**
	 * Don't create instances of this class - use the static methods instead.
	 */
	private JNDITestDatabaseBinder() {
	}

	/**
	 * Main method: read configuration file 'database.properties', create SQL
	 * Data Source and register it via JNDI
	 */
	public static void bind() {
		bindDatabaseContext("bibsonomy_lucene", LUCENEPROPERTYFILENAME);
		bindLuceneConfig(CONTEXTNAME, LUCENEPROPERTYFILENAME);
	}

	private static void bindLuceneConfig(String contextName, String fileName) {
		Context ctx;
		
		final Properties props;		
		try {
			props = openPropertyFile(fileName);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		LuceneConfig config = new LuceneConfig();

		// get properties
		for( Object key : props.keySet() ) {
			if( !ValidationUtils.present((key.toString()))||!(key.toString()).startsWith(CONTEXT_CONFIG_BEAN) )
				continue;
			else {
				String propertyName = getPropertyName((String)key);
				String propertyValue= props.getProperty((String)key);
				try {
					PropertyUtils.setNestedProperty(config, propertyName, propertyValue);
					log.debug("Set lucene configuration property "+propertyName+" to "+propertyValue);
				} catch (Exception e) {
					log.warn("Error setting lucene configuration property "+propertyName+" to "+propertyValue+"('"+e.getMessage()+"')");
				}
			}
		}
		
		// bind bean in context
		try {
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind(contextName+CONTEXT_CONFIG_BEAN, config);
		} catch (NamingException ex) {
			log.error("Error binding environment variable:'" + contextName + "' via JNDI ('"+ex.getMessage()+"')");
		}
	}



	private static void bindDatabaseContext(final String contextName,
			final String fileName) {
		final InitialContext ctx;
		final DataSource ds = getBasicDataSource(fileName);
		try {
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			ctx = new InitialContext();
			ctx.bind("java:comp/env/jdbc/" + contextName, ds);
			ctx.bind("java:/comp/env/jdbc/" + contextName, ds);
		} catch (NamingException ex) {
			log.error("Error when trying to bind test database connection '"
					+ contextName + "' via JNDI");
			log.error(ex.getMessage());
		}

	}

	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}

	/**
	 * Create sql data source according to configuration in given property file
	 * 
	 * @param configFile
	 * @return
	 */
	private static DataSource getBasicDataSource(final String configFile) {

		final Properties props;
		try {
			// read database properties
			props = openPropertyFile(configFile);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		final BasicDataSource dataSource = new BasicDataSource();

		dataSource.setUrl(props.getProperty(PROPERTY_DB_URL));
		dataSource.setUsername(props.getProperty(PROPERTY_DB_USERNAME));
		dataSource.setPassword(props.getProperty(PROPERTY_DB_PASSWORD));

		return dataSource;
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
}