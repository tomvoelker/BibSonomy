package org.bibsonomy.webapp.config;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Bean for managing runtime configuration of the authorization process.
 * 
 * @author folke
 * @author dzo
 * @version $Id$
 */
public class AuthenticationFilterConfigurator implements BeanPostProcessor {
	
	/**
	 * additional filters are added into the filter chain AFTER the {@link LogoutFilter}
	 * TODO: configure via spring 
	 */
	private static final Class<LogoutFilter> ENTRYPOINTFILTER = LogoutFilter.class;

	/**
	 * RememberMe filters are added into the filter chain BEFORE the {@link AnonymousAuthenticationFilter} 
	 * TODO: configure via spring 
	 */
	private static final Class<AnonymousAuthenticationFilter> REMEMBERME_ENTRYPOINTFILTER = AnonymousAuthenticationFilter.class;

	/** dertermines which authentication methods are used */
	private AuthConfig config;

	/**
	 * all known filters
	 */
	private Map<AuthMethod, Filter> authFilterMap = new HashMap<AuthMethod, Filter>();
	private Map<AuthMethod, Filter> authRememberMeFilterMap = new HashMap<AuthMethod, Filter>();
	/** FIXME: what are the pre filters? */
	private Map<AuthMethod, Filter> authPreFilterMap = new HashMap<AuthMethod, Filter>();

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof FilterChainProxy) {
			final FilterChainProxy proxy = (FilterChainProxy) bean;
			final Map<String, List<Filter>> filterChainMap = proxy.getFilterChainMap();
			
			for (final Entry<String, List<Filter>> list : filterChainMap.entrySet()) {
				final List<Filter> filterList = list.getValue();
				if (!filterList.isEmpty()) {
					// get all filters for each auth method
					final List<Filter> filters = new LinkedList<Filter>();
					final List<Filter> preFilters = new LinkedList<Filter>();
					final List<Filter> rememberMeFilters = new LinkedList<Filter>();
					for (final AuthMethod method : this.config.getAuthOrder()) {
						final Filter filter = this.authFilterMap.get(method);
						if (present(filter)) {
							filters.add(filter);
						}
							
						final Filter preFilter = this.authPreFilterMap.get(method);
						if (present(preFilter)) {
							preFilters.add(preFilter);
						}
							
						final Filter rememberMeFilter = this.authRememberMeFilterMap.get(method);
						if (present(rememberMeFilter)) {
							rememberMeFilters.add(rememberMeFilter);
						}
					}
					
					// TODO: preFilters

					// additional filters are added into the filter chain AFTER the {@link LogoutFilter}
					Integer filterEntryPoint = this.findFilter(ENTRYPOINTFILTER, filterList) + 1;
					filterList.addAll(filterEntryPoint, filters);
					
					// RememberMe filters are added into the filter chain BEFORE the {@link AnonymousAuthenticationFilter}
					int rememberMeEntryPoint = this.findFilter(REMEMBERME_ENTRYPOINTFILTER, filterList);
					filterList.addAll(rememberMeEntryPoint, rememberMeFilters);
				}
			}
			
			// set the new filter chain map
			proxy.setFilterChainMap(filterChainMap);
		}
		return bean;
	}
	
	/**
	 * look for given filter in given list of filters and return its position if found,
	 * null otherwise
	 * 
	 * @param filterClass requested filter
	 * @param filterList list of filters
	 * @return filter's position in filter list if found, null otherwise
	 */
	private Integer findFilter(Class<? extends GenericFilterBean> filterClass, List<Filter> filterList) {
		for( int i=0; i<filterList.size(); i++ ) {
			Filter curFilter = filterList.get(i);
			if( filterClass.isAssignableFrom(curFilter.getClass()) ) {
				return i;
			}
		}
		
		// not found
		return null;
	}
	
	/**
	 * @param config the config to set
	 */
	public void setConfig(AuthConfig config) {
		this.config = config;
	}
	
	/**
	 * @param authPreFilterMap the authPreFilterMap to set
	 */
	public void setAuthPreFilterMap(final Map<AuthMethod, Filter> authPreFilterMap) {
		this.authPreFilterMap = authPreFilterMap;
	}

	/**
	 * @param authFilterMap the authFilterMap to set
	 */
	public void setAuthFilterMap(final Map<AuthMethod, Filter> authFilterMap) {
		this.authFilterMap = authFilterMap;
	}
	
	/**
	 * @param authRememberMeFilterMap the authRememberMeFilterMap to set
	 */
	public void setAuthRememberMeFilterMap(final Map<AuthMethod, Filter> authRememberMeFilterMap) {
		this.authRememberMeFilterMap = authRememberMeFilterMap;
	}
}
