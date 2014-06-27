package org.bibsonomy.layout.jabref.self;

import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;

/**
 * TODO: add documentation to this class
 *
 * @author MarcelM
 */
public abstract class SelfRenderingJabrefLayout extends JabrefLayout{

	/**
	 * @param name
	 */
	public SelfRenderingJabrefLayout(String name) {
		super(name);
	}
	
	public abstract StringBuffer render(final BibtexDatabase database, final List<BibtexEntry> sorted, final JabrefLayout layout, final boolean embeddedLayout) throws LayoutRenderingException;
	
}
