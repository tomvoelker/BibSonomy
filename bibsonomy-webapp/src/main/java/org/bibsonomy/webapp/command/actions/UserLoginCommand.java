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
package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author dzo
 */
@Getter
@Setter
public class UserLoginCommand extends BaseCommand implements Serializable {

	private static final long serialVersionUID = -8690852609913391454L;
	
	private String message;
	
	/**
	 * The name of the user which wants to login.
	 */
	private String username;
	
	/**
	 * The users password
	 */
	private String password;
	
	/**
	 *	The openID url of the user 
	 */
	private String openID;
	
	/** preselected tab */
 	private int selTab;
	
	/**
	 * URL to which the user wants to jump back after successful login.
	 */
	private String referer;

	/**
	 * Some pages need the user to login first, before they can be used.
	 * They can give the user a notice using this param. 
	 */
	private String notice;

	/**
	 * For users who want to stay logged in for longer time with a cookie
	 */
	private boolean rememberMe;

}
