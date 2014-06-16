package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.msbib.MSBibDatabase;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.util.JabRefModelConverter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.AbstractPostExportRenderer;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.services.URLGenerator;
import org.w3c.dom.Document;

/**
 * Renderer for MSOfficeXML-Format
 * 
 * @author MarcelM
 */
public class JabrefMSOfficeXMLRenderer extends AbstractPostExportRenderer{
	
	private URLGenerator urlGenerator;
	
	public JabrefMSOfficeXMLRenderer(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
	
	@Override
	public void serializePosts(Writer writer,
			List<? extends Post<? extends Resource>> posts, ViewModel viewModel) throws InternServerException {
		
		//Check if the resource of every post is of type BibTex
		for (Post<? extends Resource> post : posts) {
			if (!(post.getResource() instanceof BibTex)) {
				this.handleUnsupportedMediaType();
				return;
			}
		}
		
		BibtexDatabase bibtexDB = JabRefModelConverter.bibtex2JabrefDB(posts, urlGenerator);
		MSBibDatabase msbibDB = new MSBibDatabase(bibtexDB);
		Document doc = msbibDB.getDOMrepresentation();
		
		try {
			writer.append(getStringFromDocument(doc));
			writer.flush();
		} catch (final LayoutRenderingException ex) {
			throw new InternServerException(ex);
		} catch (final IOException ex) {
			throw new InternServerException(ex);
		} catch (TransformerException ex) {
			throw new InternServerException(ex);
		}
			
	}
	
	public void append(Writer writer, List<Post<BibTex>> bibtexPosts) {
		BibtexDatabase bibtexDB = JabRefModelConverter.bibtex2JabrefDB(bibtexPosts, urlGenerator);
		MSBibDatabase msbibDB = new MSBibDatabase(bibtexDB);
		Document doc = msbibDB.getDOMrepresentation();
		
		try {
			writer.append(getStringFromDocument(doc));
			writer.flush();
		} catch (final LayoutRenderingException ex) {
			throw new InternServerException(ex);
		} catch (final IOException ex) {
			throw new InternServerException(ex);
		} catch (TransformerException ex) {
			throw new InternServerException(ex);
		}
	}
	
	
	@Override
	public void serializePost(Writer writer, Post<? extends Resource> post,
			ViewModel model) {
		this.serializePosts(writer, Collections.singletonList(post), model);
	}
	
	/**
	 * @throws TransformerException 
	 * This method converts a org.w3c.dom.Document to String
	 * @param doc
	 * @return
	 * @throws  
	 */
	private String getStringFromDocument(Document doc) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();
	} 

	@Override
	protected RenderingFormat getFormat() {
		return RenderingFormat.MSOFFICEXML;
	}

}
