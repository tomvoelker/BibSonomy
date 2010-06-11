package org.bibsonomy.database.systemstags;

import org.bibsonomy.model.Tag;


/**
 * @author Andreas Koch
 * @version $Id$ 
 */
public interface SystemTag {
	
	public Tag getTag();
	
	public void setTag(Tag tag);
	
	public String getArgument();
	
	public void setArgument(String argument);
	
	public String getName();
	
	public void setName(String name);
}
