/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.spring.servlet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bibsonomy.model.UserSettings;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.view.constants.ViewLayout;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class LayoutSwitchingViewResolver implements ViewResolver {

	private Map<ViewLayout,ViewResolver> resolverMap = new HashMap<ViewLayout,ViewResolver>();
	


	@Override
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		
		UserSettings settings = AuthenticationUtils.getUser().getSettings();
		ViewLayout layout = settings.getLayoutSettings().getViewLayout();
		
		ViewResolver viewResolver = resolverMap.get(layout);
		
		if(viewResolver == null) {
			viewResolver = resolverMap.get(ViewLayout.CLASSIC);
			
			if(viewResolver == null) {
				throw new IllegalStateException("viewResolver empty");
			}
		}
		
		return viewResolver.resolveViewName(viewName, locale);
	}
	
	public Map<ViewLayout, ViewResolver> getResolverMap() {
		return resolverMap;
	}

	public void setResolverMap(Map<ViewLayout, ViewResolver> resolverMap) {
		this.resolverMap = resolverMap;
	}


	

}