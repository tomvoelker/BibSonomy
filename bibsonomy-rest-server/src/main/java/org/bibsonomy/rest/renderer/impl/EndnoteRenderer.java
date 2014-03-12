package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.EndnoteUtils;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.renderer.AbstractPostExportRenderer;
import org.bibsonomy.rest.renderer.RenderingFormat;

/**
 * Simple Renderer for EndNote-Format
 *
 * @author MarcelM
 */
public class EndnoteRenderer extends AbstractPostExportRenderer {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.AbstractPostExportRenderer#getFormat()
	 */
	@Override
	protected RenderingFormat getFormat() {
		return RenderingFormat.ENDNOTE;
	}
	
	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		// TODO see super class
	}
	
	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		serializePosts(writer, Collections.singletonList(post), model);
	}

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		for (Post<? extends Resource> post : posts) {
			// check if the resource of post is of type BibTex
			if (post.getResource() instanceof BibTex) {
				@SuppressWarnings("unchecked")
				Post<BibTex> bibtex = (Post<BibTex>)post;
				try {
					writer.append(EndnoteUtils.toEndnoteString(bibtex,true));
					writer.flush();
				}  catch (final LayoutRenderingException ex) {
					throw new InternServerException(ex);
				} catch (final IOException ex) {
					throw new InternServerException(ex);
				}
			}
			else {
				// FIXME : throw Proper Exception
				throw new UnsupportedOperationException();
			}
		}
	}
}
