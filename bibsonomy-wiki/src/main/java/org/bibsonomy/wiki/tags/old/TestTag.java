package org.bibsonomy.wiki.tags.old;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.AbstractTag;

public class TestTag extends AbstractTag{
	private static final String NAME = "name";
	public static final String TAG_NAME = "test";
	public TestTag() {
		super(TAG_NAME);
		// TODO Auto-generated constructor stub
	}
	
	protected StringBuilder render() {
		final String tagName = this.getAttributes().get(NAME);
		final StringBuilder renderedHTML = new StringBuilder();
		
        if (!present(tagName)) {
        	return renderedHTML;
        }
        
        renderedHTML.append("test");
        return renderedHTML;
		
	}

}
