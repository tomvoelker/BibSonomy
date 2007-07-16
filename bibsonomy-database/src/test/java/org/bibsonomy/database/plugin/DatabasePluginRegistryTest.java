package org.bibsonomy.database.plugin;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
@Ignore
public class DatabasePluginRegistryTest {

	/*
	 * Works with the ExampleWaitPlugin enabled.
	 */
	@Test // (timeout=1000)
	public void onBookmarkCreate() {
		// FIXME needs IoC-Framework to test this properly
		// DatabasePluginRegistry.getInstance().onBibTexInsert(-1, null);
		// Thread.sleep(2500);
	}
}