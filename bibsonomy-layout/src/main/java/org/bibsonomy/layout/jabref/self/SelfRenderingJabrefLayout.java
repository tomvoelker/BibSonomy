package org.bibsonomy.layout.jabref.self;

import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;

/**
 * abstract class for self rendering jabref layouts
 *
 * @author MarcelM
 */
public abstract class SelfRenderingJabrefLayout extends JabrefLayout {

	/**
	 * @param name
	 */
	public SelfRenderingJabrefLayout(String name) {
		super(name);
	}
	
	public void init(JabrefLayout layout) {
		this.displayName = layout.getDisplayName();
		this.mimeType = layout.getMimeType();
		this.extension = layout.getExtension();
		this.description = layout.getDescription();
		this.publicLayout = layout.isPublicLayout();
	}
	
	public abstract StringBuffer render(final BibtexDatabase database, final List<BibtexEntry> sorted, final JabrefLayout layout, final boolean embeddedLayout) throws LayoutRenderingException;
}
