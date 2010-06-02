package org.bibsonomy.database.testutil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockejb.jndi.MockContextFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Helper class for binding a test database resource via JNDI to enable database access
 * without a running application server, which usually provides the JNDI tree.
 * 
 * @author Dominik Benz
 * @author bkr
 * @author dzo
 * @version $Id$
 */
public final class JNDIBinder {
	private static final Log log = LogFactory.getLog(JNDIBinder.class);
	
	/**
	 * TODO
	 */
	public static final String JDBC_URL_START = "jdbc:mysql://";

	/**
	 * the key of the host property
	 */
	public static final String HOST_KEY = "host";

	/**
	 * the key of the database property
	 */
	public static final String DATABASE_KEY = "database";
	
	/**
	 * the key of the options property
	 */
	public static final String OPTIONS_KEY = "options";

	/**
	 * the key of the password property
	 */
	public static final String PASSWORD_KEY = "password";
	
	/**
	 * the key of the username property
	 */
	public static final String USERNAME_KEY = "username";
	
	private static final List<String> IGNORED_PROPERTY_FILES = Arrays.asList("log4j.properties");
	
	/**
	 * Go back to original state
	 */
	public static void unbind() {
		MockContextFactory.revertSetAsInitial();
	}	

	/**
	 * TODO
	 */
	public static void bind() {
		// get all propertyFile names
		final Set<String> names = new HashSet<String>();
		
		try {
			final Enumeration<URL> resources = JNDIBinder.class.getClassLoader().getResources("");
			final URL nextElement = resources.nextElement();
			
			final File file = new File(nextElement.getPath());
			final File[] listFiles = file.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith("_database.properties");
				}
			});
			
			for (final File props : listFiles) {
				names.add(props.getName());
			}
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		names.removeAll(IGNORED_PROPERTY_FILES);
		
		try {			
			// create Mock JNDI context
			MockContextFactory.setAsInitial();
			final InitialContext ctx = new InitialContext();
			
			for (final String propFile : names) {
				bindDataSource(propFile, ctx);
			}
		} catch (Exception ex) {
			log.error("Error when trying to bind test database connection via JNDI");
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
	
	private static void bindDataSource(String propFile, InitialContext ctx) throws IOException, NamingException {
		log.debug("loading properties " + propFile);
		final Properties properties = getPropertiesFromFile(propFile);
		
		final MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
		
		final String database = properties.getProperty(DATABASE_KEY);
		final String host = properties.getProperty(HOST_KEY);
		final String options = properties.getProperty(OPTIONS_KEY);
		final String password = properties.getProperty(PASSWORD_KEY);
		final String username = properties.getProperty(USERNAME_KEY);
		
		dataSource.setUrl(JDBC_URL_START + host + "/" + database + options);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		
		final String jdbcKey = propFile.substring(0, propFile.lastIndexOf("_"));
		
		ctx.bind("java:comp/env/jdbc/" + jdbcKey, dataSource);
	}

	private static Properties getPropertiesFromFile(String filename) throws IOException {
		final Properties props = new Properties();		

		// read database properties
		props.load(JNDIBinder.class.getClassLoader().getResourceAsStream(filename));
		
		return props;
	}
}