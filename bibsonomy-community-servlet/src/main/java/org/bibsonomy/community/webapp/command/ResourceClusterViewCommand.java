package org.bibsonomy.community.webapp.command;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;


@SuppressWarnings("unchecked")
public class ResourceClusterViewCommand extends SimpleResourceViewCommand {
	
	public static class ClusterFactory implements Factory {
		public Object create() {
			return new ResourceCluster();
		}
	}
	
	private static final Log log = LogFactory.getLog(ResourceClusterViewCommand.class);
	
	/** collection of clusters considered by this controller */
	// private Collection<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
	private Collection<ResourceCluster> clusters = (Collection<ResourceCluster>)LazyList.decorate(new ArrayList<ResourceCluster>(), new ClusterFactory());

	/** limit */
	private Integer limit;
	
	/** offset */
	private Integer offset;
	
	/** total number of clusters */
	private Integer total;
	
	/** action identifier */
	private String action;
	
	public ResourceClusterViewCommand() {
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setClusters(Collection<ResourceCluster> clusters) {
		this.clusters = clusters;
	}

	public Collection<ResourceCluster> getClusters() {
		return clusters;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getTotal() {
		return total;
	}

}
