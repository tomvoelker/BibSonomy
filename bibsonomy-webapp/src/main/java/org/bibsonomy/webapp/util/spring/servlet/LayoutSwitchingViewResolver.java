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