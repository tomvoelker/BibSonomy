package org.bibsonomy.database.plugin.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.DBSession;

/**
 * This plugin is just an example to demonstrate the usage of the database
 * plugin facility: it just returns a Runnable that'll wait for some seconds.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class ExampleWaitPlugin extends AbstractDatabasePlugin {
	private static final Log log = LogFactory.getLog(ExampleWaitPlugin.class);

	private final Runnable waitRunnable = new Runnable() {
		public void run() {
			try {
				log.debug("Start...");
				Thread.sleep(2000);
				log.debug("End.");
			} catch (final InterruptedException ex) {
				throw new RuntimeException("Couldn't sleep", ex);
			}
		}
	};

	@Override
	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		return this.waitRunnable;
	}

	@Override
	public Runnable onBibTexUpdate(final int oldContentId, final int newContentId, final DBSession session) {
		return this.waitRunnable;
	}
}