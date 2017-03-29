/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * This is a temporary logic interface factory to enable logic interface access
 * from within BibSonomy 1 without having to re-do authentication
 * 
 * XXX: Please remove this class once this is not necessary anymore.
 * 
 * dbe, 20071203
 * tni: TEMPORARY???
 * 
 * @author Dominik Benz
 */
public class DBLogicNoAuthInterfaceFactory extends AbstractDBLogicInterfaceFactory {
	
	@Override
	public LogicInterface getLogicAccess(final String loginName, final String password) {
		if (loginName != null) {
			/*
			 * In this case we don't fill the user object completely, but set
			 * it's name such that the user is seen as logged in (users which
			 * are not logged in cause a user object with empty name).
			 */
			return new DBLogic(new User(loginName), this.getDbSessionFactory(), this.bibtexReader);
		}
		// guest access
		return new DBLogic(new User(), this.getDbSessionFactory(), this.bibtexReader);
	}

}