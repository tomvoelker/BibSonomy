/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util.spring.security;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.bibsonomy.common.enums.AuthMethod;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * Adds the filters in the properties authFilterMap, authRememberMeFilterMap, and authPreFilterMap to all the filterChains in the applicationContext (in a specific order).
 * These filterchains are then used by the {@link FilterChainProxy} instance that is to be created.
 * Only filters for the {@link AuthMethod}s specified by {@link #config} are considered (such that config can come from a installation specific properties file)
 * 
 * @author dzo
 */
public class FilterChainProxyFactoryBean implements FactoryBean<FilterChainProxy>, ApplicationContextAware {
	
	/**
	 * remember me filters are added into the filter chain BEFORE the {@link AnonymousAuthenticationFilter} 
	 */
	private static final Class<?> REMEMBERME_ENTRYPOINT_FILTER = AnonymousAuthenticationFilter.class;
	
	/**
	 * pre filters are added into the filter chain AFTER the {@link LogoutFilter}
	 */
	private static final Class<?> LOGOUT_FILTER = LogoutFilter.class;
	
	/**
	 * look for given filter in given list of filters and return its position if found,
	 * null otherwise
	 * 
	 * @param filterClass requested filter
	 * @param filterList list of filters
	 * @return filter's position in filter list if found, null otherwise
	 */
	private static int findFilter(final Class<?> filterClass, final List<Filter> filterList) {
		for (int i = 0; i < filterList.size(); i++ ) {
			final Filter filter = filterList.get(i);
			if (filterClass.isAssignableFrom(filter.getClass())) {
				return i;
			}
		}
		
		// not found
		return -1;
	}
	

	/** determines which authentication methods are used */
	private List<AuthMethod> config;

	/**
	 * all known filters
	 */
	private Map<AuthMethod, List<Filter>> authFilterMap = new HashMap<AuthMethod, List<Filter>>();
	private Map<AuthMethod, Filter> authRememberMeFilterMap = new HashMap<AuthMethod, Filter>();
	private Map<AuthMethod, Filter> authPreFilterMap = new HashMap<AuthMethod, Filter>();

	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public FilterChainProxy getObject() throws Exception {
		final List<SecurityFilterChain> filterChains = new LinkedList<SecurityFilterChain>(this.applicationContext.getBeansOfType(SecurityFilterChain.class).values());
		
		for (final SecurityFilterChain securityFilterChain : filterChains) {
			final List<Filter> filterList = securityFilterChain.getFilters();
			if (present(filterList)) {
				// get all filters for each auth method
				final List<Filter> filters = new LinkedList<Filter>();
				final List<Filter> preFilters = new LinkedList<Filter>();
				final List<Filter> rememberMeFilters = new LinkedList<Filter>();
				for (final AuthMethod method : this.config) {
					final List<Filter> authFilters = this.authFilterMap.get(method);
					if (present(authFilters)) {
						filters.addAll(authFilters);
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
				
				/*
				 * filter chain
				 * 1. SecurityContextPersistenceFilter
				 * 2. LogoutFilter
				 * 3. all pre filters @see preFilters
				 * 4. all filters @see filters
				 * 5. RequestCacheAwareFilter
				 * 6. SecurityContextHolderAwareRequestFilter
				 * 7. all remember me filters @rememberMeEntryPoint
				 * 8. SecurityContextHolderAwareRequestFilter 
				 * 9. AnonymousAuthenticationFilter
				 * 10. SessionManagementFilter
				 * 11. ExceptionTranslationFilter
				 * 11. a. our ExceptionTranslationFilter
				 * 12. FilterSecurityInterceptor
				 */
				// to keep things simple add all filters after the preFilters
				preFilters.addAll(filters);

				// additional filters are added into the filter chain AFTER the {@link LogoutFilter}
				final int filterEntryPoint = findFilter(LOGOUT_FILTER, filterList) + 1;
				filterList.addAll(filterEntryPoint, preFilters);
				
				// RememberMe filters are added into the filter chain BEFORE the {@link AnonymousAuthenticationFilter}
				final int rememberMeEntryPoint = findFilter(REMEMBERME_ENTRYPOINT_FILTER, filterList);
				filterList.addAll(rememberMeEntryPoint, rememberMeFilters);
			}
		}
		
		return new FilterChainProxy(filterChains);
	}

	@Override
	public Class<?> getObjectType() {
		return FilterChainProxyFactoryBean.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	/**
	 * @param config the config to set
	 */
	public void setConfig(final List<AuthMethod> config) {
		this.config = config;
	}
	
	/**
	 * @return the config
	 */
	public List<AuthMethod> getConfig() {
		return this.config;
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
	public void setAuthFilterMap(final Map<AuthMethod, List<Filter>> authFilterMap) {
		this.authFilterMap = authFilterMap;
	}
	
	/**
	 * @param authRememberMeFilterMap the authRememberMeFilterMap to set
	 */
	public void setAuthRememberMeFilterMap(final Map<AuthMethod, Filter> authRememberMeFilterMap) {
		this.authRememberMeFilterMap = authRememberMeFilterMap;
	}

}
