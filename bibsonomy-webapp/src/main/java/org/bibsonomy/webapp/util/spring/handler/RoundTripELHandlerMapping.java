/*
 * Created on 03.10.2007
 */
package org.bibsonomy.webapp.util.spring.handler;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import de.innofinity.roundtripel.MultiRoundTripEL;
import de.innofinity.roundtripel.expressionparts.ParseResult;

public class RoundTripELHandlerMapping<T> implements HandlerMapping {
	private static final Logger log = Logger.getLogger(RoundTripELHandlerMapping.class);
	private final MultiRoundTripEL<T> handlerMap;
	
	public RoundTripELHandlerMapping(final Map<String,T> handlerMap) {
		this.handlerMap = new MultiRoundTripEL<T>();
		for (Map.Entry<String, T> entry : handlerMap.entrySet()) {
			this.handlerMap.put(entry.getKey(), entry.getValue(), true);
		}
	}
	
	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		try {
			String fullPath = request.getRequestURI();
			String ctxPath = request.getContextPath();
			String path = fullPath.substring(ctxPath.length());
			final Collection<ParseResult<T>> results = handlerMap.parse(path);
			if (results != null) {
				for (ParseResult<T> result : results) {
					Map<String, String> varMap = result.getVarMap();
					for (Map.Entry<String, String> varMapEntry : varMap.entrySet()) {
						request.setAttribute(varMapEntry.getKey(), varMapEntry.getValue());
					}
					return new HandlerExecutionChain(result.getValue(), null);
				}
			}
		} catch (Exception ex) {
			log.error(ex,ex);
		}
		return null;
	}
	
}
