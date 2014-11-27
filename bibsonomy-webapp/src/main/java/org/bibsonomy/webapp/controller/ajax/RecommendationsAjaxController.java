/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.webapp.command.ajax.AjaxRecommenderCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.core.RecommendationService;
import recommender.core.interfaces.renderer.RecommendationRenderer;

/**
 * Some common operations for recommendation tasks.
 * 
 * TODO: This is a candidate for refactoring/performance optimization:
 *       As in the post*controller, the post-command has to be filled -
 *       at least with grouping information, as private posts shouldn't
 *       be sent to remotely installed recommender
 *       
 * @param <R>
 *  
 * @author fei
 */
public abstract class RecommendationsAjaxController<R extends Resource> extends AjaxController implements MinimalisticController<AjaxRecommenderCommand<R>> {
	private static final Log log = LogFactory.getLog(RecommendationsAjaxController.class);

	/** this identifies spammer, which are flagged by an admin */
	private static final String USERSPAMALGORITHM = "admin";
	
	/** 
	 * To sort out spam posts, we need access to informations 
	 * from the spam detection framework 
	 */
	private LogicInterface adminLogic;
	
	/** the renderer for serialing the recommendation results */
	private RecommendationRenderer<Post<? extends Resource>, RecommendedTag> recommendationRenderer;
	
	/** default recommender for serving spammers */
	private RecommendationService<Post<? extends Resource>, RecommendedTag> spamTagRecommender;
	
	/**
	 * Provides tag recommendations to the user.
	 */
	private RecommendationService<Post<? extends Resource>, RecommendedTag> recommender;
	
	@Override
	public View workOn(final AjaxRecommenderCommand<R> command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * only users which are logged in get recommendations
		 */
		if (!context.isUserLoggedIn()) {
			command.setResponseString("");
			return Views.AJAX_JSON;
		}
		
		final User loginUser = context.getLoginUser();
		
		//------------------------------------------------------------------------
		// TODO: THIS IS AN ISSUE WE STILL HAVE TO DISCUSS:
		// During the ECML/PKDD recommender challenge, many recommender systems
		// couldn't deal with the high load, so we filter out those users, which
		// are flagged as spammer either by an admin, or by the framework for sure 
		// TODO: we could probably also filter out those users, which are 
		//       flagged as 'spammer unsure' 
		//------------------------------------------------------------------------
		final String loggedinUserName = loginUser.getName();
		final User dbUser = this.adminLogic.getUserDetails(loggedinUserName);

		/*
		 * set the user of the post to the loginUser (the recommender might need
		 * the user name)
		 */
		command.getPost().setUser(loginUser);

		/*
		 * initialize groups
		 */
		GroupingCommandUtils.initGroups(command, command.getPost().getGroups());
		
		// set postID for recommender
		command.getPost().setContentId(command.getPostID());

		if ((dbUser.isSpammer()) && (((dbUser.getPrediction() == null) && (dbUser.getAlgorithm() == null)) ||
					(dbUser.getPrediction().equals(1) || dbUser.getAlgorithm().equals(USERSPAMALGORITHM)))  ) {
			// the user is a spammer
			log.debug("Filtering out recommendation request from spammer");
			if (this.spamTagRecommender != null)	{
				final SortedSet<RecommendedTag> result = this.spamTagRecommender.getRecommendationsForUser(loggedinUserName, command.getPost());
				this.processRecommendedTags(command, result);
			} else {
				command.setResponseString("");
			}
		} else {
			/* the user doesn't seem to be a spammer
			 * get the recommended tags for the post from the normal recommender
			 */
			if (this.recommender != null) {
				final SortedSet<RecommendedTag> result = this.recommender.getRecommendationsForUser(loggedinUserName, command.getPost());
				this.processRecommendedTags(command, result);
			} else {
				command.setResponseString("");
			}
		}
		
		return Views.AJAX_JSON;
	}
	
	@Override
	public AjaxRecommenderCommand<R> instantiateCommand() {
		final AjaxRecommenderCommand<R> command = new AjaxRecommenderCommand<R>();
		/*
		 * initialize lists
		 * FIXME: is it really neccessary to initialize ALL those lists? Which are really needed?
		 */
		command.setRelevantGroups(new ArrayList<String>());
		command.setRelevantTagSets(new HashMap<String, Map<String, List<String>>>());
		command.setRecommendedTags(new TreeSet<RecommendedTag>());
		command.setCopytags(new ArrayList<Tag>());
		/*
		 * initialize post & resource
		 */
		command.setPost(new Post<R>());
		command.getPost().setResource(this.initResource());
		
		GroupingCommandUtils.initGroupingCommand(command);
		
		return command;
	}

	protected abstract R initResource();
	
	//------------------------------------------------------------------------
	// private helper functions
	//------------------------------------------------------------------------
	private void processRecommendedTags(final AjaxRecommenderCommand<R> command, final SortedSet<RecommendedTag> tags) {
		command.setRecommendedTags(tags);
		// TODO: renderer is thread safe? => constant
		final StringWriter sw = new StringWriter(100);
		this.recommendationRenderer.serializeRecommendationResultList(sw, command.getRecommendedTags());
		command.setResponseString(sw.toString());
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param recommendationRenderer the recommendationRenderer to set
	 */
	public void setRecommendationRenderer(
			RecommendationRenderer<Post<? extends Resource>, RecommendedTag> recommendationRenderer) {
		this.recommendationRenderer = recommendationRenderer;
	}

	/**
	 * @param spamTagRecommender the spamTagRecommender to set
	 */
	public void setSpamTagRecommender(
			RecommendationService<Post<? extends Resource>, RecommendedTag> spamTagRecommender) {
		this.spamTagRecommender = spamTagRecommender;
	}

	/**
	 * @param recommender the recommender to set
	 */
	public void setRecommender(
			RecommendationService<Post<? extends Resource>, RecommendedTag> recommender) {
		this.recommender = recommender;
	}
}
