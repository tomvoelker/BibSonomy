package org.bibsonomy.community.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.topics.search.TopicQuery;
import org.bibsonomy.topics.search.TopicSearcher;
import org.springframework.web.servlet.ModelAndView;

import weka.core.Instance;

/**
 * controller for retrieving a ranking based on a topic query
 * 
 * @author andi
 *
 */
public class TopicQueryController extends AbstractBaseController<ResourceClusterViewCommand> {

	@SuppressWarnings("unused")
	private final static Log log = LogFactory.getLog(TopicQueryController.class);

	// number of resources to query for
	private static final Integer RESOURCELIMIT = 100;
	
	/** searcher for kdtree models */
	private TopicSearcher topicSearcher;
	
	public TopicQueryController() {
		setCommandClass(ResourceClusterViewCommand.class);
		topicSearcher = TopicSearcher.getInstance();
	}

	//------------------------------------------------------------------------
	// controller interface
	//------------------------------------------------------------------------
	@Override
	protected ResourceClusterViewCommand instantiateCommand() {
		return new ResourceClusterViewCommand();
	}

	@Override
	public ModelAndView workOn(ResourceClusterViewCommand command) {
		
		if (command.getAction() != null && command.getAction().equals("LOADMODEL")) {
			int modelId = command.getOffset();
			topicSearcher = TopicSearcher.getInstance(modelId);
			
			// return result for error page
			ResourceCluster cluster = new ResourceCluster();
			cluster.setClusterID(-1);
			cluster.setBibtex(new LinkedList<Post<BibTex>>());
			cluster.setBookmark(new LinkedList<Post<Bookmark>>());
			Collection<ResourceCluster> clusters = new ArrayList<ResourceCluster>();
			clusters.add(cluster);
			command.setClusters(clusters);
			populateResourcesForError(command);
			return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
		}
		
		// check for loaded model
		if (!topicSearcher.isReady()) {
			// return result for error page
			ResourceCluster cluster = new ResourceCluster();
			cluster.setClusterID(-1);
			cluster.setBibtex(new LinkedList<Post<BibTex>>());
			cluster.setBookmark(new LinkedList<Post<Bookmark>>());
			Collection<ResourceCluster> clusters = new ArrayList<ResourceCluster>();
			clusters.add(cluster);
			command.setClusters(clusters);
			populateResourcesForError(command);
			return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
		}
		
		command.setLimit(RESOURCELIMIT);
		command.setOffset(0);
		command.setTotal(RESOURCELIMIT);

		TopicQuery query = new TopicQuery(command.getClusters(), command.getTotal());
		try {
			topicSearcher.search(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashSet<Integer> clusterIds = new HashSet<Integer>();
		for(ResourceCluster cl: command.getClusters()) {
			clusterIds.add(cl.getClusterID());
		}
		
		double[] bibDist = query.getBibtexDistances();
		double[] bookDist = query.getBookmarkDistances();
		
		// assign search results by cluster
		List<Post<BibTex>> bib;
		List<Post<Bookmark>> book;
		Instance inst;
		for(ResourceCluster cl: command.getClusters()) {
			bib = new LinkedList<Post<BibTex>>();
			book = new LinkedList<Post<Bookmark>>();
			
			int i = 0;
			for (Post<BibTex> p: query.getBibtexPosts()) {
				
				if (p == null) {
					i++;
					continue;
				}
				
				// get instance
				inst = query.getBibtexInstance(i);
				
				double max = -1;
				int maxId = -1;
				for (int id: clusterIds) {
					// +1 because contentId is stored at position 0
					// but we don't have to change it because
					// community servlet starts from 1 as well
					if (inst.value(id) > max) {
						max = inst.value(id);
						maxId = id;
					}
				}
					
				if (maxId == cl.getClusterID()) {
					p.setWeight(1-bibDist[i]);
					bib.add(p);
				}
				
				i++;
			}
			cl.setBibtex(bib);
			
			i = 0;
			for (Post<Bookmark> p: query.getBookmarkPosts()) {
				
				if (p == null) {
					i++;
					continue;
				}
				
				// get instance
				inst = query.getBookmarkInstance(i);
				
				double max = -1;
				int maxId = -1;
				for (int id: clusterIds) {
					// +1 because contentId is stored at position 0
					// but we don't have to change it because
					// community servlet starts from 1 as well
					if (inst.value(id) > max) {
						max = inst.value(id);
						maxId = id;
					}
				}
					
				if (maxId == cl.getClusterID()) {
					p.setWeight(1-bookDist[i]);
					book.add(p);
				}
				
				i++;
			}
			cl.setBookmark(book);
				
		}

		populateResources(command, query.getBibtexPosts(), query.getBookmarkPosts(), 50);
		
		return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
	}
	
	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private void populateResources(final ResourceClusterViewCommand resources, final List<Post<BibTex>> bibTexPosts, final List<Post<Bookmark>> bookmarkPosts, final int entriesPerPage) {
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);
	
		bookmarks.setList(bookmarkPosts);
		bookmarks.setEntriesPerPage(entriesPerPage);
		bibTex.setList(bibTexPosts);
		bibTex.setEntriesPerPage(entriesPerPage);

		resources.setBibtex(bibTex);
		resources.setBookmark(bookmarks);
	}

	private void populateResourcesForError(final ResourceClusterViewCommand resources) {
		
		final List<Post<BibTex>> bibTexPosts = new LinkedList<Post<BibTex>>();
		final List<Post<Bookmark>> bookmarkPosts = new LinkedList<Post<Bookmark>>();
		
		final int entriesPerPage = 50;
		
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);
	
		bookmarks.setList(bookmarkPosts);
		bookmarks.setEntriesPerPage(entriesPerPage);
		bibTex.setList(bibTexPosts);
		bibTex.setEntriesPerPage(entriesPerPage);

		resources.setBibtex(bibTex);
		resources.setBookmark(bookmarks);
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	
	public TopicSearcher getTopicSearcher() {
		return topicSearcher;
	}

	public void setTopicSearcher(TopicSearcher topicSearcher) {
		this.topicSearcher = topicSearcher;
	}

}