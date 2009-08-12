package org.bibsonomy.testutil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.DatabaseType;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.database.util.DatabaseUtils;

/**
 * An implementation of the {@link DBSessionFactory} interface which intercepts
 * all calls to methods from the {@link DBSession} interface that are dealing
 * with transaction management. Instead of calling certain methods this
 * implementation circumvents transaction management and prevents write access
 * to the database. This way it's well suited for test cases which shouldn't
 * alter the database.<br/>
 * 
 * Since the test cases should write to the database too this implementation is
 * obsolete. It may just stay here because of its elegance ;-).
 * 
 * @author Jens Illig
 * @version $Id$
 */
@Deprecated
public class SandboxDBSessionFactory extends DatabaseUtils implements DBSessionFactory {

	private static final Log log = LogFactory.getLog(SandboxDBSessionFactory.class);
	private static final HashSet<String> firewalledMethods = new HashSet<String>();
	private DBSession realDbSession = null;
	private DBSession dbSessionProxy = null;

	static {
		firewalledMethods.add("beginTransaction");
		firewalledMethods.add("endTransaction");
		firewalledMethods.add("commitTransaction");
		firewalledMethods.add("close");
	}

	public DBSession getDatabaseSession() {
		if (this.dbSessionProxy == null) {
			this.realDbSession = super.getDBSessionFactory().getDatabaseSession();
			this.realDbSession.beginTransaction();

			this.dbSessionProxy = (DBSession) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { DBSession.class }, new InvocationHandler() {

				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
					if (firewalledMethods.contains(method.getName()) == false) {
						return method.invoke(realDbSession, args);
					}
					log.debug("firewalled invocation of method '" + method.getName() + "'");
					return null;
				}

			});
		}
		return this.dbSessionProxy;
	}

	/**
	 * Ends the transaction and closes the connection to the database. Should be
	 * called in a <code>tearDown</code> method.
	 */
	public void endTest() {
		if (this.realDbSession != null) {
			this.realDbSession.endTransaction();
			this.realDbSession.close();
		}
	}

	public DBSession getDatabaseSession(DatabaseType dbType) {
		// TODO Auto-generated method stub
		return null;
	}
}