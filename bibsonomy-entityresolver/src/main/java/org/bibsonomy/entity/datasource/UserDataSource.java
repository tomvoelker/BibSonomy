package org.bibsonomy.entity.datasource;

import java.util.Collection;

import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.RecordImpl;
import no.priv.garshol.duke.datasources.InMemoryDataSource;

import org.bibsonomy.model.User;

public class UserDataSource extends InMemoryDataSource {
	
	public UserDataSource(Collection<User> entities) {
		super();
		for (User user : entities) {
			this.add(this.convert(user));
		}
	}

	protected Record convert(User user) {
		RecordImpl entry = new RecordImpl();
		if (user.getName()!=null) {
			entry.addValue("user_name", user.getName());
		}
		if (user.getRealname()!=null) {
			entry.addValue("user_realname", user.getRealname());
		}
		if (user.getPlace()!=null) {
			entry.addValue("place", user.getPlace());
		}
		if (user.getHomepage()!=null) {
			String homepage = (user.getHomepage()==null)?null:user.getHomepage().toExternalForm();
			entry.addValue("user_homepage", homepage);
		}
		
		return entry;
	}
}