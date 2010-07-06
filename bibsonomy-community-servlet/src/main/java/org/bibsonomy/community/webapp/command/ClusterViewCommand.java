package org.bibsonomy.community.webapp.command;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.Cluster;


@SuppressWarnings("unchecked")
public class ClusterViewCommand<T> extends SimpleResourceViewCommand {
	
	public static class ClusterFactory<T> implements Factory {
		public Object create() {
			return new Cluster<T>();
		}
	}
	
	private static final Log log = LogFactory.getLog(ClusterViewCommand.class);
	
	/** collection of clusters considered by this controller */
	// private Collection<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
	private Collection<Cluster<T>> clusters = (Collection<Cluster<T>>)LazyList.decorate(new ArrayList<Cluster<T>>(), new ClusterFactory<T>());

	public ClusterViewCommand() {
		log.info("Object created...");
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setClusters(Collection<Cluster<T>> clusters) {
		this.clusters = clusters;
	}

	public Collection<Cluster<T>> getClusters() {
		return clusters;
	}

}
