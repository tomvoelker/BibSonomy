package net.sf.jabref.export.layout.format;

import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;

public class MittelalterEditorFirstLast extends MittelalterEditorNamesFormatter {

	@Override
	public String format(String fieldText) {
		
		return getEditorsString(fieldText);
	}
	
	@Override
	protected String getPersonName(Author a) {
		return a.getFirstLast(false);
	}
	
	@Override
	protected String getPersonNames(AuthorList list) {
		return list.getAuthorsFirstFirst(false, false);
	}
	
}
