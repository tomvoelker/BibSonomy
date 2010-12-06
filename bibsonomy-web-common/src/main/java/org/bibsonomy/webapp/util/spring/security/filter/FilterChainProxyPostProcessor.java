package org.bibsonomy.webapp.util.spring.security.filter;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.util.UrlMatcher;

/**
 * class for customizing beans which are created with the spring security namespace (e.g. <http/>)
 * @author folke
 * @version $Id$
 */
public class FilterChainProxyPostProcessor implements BeanPostProcessor {

	private static final String FILTERCHAIN_BEAN_NAME = "org.springframework.security.filterChainProxy";
	
	/**
	 *   <map>
     *      <entry key="/somepath/**">
     *           <list>
     *             <ref local="filter1"/>
     *           </list>
     *       </entry>
     *       <entry key="/images/*">
     *           <list/>
     *       </entry>
     *       <entry key="/**">
     *           <list>
     *             <ref local="filter1"/>
     *             <ref local="filter2"/>
     *             <ref local="filter3"/>
     *           </list>
     *       </entry>
     *   </map>
     *   
     *   or 
     *   
     *   <sec:filter-chain-map path-type="ant">
     *	    <sec:filter-chain pattern="/somepath/**" filters="filter1"/>
     *      <sec:filter-chain pattern="/images/*" filters="none"/>
     *      <sec:filter-chain pattern="/**" filters="filter1, filter2, filter3"/>
     *   </sec:filter-chain-map>
	 */
	private Map <String, List<Filter>> filterChainMap;
	
	/** dummy attribute for allowing simplified namespace configuration */
	private UrlMatcher matcher;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof FilterChainProxy && FILTERCHAIN_BEAN_NAME.equals(beanName)) {
			final FilterChainProxy filterChain = (FilterChainProxy) bean;
			/** disabled for commit */
			// filterChain.setFilterChainMap(getFilterChainMap());
		}

		return bean;
	}

	public void setFilterChainMap(Map <String, List<Filter>> filterChainMap) {
		this.filterChainMap = filterChainMap;
	}

	public Map <String, List<Filter>> getFilterChainMap() {
		return filterChainMap;
	}

	public void setMatcher(UrlMatcher matcher) {
		this.matcher = matcher;
	}

	public UrlMatcher getMatcher() {
		return matcher;
	}

}
