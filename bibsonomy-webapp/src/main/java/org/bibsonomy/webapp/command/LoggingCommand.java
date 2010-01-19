package org.bibsonomy.webapp.command;

/**
 * Command for ajax requests from admin page
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class LoggingCommand extends AjaxCommand {
	
	/** user for which we want to add a group or mark as spammer */
	private String userName;


	/** path in dom, backwards */
	private String dompath;

	/** page url */
	private String pageurl;

	/** anchor title */
	private String atitle;

	/** anchor hyper-reference */
	private String ahref;

	/** referer */
	private String referer;

	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the dompath
	 */
	public String getDompath() {
		return this.dompath;
	}

	/**
	 * @param dompath the dompath to set
	 */
	public void setDompath(final String dompath) {
		this.dompath = dompath;
	}

	/**
	 * @return the pageurl
	 */
	public String getPageurl() {
		return this.pageurl;
	}

	/**
	 * @param pageurl the pageurl to set
	 */
	public void setPageurl(final String pageurl) {
		this.pageurl = pageurl;
	}

	/**
	 * @return the atitle
	 */
	public String getAtitle() {
		return this.atitle;
	}

	/**
	 * @param atitle the atitle to set
	 */
	public void setAtitle(final String atitle) {
		this.atitle = atitle;
	}

	/**
	 * @return the ahref
	 */
	public String getAhref() {
		return this.ahref;
	}

	/**
	 * @param ahref the ahref to set
	 */
	public void setAhref(final String ahref) {
		this.ahref = ahref;
	}

}