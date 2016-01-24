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
package org.bibsonomy.webapp.util.captcha;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * ReCaptcha2 implementation
 *
 * @author niebler
 */
public class ReCaptcha2 implements Captcha {
	private static final Log log = LogFactory.getLog(ReCaptcha2.class);

	private static final String ERROR_CODES_FIELD = "error-codes";
	private static final String SUCCESS_FIELD = "success";
	
	
	// FIXME: check for timeouts (request, response)
	private CloseableHttpClient client;
	
	private String privateKey;
	private String publicKey;
	private String recaptchaServer;
	private boolean includeNoscript; // TODO: support noscript by default; remove param
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.Captcha#createCaptchaHtml(java.util.Locale)
	 */
	@Override
	public String createCaptchaHtml(final Locale locale) {
		// FIXME: support locale; see https://developers.google.com/recaptcha/docs/language
		return "<script src='https://www.google.com/recaptcha/api.js'></script>" + 
				"<div class=\"g-recaptcha\" data-sitekey=\"" + this.publicKey +  "\"></div>";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.Captcha#checkAnswer(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CaptchaResponse checkAnswer(final String challenge, final String response, final String remoteHostInetAddress) {
		log.debug("Received response: " + response);
		log.debug("Received remoteHostInetAddress: " + remoteHostInetAddress);
		
		try {
			final List<BasicNameValuePair> entity = Arrays.asList(
				new BasicNameValuePair("secret", this.privateKey),
				new BasicNameValuePair("response", response),
				new BasicNameValuePair("remoteip", remoteHostInetAddress));
			
			final HttpPost post = new HttpPost(this.recaptchaServer);
			post.setEntity(new UrlEncodedFormEntity(entity));
			
			final CloseableHttpResponse httpResponse = this.client.execute(post);
			final String reCaptchaResponse = EntityUtils.toString(httpResponse.getEntity());
			log.debug("Received reCaptcha response: " + reCaptchaResponse);
			
			final JSONParser parser = new JSONParser();
			final JSONObject responseObject = (JSONObject) parser.parse(reCaptchaResponse);
			log.debug(responseObject.toString());
			
			final boolean success = Boolean.parseBoolean(responseObject.get(SUCCESS_FIELD).toString());
			String errorCodes = null;
			if (responseObject.containsKey(ERROR_CODES_FIELD)) {
				errorCodes = responseObject.get(ERROR_CODES_FIELD).toString();
			}
			httpResponse.close();
			log.debug("success: " + success);
			log.debug("error-codes: " + errorCodes);
			
			// TODO: Parse Error Codes and post corresponding messages.
			return new ReCaptcha2Response(success, errorCodes);
		} catch (final UnsupportedEncodingException e) {
			return new ReCaptcha2Response(false, "Unsupported Encoding! Did not send a request.");
		} catch (final HttpException e) {
			return new ReCaptcha2Response(false, "HttpException: " + e.getStackTrace());
		} catch (final IOException e) {
			return new ReCaptcha2Response(false, "IOException: " + e.getStackTrace());
		} catch (final ParseException e) {
			return new ReCaptcha2Response(false, "Could not parse response: " + e.getStackTrace());
		} catch (final org.json.simple.parser.ParseException e) {
			return new ReCaptcha2Response(false, "Could not parse JSON: " + e.getStackTrace());
		}
	}
	
	/**
	 * @param includeNoscript the includeNoscript to set
	 */
	public void setIncludeNoscript(boolean includeNoscript) {
		this.includeNoscript = includeNoscript;
	}
	
	/**
	 * 
	 * @param privateKey
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
	/**
	 * 
	 * @param publicKey
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * 
	 * @param recaptchaServer
	 */
	public void setRecaptchaServer(String recaptchaServer) {
		this.recaptchaServer = recaptchaServer;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}
}
