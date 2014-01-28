package org.bibsonomy.webapp.command.actions;



/**
 * @author philipp
 */
public class EditTagsCommand {
	
	// TODO: Set<Tag>
	private String delTags = "";
	
	// TODO: Set<Tag>
	private String addTags = "";
	
	private boolean updateRelations = false;

	/**
	 * @param delTags the delTags to set
	 */
	public void setDelTags(final String delTags) {
		this.delTags = delTags;
	}

	/**
	 * @return the delTags
	 */
	public String getDelTags() {
		return this.delTags;
	}

	/**
	 * @param addTags the addTags to set
	 */
	public void setAddTags(final String addTags) {
		this.addTags = addTags;
	}

	/**
	 * @return the addTags
	 */
	public String getAddTags() {
		return this.addTags;
	}

	/**
	 * @param updateRelations the updateRelations to set
	 */
	public void setUpdateRelations(final boolean updateRelations) {
		this.updateRelations = updateRelations;
	}

	/**
	 * @return the updateRelations
	 */
	public boolean isUpdateRelations() {
		return this.updateRelations;
	}

}
