package org.bibsonomy.wiki.tags;


public abstract class SharedResourceTag extends SharedTag {

	public SharedResourceTag(final String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected abstract String renderSharedTag(final RequestType requestType);
	
}
