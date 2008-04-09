package org.bibsonomy.database.plugin;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.junit.Before;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class AbstractDatabasePluginTest extends AbstractDatabaseManagerTest {

	protected DatabasePluginRegistry plugins;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.plugins = DatabasePluginRegistry.getInstance();
	}
}