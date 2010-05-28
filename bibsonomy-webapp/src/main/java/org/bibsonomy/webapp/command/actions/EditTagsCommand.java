package org.bibsonomy.webapp.command.actions;



/**
 * @author philipp
 * @version $Id$
 */
public class EditTagsCommand {
	
	private String delTags = "";
	
	private String addTags = "";
	
	private boolean updateRelations = false;

	/**
	 * @param delTags the delTags to set
	 */
	public void setDelTags(String delTags) {
		this.delTags = delTags;
	}

	/**
	 * @return the delTags
	 */
	public String getDelTags() {
		return delTags;
	}

	/**
	 * @param addTags the addTags to set
	 */
	public void setAddTags(String addTags) {
		this.addTags = addTags;
	}

	/**
	 * @return the addTags
	 */
	public String getAddTags() {
		return addTags;
	}

	/**
	 * @param updateRelations the updateRelations to set
	 */
	public void setUpdateRelations(boolean updateRelations) {
		this.updateRelations = updateRelations;
	}

	/**
	 * @return the updateRelations
	 */
	public boolean isUpdateRelations() {
		return updateRelations;
	}

}
