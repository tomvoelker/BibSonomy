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
package org.bibsonomy.webapp.command.opensocial;

import lombok.Getter;
import lombok.Setter;
import net.oauth.OAuthConsumer;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author fei
 */
@Setter
@Getter
public class OAuthCommand extends BaseCommand {
	public enum AuthorizeAction { Authorize, Deny };

	private String responseString;
	
	private String authorizeAction;
	
	private OAuthConsumer consumer;
	
	/** information about OAuth token and authorization */
	private OAuthEntry entry;
	
	/** consumer meta information */
	private String appTitle;
	/** consumer meta information */
	private String appDescription;
	/** consumer meta information */
	private String appIcon;
	/** consumer meta information */
	private String appThumbnail;
	/** call back URL */
	private String callBackUrl;
	
	private OAuthConsumerInfo consumerInfo;
	
	public KeyType[] getKeyTypes() {
		return KeyType.values();
	}

}
