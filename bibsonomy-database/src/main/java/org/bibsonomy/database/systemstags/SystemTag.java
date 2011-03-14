package org.bibsonomy.database.systemstags;

/**
 * @author Andreas Koch
 * @version $Id$ 
 */
public interface SystemTag {
	
	/**
	 * @return the argument
	 */
	public String getArgument();
	
	/**
	 * @param argument the argument to set
	 */
	public void setArgument(String argument);
	
	/**
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Returns true if the tagName belongs to an instance of the SystemTag
	 * @param tagName
	 * @return
	 */
	public boolean isInstance(String tagName);
	
	/**
	 * Returns true if it should be hidden from tag clouds and posts
	 * @return
	 */
	public boolean isToHide();
}
