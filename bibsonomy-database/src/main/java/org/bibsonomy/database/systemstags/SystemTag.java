package org.bibsonomy.database.systemstags;

import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.DBSessionFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author Andreas Koch
 * @version $Id$ 
 */
public abstract class SystemTag {
	private String name;
	private String value;
	/**
	 * Raw tag is given by user
	 */
	private Tag tag;
	/**
	 * Interface for database access
	 */
	private LogicInterface logicInterface;
	/**
	 * Factory for creating new database sessions. 
	 * (Needed when posts for different users are created (e.g. for:group))
	 */
	private DBSessionFactory dbSessionFactory;
	
	/**
	 * default constructor for creating empty instances (e.g. for spring configuration)
	 */
	public SystemTag() {
		this(null, null);
	}	

	/**
	 * used if no start value for the system tag is given
	 * 
	 * @param name
	 *            of the system tag
	 */
	public SystemTag(String name) {
		this(name, null);
	}

	/**
	 * 
	 * @param name
	 *            of the system tag
	 * @param value
	 *            of the system tag
	 */
	public SystemTag(String name, String value) {
		this.setName(name);
		this.value = value;
	}

	/**
	 * action to perform before the update/delete action
	 * 
	 * @param <T> Resource Type
	 * @param post post for which action should be performed
	 * @param session action's database session 
	 */
	public abstract <T extends Resource> void performBefore(Post<T> post, final DBSession session);

	/**
	 * action to perform after the update/delete action
	 * 
	 * @param <T> Resource Type
	 * @param post post for which action should be performed
	 * @param session action's database session 
	 */
	public abstract <T extends Resource> void performAfter(Post<T> post, final DBSession session);

	/**
	 * Factory for creating new instances.
	 * 
	 * @return new instance
	 */
	public abstract SystemTag newInstance();

	/**
	 * Sets this instance's tag input representation and extracts tag's argument.
	 * Precondition:
	 *   Given tag is a system tag.
	 * @param tag as given by user
	 */
	public void setTag(final Tag tag) {
//		assert(SystemTagFactory.isSystemTag(tag.getName()));
		this.tag = tag;
		
		// extract argument
		setValue(SystemTagsUtil.extractArgument(tag.getName()));
		setName(SystemTagsUtil.extractName(tag.getName()));
	} 
	

	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public Tag getTag() {
		return this.tag;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLogicInterface(LogicInterface logicInterface) {
		this.logicInterface = logicInterface;
	}

	public LogicInterface getLogicInterface() {
		return logicInterface;
	}

	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	public DBSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}
}
