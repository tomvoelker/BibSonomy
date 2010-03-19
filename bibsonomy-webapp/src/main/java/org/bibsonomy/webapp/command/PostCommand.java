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
	public void setTagcloud(TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

}
