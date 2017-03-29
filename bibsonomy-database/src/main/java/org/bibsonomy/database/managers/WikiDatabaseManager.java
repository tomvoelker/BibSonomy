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
package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.WikiParam;
import org.bibsonomy.model.Wiki;

/**
 * This class represents the layer between BibSonomy and the database
 * concerning all requests about the CVWiki.
 * 
 * @author philipp
 */
public class WikiDatabaseManager extends AbstractDatabaseManager {
    private static final WikiDatabaseManager singleton = new WikiDatabaseManager();

    /**
     * Returns a singleton object for this database manager.
     * @return UserDatabaseManager
     */
    public static WikiDatabaseManager getInstance() {
    	return singleton;
    }

    private WikiDatabaseManager() {
    }

    /**
     * return the current wiki for a specified user.
     * @param userName the name of the user
     * @param session the current dbsession which is to retrieve the corresponding wiki.
     * @return the current wiki for the specified user
     */
    public Wiki getCurrentWiki(final String userName, final DBSession session) {
    	return this.queryForObject("getCurrentWikiForUser", userName, Wiki.class, session);
    }

    /**
     * updates the wiki for the specified user
     * @param userName the user for whom the wiki is updated.
     * @param wiki the new wiki which is to be inserted in the database.
     * @param session the requesting session.
     */
    public void updateWiki(final String userName, final Wiki wiki, final DBSession session) {
		final WikiParam param = new WikiParam();
		param.setUserName(userName);
		param.setWikiText(wiki.getWikiText());
		param.setDate(new Date());
		
		this.update("updateWikiForUser", param, session);
    }

    /**
     * creates a new wiki for the specified user
     * @param userName the name of the user who we want to create the wiki for.
     * @param wiki an object containing all the necessary information to be put in the db.
     * @param session the session which is to submit the wiki in the database.
     */
    public void createWiki(final String userName, final Wiki wiki, final DBSession session) {
		session.beginTransaction();
		
		final WikiParam param = new WikiParam();
		param.setUserName(userName);
		param.setWikiText(wiki.getWikiText());
		param.setDate(new Date());
		
		try {
		    this.insert("insertWiki", param, session);
		    session.commitTransaction();
		} finally {
		    session.endTransaction();
		}
    }

    /**
     * This method is never called until yet.
     * 
     * returns a list of saved wiki versions, described by the saving date.
     * 
     * @param userName the userName for whom the versions shall be received.
     * @param session the requesting session.
     * @return all wiki versions (dates) for the specified user
     */
    public List<Date> getWikiVersions(final String userName, final DBSession session) {
    	return this.queryForList("getWikiVersionsForUser", userName, Date.class, session);
    }

    /**
     * This method is never called until yet. See DBLogic.getWiki for this.
     * 
     * This method retrieves the latest wiki before the date parameter.
     * 
     * @param userName the user for whom this wiki is requested.
     * @param date the date where we want the latest wiki before.
     * @param session the requesting session.
     * @return the wiki version specified by the date for the specified user
     */
    public Wiki getPreviousWiki(final String userName, final Date date, final DBSession session) {
		final WikiParam param = new WikiParam();
	
		param.setDate(date);
		param.setUserName(userName);
	
		return this.queryForObject("getLoggedWiki", param, Wiki.class, session);
    }
    
    /**
     * This method is never called until yet.
     * 
     * logs the wiki of the user, so that he can recall older revisions. Wiki style!
     * 
     * @param userName
     * @param wiki
     * @param session
     */
    public void logWiki(final String userName, final Wiki wiki, final DBSession session) {
		session.beginTransaction();
		
		final WikiParam param = new WikiParam();
		param.setUserName(userName);
		param.setWikiText(wiki.getWikiText());
		/*
		 * FIXME: shouldn't we have an (additional) logging date here?
		 * 
		 * I.e., 
		 * date = wiki.getDate()
		 * logDate = new Date()
		 * 
		 * Nope, because we are inserting a new Wiki into the database. We will not change
		 * old Wikis.
		 */
		param.setDate(new Date());
		
		try {
		    this.insert("logWiki", param, session);
		    session.commitTransaction();
		} finally {
		    session.endTransaction();
		}
    }

}
