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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.webapp.command.actions.DiffPublicationCommand;
import org.bibsonomy.webapp.controller.PostHistoryController;
import org.bibsonomy.webapp.controller.ResourceListController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.impl.database.RecommenderStatisticsManager;

/**
 * TODO: check inheritance to ResourceListController
 * TODO: make generic
 * 
 * @author pba
 */
public class DiffPublicationController extends ResourceListController implements MinimalisticController<DiffPublicationCommand> {
	private static final Log LOGGER = LogFactory.getLog(PostHistoryController.class);

	@Override
	public DiffPublicationCommand instantiateCommand() {
		DiffPublicationCommand command = new DiffPublicationCommand();
		command.setPost(new Post<BibTex>());
		command.getPost().setResource(this.instantiateResource());
		command.setPostDiff(new Post<BibTex>());
		command.getPostDiff().setResource(this.instantiateResource());
		/*
		 * set default values.
		 */
		command.setPostID(RecommenderStatisticsManager.getUnknownEntityID());
		return command;
	}

	@Override
	public View workOn(DiffPublicationCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(format);
		final String requestedUser = command.getUser();
		// TODO: rename property. we do not update the post here
		final String intraHashToUpdate = command.getIntraHashToUpdate();
		
		/* TODO: why we have to set the post id for recommendation?
		 * this is the post we're working on for now ...
		 */
		final Post<BibTex> post = command.getPost();
		if (command.getPostID() == RecommenderStatisticsManager.getUnknownEntityID()) {
			command.setPostID(RecommenderStatisticsManager.getNewPID());
		}

		final int compareVersion = command.getCompareVersion();
		if (present(compareVersion) && present(intraHashToUpdate)) {
			LOGGER.debug("intra hash to diff found -> handling diff of existing post");
			final List<?> dbPosts = logic.getPosts(post.getResource().getClass(), GroupingEntity.ALL, command.getUser(), null, intraHashToUpdate, null, FilterEntity.POSTS_HISTORY_BIBTEX, null, null, null, compareVersion, compareVersion + 1);
			command.setPostDiff((Post<BibTex>) dbPosts.get(0));
		}
		command.setPost(getPostDetails(intraHashToUpdate, requestedUser));

		this.endTiming();
		return Views.DIFFPUBLICATIONPAGE;
	}
	

	protected BibTex instantiateResource() {
		return new BibTex();
	}
		
	/**
	 * The method {@link PostLogicInterface#getPostDetails(String, String)} throws
	 * an exception, if the post with the requested hash+user does not exist but 
	 * once existed and now has been moved. Since we just want to check, if the
	 * post with the given hash exists NOW, we can ignore that exception and 
	 * instead just return null.
	 * 
	 * @param intraHash
	 * @param userName
	 * @return
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostHashRedirect}
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostPublicationUmziehen#gel.C3.B6schte.2Fge.C3.A4nderte_Posts_.28Hash-Redirect-Problem.29}
	 */
	@SuppressWarnings("unchecked")
	protected Post<BibTex> getPostDetails(final String intraHash, final String userName) {
		try {
			return (Post<BibTex>) logic.getPostDetails(intraHash, userName);
		} catch (final ResourceMovedException e) {
			/*
			 * getPostDetails() has a redirect mechanism that checks for posts 
			 * in the log tables. If it find's a post with the given hash there,
			 * it throws an exception, giving the hash of the next post. We want
			 * to ignore this behavior, thus we ignore the exception
			 * 
			 * see https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostHashRedirect
			 * and https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostPublicationUmziehen#gel.C3.B6schte.2Fge.C3.A4nderte_Posts_.28Hash-Redirect-Problem.29
			 */
		}
		
		return null;
	}
}
