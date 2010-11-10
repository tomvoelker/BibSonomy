package org.bibsonomy.database.params;

import java.util.Date;

/**
 * @author philipp
 * @version $Id$
 */
public class WikiParam {
    
    private String userName;
    
    private String wikiText;
    
    private Date date;

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * @param wiki the wiki to set
     */
    public void setWikiText(String wikiText) {
	this.wikiText = wikiText;
    }

    /**
     * @return the wiki
     */
    public String getWikiText() {
	return wikiText;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
	this.date = date;
    }

    /**
     * @return the date
     */
    public Date getDate() {
	return date;
    }

}
