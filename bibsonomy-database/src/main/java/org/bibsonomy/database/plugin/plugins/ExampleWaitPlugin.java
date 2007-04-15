package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Bookmark;

public class ExampleWaitPlugin extends AbstractDatabasePlugin {

	private final Runnable waitRunnable = new Runnable() {
		public void run() {
			try {
				// System.out.println("Start...");
				Thread.sleep(2000);
				// System.out.println("End.");
			} catch (final InterruptedException ex) {
				throw new RuntimeException("Couldn't sleep", ex);
			}
		}
	};

	@Override
	public Runnable onBookmarkCreate(Bookmark bookmark) {
		return this.waitRunnable;
	}

	@Override
	public Runnable onBookmarkUpdate(Bookmark bookmark, Bookmark oldBookmark) {
		return this.waitRunnable;
	}
}