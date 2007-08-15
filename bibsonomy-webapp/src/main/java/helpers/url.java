package helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class url {
	
	public static String encode (String url) {
        try {
        	return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	System.out.println("url.encode: " + e);
        	return url;
        }
	}
	
}
