/*
 * Created on Mar 29, 2003
 * 
 * @author henkel@cs.colorado.edu
 *  
 */
package bibtex.expansions;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexString;

/**
 * This expander will convert author/editor field values into BibtexPersonList
 * objects.
 * 
 * @author henkel
 */
public class PersonListExpander extends AbstractExpander implements Expander {

	/**
	 * Equivalent to PersonListExpander(expandAuthors,expandEditors,true).
	 * 
	 * @param expandAuthors
	 * @param expandEditors
	 */
	
	public PersonListExpander(boolean expandAuthors, boolean expandEditors) {
		this(expandAuthors, expandEditors, true);
	}

	/**
	 * @param expandAuthors
	 * @param expandEditors
	 * @param throwAllExpansionExceptions
	 *            Setting this to true means that all exceptions will be thrown
	 *            immediately. Otherwise, the expander will skip over things it
	 *            can't expand and you can use getExceptions to retrieve the
	 *            exceptions later
	 */
	public PersonListExpander(
		boolean expandAuthors,
		boolean expandEditors,
		boolean throwAllExpansionExceptions) {
		super(throwAllExpansionExceptions);
		this.expandAuthors = expandAuthors;
		this.expandEditors = expandEditors;
	}

	private final boolean expandAuthors, expandEditors;

	/**
	 * This method will expand all author and editor fields (if configured in
	 * the constructor) into BibtexPersonList values. Before you call this
	 * method, please make sure you have used the MacroReferenceExpander.
	 * 
	 * If you use the flag throwAllExpansionExceptions set to false, you can
	 * retrieve all the exceptions using getExceptions()
	 * 
	 * @param file
	 */
	public void expand(final BibtexFile file) throws ExpansionException {
		for (final BibtexAbstractEntry bibtexAbstractEntry : file.getEntries()) {
			if (!(bibtexAbstractEntry instanceof BibtexEntry))
				continue;
			final BibtexEntry entry = (BibtexEntry) bibtexAbstractEntry;
			if (expandAuthors && entry.getFieldValue("author") != null) {
				try {
					entry.setField(
						"author",
						BibtexPersonListParser.parse((BibtexString) entry.getFieldValue("author"),""+entry.getEntryKey()));
				} catch (final PersonListParserException e) {
					throwExpansionException(e);
				}
			}
			if (expandEditors && entry.getFieldValue("editor") != null) {
				try {
					entry.setField(
						"editor",
						BibtexPersonListParser.parse((BibtexString) entry.getFieldValue("editor"),""+entry.getEntryKey()));
				} catch (final PersonListParserException e) {
					throwExpansionException(e);
				}
			}
		}
		finishExpansion();
	}

}
