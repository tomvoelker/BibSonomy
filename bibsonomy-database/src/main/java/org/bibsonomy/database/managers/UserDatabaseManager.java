package org.bibsonomy.database.managers;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.User;

/**
 * Used to retrieve users from the database.
 *
 * @author mgr
 * @version $Id$
 */
public class UserDatabaseManager extends AbstractDatabaseManager  {

	/** Singleton */
	private  final static UserDatabaseManager singleton = new UserDatabaseManager();

	private UserDatabaseManager() {
	}

	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	/*
	 * get all Users of a given Group required different view right
	 */

	public List<User> getUsersOfGroupPublic(final UserParam user, final Transaction transaction) {
		return this.queryForList("getUsersOfGroupPublic", user, User.class, transaction);
	}
	
	public List<User> getUsersOfGroupPrivate(final UserParam user, final Transaction transaction) {
		return this.queryForList("getUsersOfGroupPrivate", user, User.class, transaction);
	}
	
	public List<User> getUsersOfGroupFriends(final UserParam user, final Transaction transaction) {
		return this.queryForList("getUsersOfGroupFriends", user, User.class, transaction);
	}
	
	public Integer getPrivlevelOfUser(final UserParam user, final Transaction transaction) {
		return this.queryForObject("getPrivlevelOfUser", user, Integer.class, transaction);
	}

	/*
	 * get details by a given group of a user
	 */
	public List<User> getUserDetails(final UserParam user, final Transaction transaction) {
		return this.queryForList("getUserDetails", user, User.class, transaction);
	}

	/*
	 * get all users of the system
	 */
	public List<User> getAllUsers(final UserParam user, final Transaction transaction) {
		return this.queryForList("getAllUsers", user, User.class, transaction);
	}

	/*
	 * insert attributes for new user account
	 */
	public void insertUser(final User user, final Transaction transaction) {
		this.insert("insertUser", user, transaction);
	}

	/*
	 * delete a user from the system
	 */ 
	public void deleteUser(final User user, final Transaction transaction) {
		this.delete("deleteUser", user, transaction);
	}
	
	
	
	
	
	
	public List<User> getUsers(String authUser, int start, int end, final Transaction transaction) {
		// TODO: implement
		return null;
	}

	/*
	 * returns all users who are members of the specified group
	 */
	
	public List<User> getUsers(String authUser, String groupName, int start, int end, final Transaction transaction) {
		// TODO: implement
		return null;
	}
	
	
	
	public User getUserDetails(String authUserName, String userName, final Transaction transaction) {
		// TODO: implement
		return null;
	}
	
	/*
	 * TODO delete should also include delete of tas beside personla information
	 */
	
	public void deleteUser(String userName, final Transaction transaction) {
		// TODO: implement
	}
	
    public void storeUser(User user, boolean update, final Transaction transaction) {
    	/*
    	 * TODO sql-statements are not implemented 
    	 */
    	/*
    	 * user would like to update his/her personal information like password or email address
    	 *************************************UPDATE********************************************
    	 */
    	
    	if(update==true ){
    		
    	/*
    	 * test if user already exist
    	 */
    	
    		
    		
    	
         List<User> userTemp=new  LinkedList<User>();
    	 userTemp= getUsers(user.getName(),1,1, transaction);
    		
    		
        /*
         * if the user already exists, it must be exist an user account in database according give name
         */	
    		
    		if(userTemp.size()==0){
        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        		System.out.println("++++++++++++++++no user for given name in database++++++++++++++++++++++");
        		System.out.println("++++++++++++++++++++EXCEPTION++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        		throw new RuntimeException("No user for given name in database");
        	}
    		
    		
    		else{	
    			/*
    			 * userProve is the object, which is already written in the database 
    			 */
    			
    	        User proveUser =userTemp.get(0);
   		 		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
   		 		System.out.println("+++++++an user object is returned for given user name++++++++++++++++++++++++++++++");     
   		 		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
   		 		System.out.println("user_name = " + proveUser.getName());
   		 		System.out.println(" email_address= " + proveUser.getEmail());
   		 		System.out.println("password " + proveUser.getPassword());
   		 		System.out.println("homepage  " + proveUser.getHomepage());
   		 		System.out.println("registry Date " + proveUser.getRegistrationDate());
   		 		
   		 		/*
   		 		 * comparison of user object in database and current handled user object 
   		 		 * each attribute two is compared
   		 		 * 
   		 		 */

   		 		if(proveUser.getName()!= user.getName()||proveUser.getEmail()!=user.getEmail()||proveUser.getHomepage()!=user.getHomepage()||proveUser.getPassword()!=user.getPassword()||proveUser.getRealname()!=user.getRealname()){
   		 			
   		 		/*
   		 		 * loggen löschen einfügen
   		 		 */
   		 			
   		 			
   		 		}
   		 		
   		 		/*
   		 		 * no changes in user settings 
   		 		 */
   		 		
   		 		else{
   		 			
   		 		System.out.println("user make no changes for his settings");
   		 			
   		 			
   		 		}
   		 		
    		}
    		
    		
    		
    		
    	
    	}
    	
    	
    	else{
    		
    		/*
    		 * new user does not exist and would like get an account
    		 * **********************INSERT************************* 
    		 */

    		// FIXME if "update" isn't "true" it should be false in this else block, shouldn't it?!?
    	 if(update==false){
    		 
    		 this.insert("insertUser", user, transaction);
    		 
    		
    		
    	}
    		
    		
   }
    	
    
    
    }
    	
	public boolean validateUserAccess(String username, String password) {
		return true;
	}

	
	
}