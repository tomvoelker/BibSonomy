package org.bibsonomy.webapp.command;

/**
 * @author fba
 * @version $Id$
 */
public class PostCommand extends ResourceViewCommand {
	private TagCloudCommand tagcloud = new TagCloudCommand();
	private ConceptsCommand concepts = new ConceptsCommand();

	@Override
	public TagCloudCommand getTagcloud() {
		return this.tagcloud;
	}

	@Override
	public void setTagcloud(final TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(final ConceptsCommand concepts) {
		this.concepts = concepts;
	}
}
