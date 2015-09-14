package org.bibsonomy.index;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.util.SimpleBlockingThreadPoolExecutor;

/**
 * Schedules new index update tasks but guarantees that there is only one task in execution
 *
 * @author jensi
 */
public class IndexUpdateTrigger {
	private static final Log log = LogFactory.getLog(IndexUpdateTrigger.class);
	private final LuceneResourceManager<?> updateManager;
	private final SimpleBlockingThreadPoolExecutor<IndexUpdateJob> execService; 
	
	/**
	 * construct it!
	 * @param updateManager
	 */
	public IndexUpdateTrigger(LuceneResourceManager<?> updateManager) {
		this.updateManager = updateManager;
		this.execService = new SimpleBlockingThreadPoolExecutor<>(updateManager.getResourceName() + "Updater");
	}
	
	private class IndexUpdateJob implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			updateManager.updateAndReloadIndex();
			return null;
		}
	}
	
	/**
	 * Schedules new index update task if necessary. Returns immediately in all cases.
	 */
	public void triggerUpdate() {
		if (log.isDebugEnabled()) {
			log.debug(this.execService.toString());
		}
		if (this.execService.getWaitingTasks().size() > 0) {
			// a new update process is already waiting to be executed -> no need to schedule a new one 
			return;
		}
		this.execService.scheduleTaskForExecution(new IndexUpdateJob());
	}
}
