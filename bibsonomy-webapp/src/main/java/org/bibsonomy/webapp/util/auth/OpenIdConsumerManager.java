package org.bibsonomy.webapp.util.auth;

import java.io.Serializable;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;

/**
 * @author sts
 * @version $Id$
 */
@Deprecated
public class OpenIdConsumerManager extends ConsumerManager implements Serializable {
	
	private static final long serialVersionUID = 1045931496332265932L;

	/**
	 * default constructor
	 * @throws ConsumerException
	 */
	public OpenIdConsumerManager() throws ConsumerException {
		super();
	}

	

}
