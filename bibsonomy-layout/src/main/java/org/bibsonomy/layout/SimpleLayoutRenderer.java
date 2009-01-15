package org.bibsonomy.layout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class SimpleLayoutRenderer extends AbstractLayoutRenderer {

	private static final Log log = LogFactory.getLog(SimpleLayoutRenderer.class);

	@Override
	protected <T extends Resource> void renderInternal(final String layout, final OutputStream outputStream, final List<Post<T>> posts) throws IOException {
		log.info("rendering internal with layout " + layout);
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		
//		setContentType("text/plain;charset=UTF-8");
//		generatesDownloadContent();
		
		for (final Post<? extends Resource> post : posts) {
			writer.write(post.getResource().getTitle() + "\n");
		}
		
		writer.close();
	}
}

