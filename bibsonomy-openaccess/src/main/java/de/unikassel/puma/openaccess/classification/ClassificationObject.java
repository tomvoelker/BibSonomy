package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;


public class ClassificationObject {

	private LinkedHashMap<String , ClassificationObject> children;
	
	private String name;

	private String description;
	
	public ClassificationObject(String name, String description) {
		this.name = name;
		this.description = description;

		children = new LinkedHashMap<String, ClassificationObject>();
	}
	
	public void addChild(String name, ClassificationObject co) {
		children.put(name, co);
	}
	
	public LinkedHashMap<String, ClassificationObject> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
