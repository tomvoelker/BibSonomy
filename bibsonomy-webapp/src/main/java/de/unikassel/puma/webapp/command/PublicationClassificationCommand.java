package de.unikassel.puma.webapp.command;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationCommand extends AjaxCommand {

	private String classificationName = "";
	private String id = "";
	private String hash = "";
	private String key = "";
	private String value = "";
	
	/**
	 * @param name
	 */
	public void setClassificationName(String name) {
		this.classificationName = name;
	}
	
	/**
	 * @return  classification name
	 */
	public String getClassificationName() {
		return this.classificationName;
	}
	
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param intrahash the intrahash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the intrahash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
