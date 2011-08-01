package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.WikiParam;
import org.bibsonomy.model.Wiki;

/**
 * TODO: tests are missing
 * 
 * @author philipp
 * @version $Id$
 */
public class WikiDatabaseManager extends AbstractDatabaseManager {
    private static final WikiDatabaseManager singleton = new WikiDatabaseManager();

    /**
     * @return UserDatabaseManager
     */
    public static WikiDatabaseManager getInstance() {
	return singleton;
    }

    private WikiDatabaseManager() {
    }

    /**
     * 
     * @param userName
     * @param session
     * @return the current wiki for the specified user
     */
    public Wiki getActualWiki(final String userName, final DBSession session) {
	return this.queryForObject("getActualWikiForUser", userName, Wiki.class, session);
    }

    /**
     * 
     * @param userName
     * @param session
     * @return all wiki versions (dates) for the specified user
     */
    public List<Date> getWikiVersions(final String userName, final DBSession session) {
	return this.queryForList("getWikiVersionsForUser", userName, Date.class, session);
    }

    /**
     * updates the wiki for the specified user
     * @param userName
     * @param wiki
     * @param session
     */
    public void updateWiki(final String userName, final Wiki wiki, final DBSession session) {
	final WikiParam param = new WikiParam();
	param.setUserName(userName);
	param.setWikiText(wiki.getWikiText());
	param.setDate(new Date());
	
	this.update("updateWikiForUser", param, session);
    }

    /**
     * @param userName
     * @param date
     * @param session
     * @return the wiki version specified by the date for the specified user
     */
    public Wiki getPreviousWiki(final String userName, final Date date, final DBSession session) {
	final WikiParam param = new WikiParam();

	param.setDate(date);
	param.setUserName(userName);

	return this.queryForObject("getLoggedWiki", param, Wiki.class, session);
    }
    
    /**
     * creates a new wiki for the specified user
     * @param userName
     * @param wiki
     * @param session
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
     * logs the wiki of the user
     * TODO: move to logging plugin
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
