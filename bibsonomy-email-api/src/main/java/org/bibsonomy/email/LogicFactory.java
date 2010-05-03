package org.bibsonomy.email;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.client.RestLogicFactory;

/**
 * Creates an instance of the {@link LogicInterface} - either with
 * direct database access or using the REST-API.
 * 
 * Currently, username+apikey are used to authenticate users. This
 * is not a good idea, since the local part of e-mail addresses is
 * at most 64 bytes long. Since BibSonomy usernames can be up to 30
 * characters (at most 120 bytes in UTF-8) long, we can't ensure 
 * that every user can use this API. Therefore, we must introduce
 * different IDs.
 * 
 * Maybe, the API-Key is enough. If so, we need a method in the 
 * REST-API, to authenticate using the key only. 
 * 
 * A probably simple solution could be to use the user-id (i.e.,
 * numeric id in the database!) plus the api-key. To use less
 * characters, we could a Base64 instead of Hex-Encoding of the
 * API-Key. On the /settings-page, this address could then be
 * shown.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class LogicFactory {

	private final ToField toField;
	private final RestLogicFactory logicFactory;

	public LogicFactory(final String apiUrl, final ToField toField, final String from) {
		/*
		 * TODO: check from field
		 * 
		 * FIXME: when we have the real IDs, we should 
		 * have a factory which creates an instance of the
		 * LogicInterface using those IDs. To use the REST-API
		 */
		this.toField = toField;
		if (present(apiUrl)) {
			this.logicFactory = new RestLogicFactory(apiUrl);
		} else {
			this.logicFactory = new RestLogicFactory();
		}
	}
	public LogicFactory(final ToField toField, final String from) {
		this(null, toField, from);
	}

	public String getLoginUserName() {
		return toField.getUsername();
	}

	public LogicInterface getDBLogic() {
		final DBLogicApiInterfaceFactory factory = new DBLogicApiInterfaceFactory();
		factory.setDbSessionFactory(new IbatisDBSessionFactory());

		return factory.getLogicAccess(toField.getUsername(), toField.getApikey());
	}

	public LogicInterface getRestLogic() {
		return logicFactory.getLogicAccess(toField.getUsername(), toField.getApikey());
	}
}
