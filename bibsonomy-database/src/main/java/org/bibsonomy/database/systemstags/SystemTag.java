package org.bibsonomy.database.systemstags;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
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
	
	private SystemTagFactory systemTagFactory;
	
	
	/**
	 * @return the systemTagFactory
	 */
	public SystemTagFactory getSystemTagFactory() {
		return this.systemTagFactory;
	}

	/**
	 * @param systemTagFactory the systemTagFactory to set
	 */
	public void setSystemTagFactory(SystemTagFactory systemTagFactory) {
		this.systemTagFactory = systemTagFactory;
	}

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
	public SystemTag(final String name) {
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
	 * action to perform before the creation of a post
	 * 
	 * @param <T> Resource Type
	 * @param post a VALID post for which action should be performed
	 * @param session action's database session 
	 */
	public abstract <T extends Resource> void performBeforeCreate(Post<T> post, final DBSession session);

	/**
	 * action to perform before the update action
	 * 
	 * @param <T> Resource Type
	 * @param oldPost post for which action should be performed: If operation is not UPDATE_TAGS the post MUST be VALID
	 * @param operation the UpdateOperation during which the method is called
	 * @param session action's database session 
	 * @param newPost TODO
	 */
	public abstract <T extends Resource> void performBeforeUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session);

	/**
	 * action to perform after the creation of a post
	 * 
	 * @param <T> Resource Type
	 * @param post VALID post for which action should be performed
	 * @param session action's database session 
	 */
	public abstract <T extends Resource> void performAfterCreate(Post<T> post, final DBSession session);
	
	/**
	 * action to perform after the update action
	 * 
	 * @param <T> Resource Type
	 * @param newPost 
	 * @param oldPost 
	 * @param operation the UpdateOperation during which the method is called
	 * @param session action's database session 
	 */
	public abstract <T extends Resource> void performAfterUpdate(Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session);

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
		this.tag = tag;
		
		// extract argument
		setValue(SystemTagsUtil.extractArgument(tag.getName()));
		setName(SystemTagsUtil.extractName(tag.getName()));
	}

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
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the logicInterface
	 */
	public LogicInterface getLogicInterface() {
		return this.logicInterface;
	}

	/**
	 * @param logicInterface the logicInterface to set
	 */
	public void setLogicInterface(LogicInterface logicInterface) {
		this.logicInterface = logicInterface;
	}

	/**
	 * @return the dbSessionFactory
	 */
	public DBSessionFactory getDbSessionFactory() {
		return this.dbSessionFactory;
	}

	/**
	 * @param dbSessionFactory the dbSessionFactory to set
	 */
	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return this.tag;
	} 
}
