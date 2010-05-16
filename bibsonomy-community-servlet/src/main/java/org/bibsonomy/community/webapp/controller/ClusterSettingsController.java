package org.bibsonomy.community.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.util.RequestWrapperContext;
import org.bibsonomy.model.Cluster;
import org.bibsonomy.model.Tag;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;


public class ClusterSettingsController extends AbstractBaseController {

	public ClusterSettingsController() {
		setCommandClass(ClusterViewCommand.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object commandObject, BindException errors)
			throws Exception {
		ClusterViewCommand<Tag> command = (ClusterViewCommand<Tag>)commandObject;
		initializeCommand(command, request);
		
		final RequestWrapperContext context = command.getContext();
		if( context.isUserLoggedIn() ) {
			populateClusterSettings(command, 10);
		}
		
		String outputFormat = getOutputFormat(command);
        return new ModelAndView("export/"+outputFormat+"/settings", "command", command);
	}

	private void populateClusterSettings(final ClusterViewCommand<Tag> command, final int nTags ) {
		String[] topics = {"politics", "computer", "medcine"};
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}
		
		Collection<Cluster<Tag>> clusters = new ArrayList<Cluster<Tag>>(topics.length); 
		for( int i=0; i<topics.length; i++ ) {
			Cluster<Tag> cluster = new Cluster<Tag>();
			cluster.setClusterID((int)Math.floor(Math.random()*42));
			Collection<Tag> tags = new LinkedList<Tag>(); 
			for( int j=0; j<nTags; j++ ) {
				Tag tag = new Tag(topics[i]+"_"+j);
				tag.setUsercount((int)Math.round(100*1.0/(j+1)));
				tags.add(tag);
			}
			cluster.setInstances(tags);
			cluster.setAnnotation(annotations.subList(i, i+1));
			clusters.add(cluster);
		}
		
		command.setClusters(clusters);
	}

	
}