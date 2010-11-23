package org.bibsonomy.webapp.util.spring;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.wurfl.core.DeviceNotDefinedException;
import net.sourceforge.wurfl.core.WURFLManager;

import org.bibsonomy.webapp.util.MobileViewNameResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Waldemar Biller <biller@cs.uni-kassel.de>
 * @version $Id$
 */
public class MobileDetectionHandlerInterceptor implements HandlerInterceptor {

	private WURFLManager wurflManager;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {

		if (
				present(modelAndView.getViewName()) && 
				isMobileCookieSet(request) && 
				!disabledMobile(request, response) && 
				isMobileDevice(request)
		) {

			if(request.getParameterMap().containsKey("manual")) {
				modelAndView.getModel().put("manual", true);
			}

			response.addCookie(new Cookie("mobile", "true"));
			modelAndView.getModel().put("isMobile", true);
			modelAndView.setViewName(MobileViewNameResolver.resolveView(modelAndView.getViewName()));
		}
	}


	private boolean isMobileDevice(HttpServletRequest request) {
		try {
			return present(wurflManager.getDeviceForRequest(request).getCapability("mobile_browser"));
		} catch (final DeviceNotDefinedException ex) {
			return false;
		}
	}


	/**
	 * checks whether mobile parameter is present or not and if the value of this is false
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean disabledMobile(HttpServletRequest request, HttpServletResponse response) {

		if (request.getParameterMap().containsKey("mobile") && "false".equals(request.getParameter("mobile"))) {
			/*
			 * disable mobile site for the session
			 */
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
		if (present(request.getCookies())) {
			for (final Cookie cookie : request.getCookies()) {

				/*
				 * check if mobile device has been deactivated
				 */
				if ("mobile".equals(cookie.getName()) && "false".equals(cookie.getValue()))
					return false;
			}
		}
		return true;
	}

	/**
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
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
