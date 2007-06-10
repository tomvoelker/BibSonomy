/*
 * Created on 10.06.2007
 */

package org.bibsonomy.database.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class SandboxDBSessionFactory extends DatabaseUtils implements DBSessionFactory {
	private static final Logger log = Logger.getLogger(SandboxDBSessionFactory.class);
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
			this.dbSessionProxy = (DBSession) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {DBSession.class}, new InvocationHandler() {
				
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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

	public void endTest() {
		if (this.realDbSession != null) {
			this.realDbSession.endTransaction();
			this.realDbSession.close();
		}
	}
}
