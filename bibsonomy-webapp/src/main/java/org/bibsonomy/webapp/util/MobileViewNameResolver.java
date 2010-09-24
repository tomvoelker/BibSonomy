package org.bibsonomy.webapp.util;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.webapp.view.MobileViews;
import org.bibsonomy.webapp.view.Views;

/**
 * @author boogie
 * @version $Id$
 */
public abstract class MobileViewNameResolver {

	private static View[][] viewsArray = new View[][] {
		{Views.HOMEPAGE, MobileViews.HOMEPAGE},
		{Views.USERPAGE, MobileViews.USERPAGE},
		{Views.TAGPAGE, MobileViews.TAGPAGE},
		{Views.SEARCHPAGE, MobileViews.SEARCHPAGE},
		{Views.POST_BOOKMARK, MobileViews.POST_BOOKMARK},
		{Views.POST_PUBLICATION, MobileViews.POST_PUBLICATION},
		{Views.BIBTEXDETAILS, MobileViews.BIBTEXDETAILS},
		{Views.GROUPPAGE, MobileViews.GROUPPAGE},
		{Views.LOGIN, MobileViews.LOGIN},
		{Views.USERTAGPAGE, MobileViews.USERTAGPAGE},
		{Views.EDIT_BOOKMARK, MobileViews.EDIT_BOOKMARK},
		{Views.EDIT_PUBLICATION, MobileViews.EDIT_PUBLICATION},
		{Views.AUTHORPAGE, MobileViews.AUTHORPAGE},
		{Views.URLPAGE, MobileViews.URLPAGE},
		{Views.POPULAR_TAGS, MobileViews.POPULAR_TAGS}
	};
	
	private static final Map<String, String> views;
	
	static {
		
		views = new HashMap<String, String>();
		
		for(View[] view : viewsArray) {
			
			views.put(view[0].getName(), view[1].getName());
		}
	}
	
	/**
	 * Resolve the view name from desktop to mobile
	 * @param viewName
	 * @return the mobile view name
	 */
	public static String resolveView(String viewName) {
		
		if(views.containsKey(viewName))
			return views.get(viewName);
		
		return viewName;
	}
}