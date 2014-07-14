package org.bibsonomy.layout.jabref.self;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.msbib.MSBibDatabase;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;
import org.w3c.dom.Document;

/**
 * a self 
 *
 * @author MarcelM
 */
public class MSOfficeXMLLayout extends SelfRenderingJabrefLayout{
	
	public static final String LAYOUTNAME = "msofficexml";
	
	/**
	 * @param name
	 */
	public MSOfficeXMLLayout() {
		super(LAYOUTNAME);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.layout.jabref.self.SelfRenderingJabrefLayout#render(net.sf.jabref.BibtexDatabase, java.util.List, org.bibsonomy.layout.jabref.JabrefLayout, boolean)
	 */
	@Override
	public StringBuffer render(BibtexDatabase database, List<BibtexEntry> sorted, JabrefLayout layout, boolean embeddedLayout) throws LayoutRenderingException {
		final MSBibDatabase msbibDB = new MSBibDatabase(database);
		final Document doc = msbibDB.getDOMrepresentation();
		
		final StringBuffer output = new StringBuffer();
		
		try {
			output.append(getStringFromDocument(doc));
		} catch (final TransformerException e) {
			throw new LayoutRenderingException(LAYOUTNAME);
		}
		
		return output;
	}
	
	/**
	 * This method converts a org.w3c.dom.Document to String
	 * @param doc
	 * @return
	 * @throws TransformerException
	 */
	private static String getStringFromDocument(Document doc) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();
	} 

}
