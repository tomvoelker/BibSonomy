package org.bibsonomy.webapp.util.captcha;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * TODO: add documentation to this class
 *
 * @author niebler
 */
public class ReCaptcha2 implements Captcha {
	
	private static final Log log = LogFactory.getLog(ReCaptcha2.class);
	
	private String privateKey;
	private String publicKey;
	private String recaptchaServer;
	private boolean includeNoscript;
	
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.Captcha#createCaptchaHtml(java.util.Locale)
	 */
	@Override
	public String createCaptchaHtml(Locale locale) {
		return "<script src='https://www.google.com/recaptcha/api.js'></script>" + 
				"<div class=\"g-recaptcha\" data-sitekey=\"" + this.publicKey +  "\"></div>";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.Captcha#checkAnswer(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CaptchaResponse checkAnswer(String challenge, String response, String remoteHostInetAddress) {
		log.warn("Received response: " + response);
		log.warn("Received remoteHostInetAddress: " + remoteHostInetAddress);
		boolean success = false;
		String errorCodes = "";
		try {
			List<BasicNameValuePair> entity = Arrays.asList(new BasicNameValuePair[] {
					new BasicNameValuePair("secret", this.privateKey),
					new BasicNameValuePair("response", response),
					new BasicNameValuePair("remoteip", remoteHostInetAddress),
					});
			HttpPost post = new HttpPost(recaptchaServer);
			
			post.setEntity(new UrlEncodedFormEntity(entity));
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse httpResponse = client.execute(post);
			String reCaptchaResponse = EntityUtils.toString(httpResponse.getEntity());
			log.warn("Received reCaptcha response: " + reCaptchaResponse);
			JSONParser parser = new JSONParser();
			JSONObject array = (JSONObject) parser.parse(reCaptchaResponse);
			log.warn(array.toString());
			success = Boolean.parseBoolean(array.get("success") + "");
			if (array.containsKey("error-codes")) {
				errorCodes = array.get("error-codes") + "";
			}
			client.close();
			httpResponse.close();
			log.warn("success: " + success);
			log.warn("error-codes: " + errorCodes);
		} catch (UnsupportedEncodingException e) {
			return new ReCaptcha2Response(false, "Unsupported Encoding! Did not send a request.");
		} catch (HttpException e) {
			return new ReCaptcha2Response(false, "HttpException: " + e.getStackTrace());
		} catch (IOException e) {
			return new ReCaptcha2Response(false, "IOException: " + e.getStackTrace());
		} catch (ParseException e) {
			return new ReCaptcha2Response(false, "Could not parse response: " + e.getStackTrace());
		} catch (org.json.simple.parser.ParseException e) {
			return new ReCaptcha2Response(false, "Could not parse JSON: " + e.getStackTrace());
		}
		
		// TODO: Parse Error Codes and post corresponding messages.
		return new ReCaptcha2Response(success, errorCodes);
	}

}
