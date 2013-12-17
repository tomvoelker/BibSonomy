package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author philipp
  */
public class ClassificationObject {

	private final Map<String , ClassificationObject> children;
	
	private String name;

	private String description;
	
	/**
	 * constructor for setting name and description
	 * @param name	the name
	 * @param description the description
	 */
	public ClassificationObject(final String name, final String description) {
		this.name = name;
		this.description = description;

		this.children = new LinkedHashMap<String, ClassificationObject>();
	}
	
	/**
	 * adds a child 
	 * @param name the name of the child
	 * @param co the child
	 */
	public void addChild(final String name, final ClassificationObject co) {
		this.children.put(name, co);
	}
	
	/**
	 * @return the children
	 */
	public Map<String, ClassificationObject> getChildren() {
		return this.children;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
