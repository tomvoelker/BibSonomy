package org.bibsonomy.model;

/**
 * @author philipp
 * @version $Id$
 */
public class Classification {

	/*
	 * name of classification (e.g. "ACM")
	 */
	private String name;

	/*
	 * long name or description of classification (e.g. "Association for Computing Machinery")
	 */
	private String desc;

	/*
	 * url for more informations
	 */
	private String url = "";
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
}
