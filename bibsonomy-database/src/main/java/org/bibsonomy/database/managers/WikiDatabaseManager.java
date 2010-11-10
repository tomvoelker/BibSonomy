package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.WikiParam;
import org.bibsonomy.model.Wiki;

/**
 * @author philipp
 * @version $Id$
 */
public class WikiDatabaseManager extends AbstractDatabaseManager {
    private static final Log log = LogFactory.getLog(WikiDatabaseManager.class);

    private static final WikiDatabaseManager singleton = new WikiDatabaseManager();

    /**
     * @return UserDatabaseManager
     */
    public static WikiDatabaseManager getInstance() {
	return singleton;
    }

    private WikiDatabaseManager() {
    }

    public Wiki getActualWiki(final String userName, final DBSession session) {
	return this.queryForObject("getActualWikiForUser", userName, Wiki.class, session);
    }

    public List<Date> getWikiVersions(String userName, final DBSession session) {
	return this.queryForList("getWikiVersionsForUser", userName, Date.class, session);
    }

    public void updateWiki(final String userName, final Wiki wiki, final DBSession session) {
	final WikiParam param = new WikiParam();
	param.setUserName(userName);
	param.setWikiText(wiki.getWikiText());
	param.setDate(new Date());
	
	this.update("updateWikiForUser", param, session);
    }

    public Wiki getPreviousWiki(String userName, Date date, DBSession session) {
	final WikiParam param = new WikiParam();

	param.setDate(date);
	param.setUserName(userName);

	return this.queryForObject("getLoggedWiki", param, Wiki.class, session);
    }

    public void createWiki(final String userName,final Wiki wiki, DBSession session) {
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

    public void logWiki(final String userName, final Wiki wiki, DBSession session) {
	session.beginTransaction();
	
	final WikiParam param = new WikiParam();
	param.setUserName(userName);
	param.setWikiText(wiki.getWikiText());
	param.setDate(new Date());
	
	try {
	    this.insert("logWiki",param, session);
	    session.commitTransaction();
	} finally {
	    session.endTransaction();
	}
    }
}
