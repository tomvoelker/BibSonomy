package net.sf.jabref.export.layout.format;

import net.sf.jabref.AuthorList;
import net.sf.jabref.AuthorList.Author;

public class MittelalterEditorLastFirst extends MittelalterEditorNamesFormatter {

	@Override
	public String format(String fieldText) {
		return getEditorsString(fieldText);
 
	}
	
	@Override
	protected String getPersonName(final Author a) {
		return a.getLastFirst(false);
	}

	@Override
	protected String getPersonNames(final AuthorList list) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
