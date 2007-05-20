package org.bibsonomy.database.plugin;

import org.junit.Test;

public class DatabasePluginRegistryTest {

	/*
	 * Works with the ExampleWaitPlugin enabled.
	 */
	@Test// (timeout=1000)
	public void onBookmarkCreate() throws InterruptedException {
		// FIXME causes null pointer
		// DatabasePluginRegistry.getInstance().onBibTexInsert(-1, null);
		// Thread.sleep(2500);
	}
}