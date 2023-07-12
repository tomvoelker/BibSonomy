/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.webapp.command.ajax.OpenAccessCommand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author clemens
 */
@Getter
@Setter
public class OpenAccessController extends AjaxController implements MinimalisticController<OpenAccessCommand> {

	private static final String GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";
	private static final String DISSEMIN = "DISSEMIN";

	private DisseminController disseminController;
	private int maxQuerySize;

	@Override
	public View workOn(final OpenAccessCommand command) {
		// check if user is logged in
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		final String action = command.getAction();
		if (present(action)) {
			switch (action) {
				case GET_SENT_REPOSITORIES:
					final JSONObject responseJson = new JSONObject();
					final PostQueryBuilder postQueryBuilder = new PostQueryBuilder()
							.setGrouping(GroupingEntity.USER)
							.setGroupingName(command.getContext().getLoginUser().getName())
							.setScope(QueryScope.LOCAL)
							.setFilters(Sets.asSet(FilterEntity.POSTS_WITH_REPOSITORY))
							.entriesStartingAt(this.maxQuerySize, 0); // TODO retrieve all posts
					final List<Post<BibTex>> posts = logic.getPosts(postQueryBuilder.createPostQuery(BibTex.class));

					/*
					 * Iterate all retrieved posts and build a JSON document.
					 * The JSON document contains information about the post's intrahash, the repository-sent-date and the sender.
					 */
					final JSONObject jsonPosts = new JSONObject();
					for (final Post<BibTex> post : posts) {
						final JSONObject postJson = new JSONObject();
						final JSONArray repositoriesJson = new JSONArray();
						repositoriesJson.addAll(post.getRepositorys());
						postJson.put("repositories", repositoriesJson);
						postJson.put("sentBySelf", (command.getContext().getLoginUser().getName().equals(post.getUser().getName()) ? 1 : 0));
						postJson.put("intrahash", post.getResource().getIntraHash());
						jsonPosts.put(post.getResource().getIntraHash(), postJson);
					}

					responseJson.put("posts", jsonPosts);
					command.setResponseString(responseJson.toString());
					break;
				case DISSEMIN:
					Post<? extends Resource> post = logic.getPostDetails(command.getIntrahash(), command.getContext().getLoginUser().getName());
					Map<String, String> policy = this.disseminController.getPolicyForPost((Post<? extends BibTex>) post);
					command.setPolicy(policy);
					return Views.AJAX_DISSEMIN;
				default:
					break;
			}
		}

		return Views.AJAX_JSON;
	}

	@Override
	public OpenAccessCommand instantiateCommand() {
		return new OpenAccessCommand();
	}

}
