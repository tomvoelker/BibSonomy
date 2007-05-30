package org.bibsonomy.database.managers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
/**
 * Used to retrieve users from the database.
 *
 * @author Miranda Grahl
 * @version $Id$
 */
/**
 * @author mgr
 *
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
	 *  
	 */
	
	/**
	 * get Api Key for given user
	 * 
	 * @param user
	 * @param session
	 * @return
	 */
	public String getApiKeyForUser(final UserParam user, final Transaction session) {
		return this.queryForObject("getApiKeyForUser", user, String.class, session);
	}
	
			
	/**
	 * Generate an API key for an existing user
	 * 
	 * @param user
	 * @param session
	 */	
	public void updateApiKeyForUser(final UserParam user, final Transaction session) {
		user.setApiKey(UserUtils.generateApiKey());
		this.update("updateApiKeyForUser", user, session);
	}
	
	/**
	 * Generate 
	 * 
	 * @param session
	 */
	public void generateApiKeysForAllUsers(final Transaction session) {
		
		UserParam param=new UserParam();
		List<User> user = this.getAllUsersWithoutApiKey(param,session);
				
		for(User currentUser  : user) {
		
			System.out.println(currentUser);
			param.setUserName(currentUser.getName());
			param.setApiKey(UserUtils.generateApiKey());			
			updateApiKeyForUser(param, session);
			
		}
		System.out.println("Finished with " + user.size() + " users");
	}
	
	/*
	 * TODO: renewApiKey
	 */
	
	
	/*
	 * get all Users of a given Group required different view right
	 */
	
	
	public List<String> getUserNamesByGroupId(final Integer groupId, final Transaction session) {
		return this.queryForList("getUserNamesByGroupId", groupId, String.class, session);
	}

	public List<User> getUsersOfGroupPublic(final UserParam user, final Transaction session) {
		return null; // TODO: implement
	}
	
	public List<User> getUsersOfGroupPrivate(final UserParam user, final Transaction session) {
		return this.queryForList("getUsersOfGroupPrivate", user, User.class, session);
	}
	
	public List<User> getUsersOfGroupFriends(final UserParam user, final Transaction session) {
		return null; // TODO: implement
	}
	
	public Integer getPrivlevelOfUser(final UserParam user, final Transaction session) {
		return this.queryForObject("getPrivlevelOfUser", user, Integer.class, session);
	}

	/*
	 * get details by a given user
	 */
	
	public User getUserDetails(final UserParam user, final Transaction session) {
		return this.queryForObject("getUserDetails", user, User.class, session);
	}

	/*
	 * get all users of the system
	 */
	
	public List<User> getAllUsers(final UserParam user, final Transaction session) {
		return this.queryForList("getAllUsers", user, User.class, session);
	}
	
	public List<User> getAllUsersWithoutApiKey(final UserParam user, final Transaction session) {
		return this.queryForList("getAllUsersWithoutApiKey", user, User.class, session);
	}

	/*
	 * insert attributes for new user account including new ApiKey;
	 */
	
	public void insertUser(final UserParam user, final Transaction session) {
		user.setApiKey(UserUtils.generateApiKey());
		this.insert("insertUser", user, session);
	}

	/*
	 * delete a user from the system
	 */ 
	
	public void deleteUser(final User user, final Transaction session) {
		this.delete("deleteUser", user, session);
	}
	
	
	/*
	 * returns all users of system
	 */
	
	public List<User> getUsers(String authUser, int start, int end, final Transaction session) {
		
		UserParam param =new UserParam();
		param.setRequestedUserName(authUser);
		
        param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		List<User> users=this.getAllUsers(param, session);
		return users;
	}

	/*
	 * returns all users who are members of the specified group
	 */
	
	public List<User> getUsers(String authUser, String groupName, int start, int end, final Transaction session) {
	
		UserParam param=new UserParam();
		param.setRequestedUserName(authUser);
		param.setGroupingName(groupName);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		
		//TODO implement incl. sql-statement
		List<User> users=null;
		
		return users;
	}
	
	
	/*
	 * Returns details about a specified user
	 */
	
	public User getUserDetails(String authUserName, String userName, final Transaction session) {
		
		UserParam param=new UserParam();
		param.setRequestedUserName(authUserName);
		param.setUserName(userName);
		User user =new User();
	    user= this.getUserDetails(param, session);
		return user;
	}
	
	/*
	 * TODO delete should also include delete of tas beside personal information
	 */
	
	public void deleteUser(String userName, final Transaction session) {
		// TODO: implement
	}
	
    public void storeUser(User user, boolean update, final Transaction session) {
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
    	 userTemp= getUsers(user.getName(),1,1, session);
    		
    		
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
    		 
    		 this.insert("insertUser", user, session);
    		 
    		
    		
    	}
    		
    		
   }
    	
    
    
    }
    	
	/**
	 * Authenticate a user by comparing his submitted API key with the one
	 * stored in the database
	 * 
	 * @param username
	 * @param apiKey
	 * @param session
	 * @return boolean true iff the 
	 */
	public boolean validateUserAccess(String username, String apiKey, Transaction session) {
		UserParam param = new UserParam();
		param.setUserName(username);
		String currentApiKey = this.getApiKeyForUser(param,session);
		return currentApiKey.equals(apiKey); 
	}

	
	
}