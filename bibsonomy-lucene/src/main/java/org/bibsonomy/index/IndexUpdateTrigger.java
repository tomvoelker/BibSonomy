/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
