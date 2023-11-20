/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * shows all users of the system
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetUserListStrategy extends AbstractGetListStrategy<List<User>> {

	private String remoteUserId;
	private String identityProvider;
	private String identityProviderType;

	/**
	 * @param context
	 */
	public GetUserListStrategy(final Context context) {
		super(context);
		this.remoteUserId = context.getStringAttribute(RESTConfig.REMOTE_USER_ID, null);
		this.identityProvider = context.getStringAttribute(RESTConfig.IDENTITY_PROVIDER, null);
		// TODO ENUM?
		this.identityProviderType = context.getStringAttribute(RESTConfig.IDENTITY_PROVIDER_TYPE, "SAML");
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForUsers();
	}

	@Override
	protected List<User> getList() {
		if (this.remoteUserId != null && this.identityProvider != null) {
			// TODO enum
			if (this.identityProviderType.equals("SAML")) {
				final RemoteUserId remoteUserId = new SamlRemoteUserId(this.identityProvider, this.remoteUserId);
				String userName = this.getLogic().getUsernameByRemoteUserId(remoteUserId);
				User user = this.getAdminLogic().getUserDetails(userName);

				if (user != null) {
					return new ArrayList<User>(){{add(user);}};
				}
			}
			return new ArrayList<>();
		}

		return this.getLogic().getUsers(null, GroupingEntity.ALL, null, null, null, null, null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}

	@Override
	protected void render(final Writer writer, final List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());
	}

	@Override
	public String getContentType() {
		return "users";
	}
}