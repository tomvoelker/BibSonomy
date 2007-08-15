package helpers.database;

import helpers.constants;

import java.sql.*;

import beans.AdminBean;


public class DBAdminEvaluator extends DBManager {
	
	/*
	 * Store user evaluation for BibSonomy data set 
	 */
	public static void flagSpammer (AdminBean bean, boolean spammer) {
		DBContext c = new DBContext();
		try {
			if (c.init()) {//initialize database
				/*
				 * update evaluator table
				 */
				String evaluator = bean.getEvaluator(); 
				
				if (spammer) {
					if (evaluator.equals("evaluator1")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator1 = " + constants.SQL_CONST_SPAMMER_TRUE  + " WHERE user_name = ? AND evaluator1 = " + constants.SQL_CONST_SPAMMER_FALSE);	
					}
					else if (evaluator.equals("evaluator2")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator2 = " + constants.SQL_CONST_SPAMMER_TRUE  + " WHERE user_name = ? AND evaluator2 = " + constants.SQL_CONST_SPAMMER_FALSE);	
					}
					else if (evaluator.equals("evaluator3")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator3 = " + constants.SQL_CONST_SPAMMER_TRUE  + " WHERE user_name = ? AND evaluator3 = " + constants.SQL_CONST_SPAMMER_FALSE);	
					}
					else {
					  bean.addError("user `" + bean.getUser() + " could not be flagged as spammer. Either he is already flagged or you misspelled the name.");
					}
				} else {
					if (evaluator.equals("evaluator1")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator1 = " + constants.SQL_CONST_SPAMMER_FALSE  + " WHERE user_name = ? AND evaluator1 = " + constants.SQL_CONST_SPAMMER_TRUE);	
					}
					else if (evaluator.equals("evaluator2")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator2 = " + constants.SQL_CONST_SPAMMER_FALSE  + " WHERE user_name = ? AND evaluator2 = " + constants.SQL_CONST_SPAMMER_TRUE);	
					}
					else if (evaluator.equals("evaluator3")){
						c.stmt = c.conn.prepareStatement("UPDATE evaluation SET evaluator3 = " + constants.SQL_CONST_SPAMMER_FALSE  + " WHERE user_name = ? AND evaluator3 = " + constants.SQL_CONST_SPAMMER_TRUE);	
					}
					else {
					  bean.addError("user `" + bean.getUser() + " could not be unflagged as spammer. Either he is already unflagged or you misspelled the name.");
					}
			    }
				c.stmt.setString(1, bean.getUser());
				if(c.stmt.executeUpdate() == 1) {
					/*
					* use has been (un)flagged as spammer ... set info output and change his posts
					*/
					if (spammer) 
						bean.addInfo("user `" + bean.getUser() + "` flagged as spammer!"); 
					else 
						bean.addInfo("user `" + bean.getUser() + "` UNflagged as spammer!");  
					}
				}
				else{ 
					bean.addError("user `" + bean.getUser() + " could not be UNflagged as spammer. Either he is already UNflagged or you misspelled the name.");
				}
				
		}catch (SQLException e) {
			System.out.println("DBAM: " + e);
			bean.addError(e.toString());
		} finally {
			c.close(); // close database connection
		}
	}

}
