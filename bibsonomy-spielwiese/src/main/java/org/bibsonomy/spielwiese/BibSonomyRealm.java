package org.bibsonomy.spielwiese;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A realm that accepts every username as long as the password is
 * <em>test123</em>. If you'd like to try it out do the following:
 * <ul>
 * <li>build a JAR and copy it to the <code>server/lib/</code> directory of
 * your Tomcat</li>
 * <li>deploy the webapp to your Tomcat, i.e. copy the file
 * <code>webapp-context.xml</code> to
 * <code>conf/Catalina/localhost/realmtest.xml</code> and adapt the
 * <code>docBase</code> parameter.</li>
 * </ul>
 * Now start the Tomcat and try to access the webapp <a
 * href="http://localhost:8080/realmtest/">http://localhost:8080/realmtest/</a>.
 * You should be promted to enter a username and passwort: type whatever
 * username you want and use <em>test123</em> for the password. This way you
 * should be able to see a page saying <em>Successfully logged in</em>. If
 * you try to enter another password the access to this page should be denied.
 * 
 * @author Christian Schenk
 */
public class BibSonomyRealm extends RealmBase {

	private final static Log log = LogFactory.getLog(BibSonomyRealm.class.getSimpleName());

	@Override
	protected String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String getPassword(final String username) {
		log.info("Method getPassword():  " + username);
		return "test123";
	}

	@Override
	protected Principal getPrincipal(final String username) {
		log.info("Method getPrincipal(): " + username);
		final List<String> roles = new ArrayList<String>();
		roles.add("tomcat");
		return new GenericPrincipal(this, username, "test123", roles);
	}
}