package org.bibsonomy.database.managers;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
/**
 * Used to retrieve users from the database.
 * @author mgr
 */
public class UserDatabaseManager extends AbstractDatabaseManager  {

	/** Singleton */
	private  final static UserDatabaseManager singleton = new UserDatabaseManager();
	private final GeneralDatabaseManager generalDb;

	UserDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	@SuppressWarnings("unchecked")
	protected List<User> userList(final String query, final User user) {
		return (List<User>) queryForList(query, user);
	}

	/*
	 * get all Users of a given Group 
	 */
	
	public List<User> getUsersOfGroup(final User user) {
		return this.userList("getUsersOfGroup", user);
	}
	
	/*
	 * get details by a given group of a user
	 */
	
	public User getUserDetails(final User user) {
		return (User) this.queryForObject("getUserDetails", user);
	}
	
	/*
	 * insert attributes for new user account
	 */
	public void insertUser(final User user) {
		this.insert("insertUser", user);
	}
	
	public void deleteUser(final User user) {
		this.delete("deleteUser", user);
	}
	
	
	
	
	public List<User> getUsers(String authUser, int start, int end) {
		return null;
	}

	/*
	 * returns all users who are members of the specified group
	 */
	
	public List<User> getUsers(String authUser, String groupName, int start, int end) {
		return null;
	}
	
	
	
	public User getUserDetails(String authUserName, String userName) {
		return null;
	}
	
	/*
	 * TODO delete should also include delete of tas beside personla information
	 */
	
	public void deleteUser(String userName) {

	}
	
    public void storeUser(User user, boolean update) {
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
    	 userTemp= getUsers(user.getName(),1,1);
    		
    		
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
    			 * userProve is the objec, which is already written in the database 
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
    		
    	 if(update==false){
    		 
    		 this.insert("insertUser",user);
    		 
    		
    		
    	}
    		
    		
   }
    	
    
    
    }
    	
    	
    	
    	
		
	
	public boolean validateUserAccess(String username, String password) {
		return true;
	}

	
	
}