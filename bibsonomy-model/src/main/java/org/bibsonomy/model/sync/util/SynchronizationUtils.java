package org.bibsonomy.model.sync.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;
import java.net.URISyntaxException;

import org.bibsonomy.model.sync.SynchronizationData;

/**
 * @author dzo
 * @version $Id$
 */
public final class SynchronizationUtils {
	private static final String CLIENT_SPECIAL_SCHEME = "client";

	private SynchronizationUtils() {}

	/**
	 * @param uri the uri to check
	 * @return <code>true</code> iff uri is a client uri
	 */
	public static boolean isClientURI(final URI uri) {
		return uri != null && CLIENT_SPECIAL_SCHEME.equals(uri.getScheme());
	}

	/**
	 * 
	 * @param service the uri of the service
	 * @return	the synchronizationData
	 */
	public static SynchronizationData buildSynchronizationDataforService(final URI service) {
		final SynchronizationData data = new SynchronizationData();
		if (!isClientURI(service)) {
			data.setService(service);
			data.setDeviceId(""); // other services get an empty string as device id
			return data;
		}
		
		/*
		 * e.g. Android App, iOS App, Texlipse Plugin, …
		 * TODO: improve extract query params
		 */
		final String deviceId = service.getPath().substring(1);
		data.setDeviceId(deviceId);
		final String query = service.getQuery();
		final String deviceInfo;
		if (present(query)) {
			final String[] split = query.split("=");
			deviceInfo = split[1];
		} else {
			deviceInfo = "unknown";
		}
		
		data.setDeviceInfo(deviceInfo);
		
		try {
			data.setService(new URI(service.getScheme(), service.getHost(), null, null));
		} catch (final URISyntaxException ex) {
			/*
			 * scheme and host from a valid uri must be valid to
			 */
			throw new RuntimeException(ex);
		}
		
		return data;
	}	
}
