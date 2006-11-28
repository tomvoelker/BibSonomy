package org.bibsonomy.ibatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.bibsonomy.ibatis.params.generic.ByDate;
import org.bibsonomy.ibatis.params.generic.ByUser;
import org.bibsonomy.model.User;

public class UserTest extends AbstractSqlMapTest {

	public ByDate getDefaultByDate() {
		return new ByDate(new Date());
	}

	public ByUser getDefaultByUser() {
		/*
		 * machen wir mal noch Datum hinzu und testen das DynamicMapped Statement
		 * 
		 * --> interessanterweise sagt die DEBUG-Ausgabe von iBatis dann immer noch, 
		 * dass das ganze als PreparedStatement abgeliefert wird. Hhhmm. Nett. :-)
		 */
		return new ByUser("rja", new Date());
	}

	public User getDefaultUser() {
		User user = new User();
		user.setName("rja");
		user.setRealname("Robert Jäschke");
		user.setEmail("jaeschke@cs.uni-kassel.de");
		user.setHomepageAsString("http://www.kde.cs.uni-kassel.de/jaeschke'\b\b\b\b\\x22\\x22\\x22\\x22\\x22\\x22\\x22\\x22\\x22; DELETE FROM user;");
		return user;
	}

	@SuppressWarnings("unchecked")
	public void testGetUsersByDate() {
		try {
			final ByUser bu = this.getDefaultByUser();

			final List<User> users = this.sqlMap.queryForList("getByUser", bu);

			for (final User user : users) {
				System.out.println("name              = " + user.getName());
				System.out.println("real name         = " + user.getRealname());
				System.out.println("email             = " + user.getEmail());
				System.out.println("homepage          = " + user.getHomepage());
				System.out.println("registration date = " + user.getRegistrationDate());
				System.out.println("-------------------");
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void testInsertUser() {
		/*
		try {
			final User user = this.getDefaultUser();

			this.sqlMap.insert("insertUser", user);

		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
		*/
	}
}