package webdav.helper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ServletContainerHelper {

	/**
	 * Gets the {@link javax.sql.DataSource} from the context in the servlet container (e.g. Tomcat).
	 * 
	 * @return A (hopefully) working instance of {@link javax.sql.DataSource}
	 * @throws RuntimeException When something goes wrong while fetching the
	 *           {@link javax.sql.DataSource}
	 */
	public static DataSource getDataSourceFromContext() {
		DataSource dataSource = null;
		try {
			final Context initContext = new InitialContext();
			final Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (final NamingException ex) {
			throw new RuntimeException("Cannot retrieve java:/comp/env/bibsonomy", ex);
		}
		return dataSource;
	}
}