package org.bibsonomy.wiki.tags;

import java.io.IOException;

import org.bibsonomy.wiki.WikiUtil;

import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;


public abstract class AbstractTag extends HTMLTag implements INoBodyParsingTag  {
	
	protected WikiUtil wikiUtil;

	public AbstractTag(String name) {
		super(name);
	}

	@Override
	public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
		wikiUtil = (WikiUtil) model;
		buf.append(render());
	}
	
	abstract StringBuffer render();

}