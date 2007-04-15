package org.bibsonomy.database.plugin;

import org.junit.Test;

public class DatabasePluginRegistryTest {

	/*
	 * Works with the ExampleWaitPlugin enabled.
	 */
	@Test// (timeout=1000)
	public void onBookmarkCreate() throws InterruptedException {
		DatabasePluginRegistry.getInstance().onBookmarkCreate(null);
		Thread.sleep(2500);
	}
}