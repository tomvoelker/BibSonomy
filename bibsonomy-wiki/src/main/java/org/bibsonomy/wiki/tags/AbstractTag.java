package org.bibsonomy.wiki.tags;

import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.wiki.WikiUtil;


public abstract class AbstractTag extends HTMLTag implements INoBodyParsingTag  {
	
	protected LogicInterface logic;
	protected User requestedUser;

	public AbstractTag(String name) {
		super(name);
	}

	@Override
	public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
		final WikiUtil wikiUtil = (WikiUtil) model;
		this.logic = wikiUtil.getLogic();
		this.requestedUser = wikiUtil.getUser();
		buf.append(this.render());
	}
	
	protected abstract StringBuilder render();

}