package org.bibsonomy.community.importer.parser;

public interface DataInputParser<T> {
	public T parseString(String str);
}
