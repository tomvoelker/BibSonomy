package org.bibsonomy.webapp.util.spring;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.WURFLManager;

import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.util.MobileViewNameResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author wbiller
 * @version $Id$
 */
public class MobileDetectionHandlerInterceptor implements HandlerInterceptor {

	private WURFLManager wurflManager;

	private boolean isMobileDevice(HttpServletRequest request) {
		
		Device device = wurflManager.getDeviceForRequest(request);
		return ValidationUtils.present(device.getCapability("mobile_browser"));
	}
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {}

	@SuppressWarnings("unchecked")
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {
		
		boolean isMobile = isMobileDevice(request);
		if(ValidationUtils.present(modelAndView.getViewName())) {
			
			if(isMobileCookieSet(request)) {
				
				if(!disabledMobile(request, response)) {
					
					if(isMobile) {
						
						if(request.getParameterMap().containsKey("manual"))
							modelAndView.getModel().put("manual", true);
						
						response.addCookie(new Cookie("mobile", "true"));
						modelAndView.getModel().put("isMobile", true);
						modelAndView.setViewName(MobileViewNameResolver.resolveView(modelAndView.getViewName()));
					}
				}
			}
		}
	}
	
	/**
	 * checks whether mobile parameter is present or not and if the value of this is false
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean disabledMobile(HttpServletRequest request, HttpServletResponse response) {
		
		if(request.getParameterMap().containsKey("mobile") && "false".equals(request.getParameter("mobile"))) {
			//disable mobile site for the session
			response.addCookie(new Cookie("mobile", "false"));
			return true;
		}
		return false;
	}
	
	/**
	 * checks the cookies for the mobile cookie and if it's value is false
	 * @param request
	 * @return
	 */
	private boolean isMobileCookieSet(HttpServletRequest request) {
		
		if(request.getCookies() != null) {
			for(Cookie c : request.getCookies()) {
				
				// check if mobile has been deactivated
				if("mobile".equals(c.getName()) && "false".equals(c.getValue()))
					return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		
		return true;
	}

	/**
	 * set the WURFL manager
	 * @param wurflManager
	 */
	public void setWurflManager(final WURFLManager wurflManager) {
		this.wurflManager = wurflManager;
	}

	/**
	 * get the WURFL manager
	 * @return the WURFL manager
	 */
	public WURFLManager getWurflManager() {
		return wurflManager;
	}

}
