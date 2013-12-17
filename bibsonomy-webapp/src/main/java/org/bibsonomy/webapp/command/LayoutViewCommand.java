package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.command.actions.PublicationRendererCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.view.LayoutView;

/**
 * interface to use either {@link SimpleResourceViewCommand} or
 * {@link PublicationRendererCommand} in {@link LayoutView}.
 * 
 * @author jensi
  */
public interface LayoutViewCommand extends PublicationViewCommand {
	/**
	 * The context contains the loginUser, the ckey, and other things which can
	 * not be changed by the user.
	 * 
	 * @return The context.
	 */
	public RequestWrapperContext getContext();

	/**
	 * @return the layout (name that identifies a jabref layout)
	 */
	public String getLayout();

	/**
	 * @return the formatEmbedded
	 */
	public boolean getFormatEmbedded();
}
