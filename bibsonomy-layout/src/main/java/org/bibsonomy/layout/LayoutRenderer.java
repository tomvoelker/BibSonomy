package org.bibsonomy.layout;

import java.io.OutputStream;
import java.util.List;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Interface for basic layout rendering. 
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface LayoutRenderer<LAYOUT extends Layout> {

	/** Returns the requested layout. A layout may be user-specific, thus the name 
	 * of the login user must be given. 
	 *  
	 * @param layoutName
	 * @param loginUserName
	 * @return
	 * @throws IOException
	 */
	public LAYOUT getLayout(final String layoutName, final String loginUserName) throws LayoutRenderingException;

	/** Renders the given layout to the outputStream.
	 * 
	 * @param <T>
	 * @param layout
	 * @param posts
	 * @param outputStream
	 * @throws IOException
	 */
	public <T extends Resource> void renderLayout(final LAYOUT layout, final List<Post<T>> posts, final OutputStream outputStream) throws LayoutRenderingException;

	/** Checks, if the renderer supports the given resource type.
	 * 
	 * XXX: this could also be layout-dependent, i.e., we should not ask the
	 * renderer, but the layout ...
	 * 
	 * @param clazz
	 * @return
	 */
	public boolean supportsResourceType(final Class<? extends Resource> clazz);	
}

