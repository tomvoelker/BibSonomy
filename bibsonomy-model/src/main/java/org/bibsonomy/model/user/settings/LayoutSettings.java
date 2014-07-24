package org.bibsonomy.model.user.settings;

import org.bibsonomy.webapp.view.constants.ViewLayout;

/**
 * Compined settings influencing the layout viewed to a user
 *
 * @author jil
 */
public class LayoutSettings {
	/** 
	 * are advanced buttons & ui options hidden from the user?
	 */
	private boolean simpleInterface = true;
	
	/**
	 * which layout should be used?
	 */
	private ViewLayout viewLayout = ViewLayout.CLASSIC;

	/**
	 * @return {@link ViewLayout} 
	 */
	public ViewLayout getViewLayout() {
		return viewLayout;
	}

	/**
	 * @param layout
	 */
	public void setViewLayout(ViewLayout layout) {
		this.viewLayout = layout;
	}

	/**
	 * @return the simpleInterface
	 */
	public boolean isSimpleInterface() {
		return this.simpleInterface;
	}

	/**
	 * @param simpleInterface the simpleInterface to set
	 */
	public void setSimpleInterface(final boolean simpleInterface) {
		this.simpleInterface = simpleInterface;
	}

}
