package org.bibsonomy.model.logic;

import java.net.InetAddress;
import java.util.List;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;

/**
 * This interface is an adapter to BibSonomy's core functionality. <br/>
 * 
 * The methods returning information return in general, if there are no matches,
 * an empty set (if a list is requested), or null (if a single entity is
 * requested (e.g. a post)). <br/>
 * 
 * <b>Please try to be as close to the method-conventions as possible.</b> If
 * something is unclear, guess, check occurences and document your result. If
 * you have to change a convention, check all occurences and document it
 * properly! Try to check each possibility with a test-case.<br/>
 * 
 * BE AWARE that this might grow quickly. So distribute methods across classes
 * or at least interfaces (like it has been done with PostLogicInterface) and
 * use these in your code.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Jens Illig <illig@innofinity.de>
 * @author Christian Kramer
 * @version $Id$
 */
public interface LogicInterface extends PostLogicInterface {

	/**
	 * @return the name of the authenticated user
	 */
	public String getAuthenticatedUser();
	
	/**
	 * Returns all users
	 * 
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(int start, int end);

	/**
	 * Returns all users who are members of the specified group
	 * 
	 * @param groupName name of the group
	 * @param start
	 * @param end
	 * @return a set of users, an empty set else
	 */
	public List<User> getUsers(String groupName, int start, int end);

	/**
	 * Returns details about a specified user
	 * 
	 * @param userName name of the user we want to get details from
	 * @return details about a named user, null else
	 */
	public User getUserDetails(String userName);

	/**
	 * Returns all groups of the system.
	 * 
	 * @param end
	 * @param start
	 * @return a set of groups, an empty set else
	 */
	public List<Group> getGroups(int start, int end);

	/**
	 * Returns details of one group.
	 * 
	 * @param groupName
	 * @return the group's details, null else
	 */
	public Group getGroupDetails(String groupName);

	/**
	 * Returns a list of tags which can be filtered.
	 * @param resourceType
	 * 			  a resourceType (i.e. Bibtex or Bookmark) to get tags
	 *  		  only from a bookmark or a bibtex entry
	 * @param grouping
	 *            grouping tells whom tags are to be shown: the tags of a user,
	 *            of a group or of the viewables.
	 * @param groupingName
	 *            name of the grouping. if grouping is user, then its the
	 *            username. if grouping is set to {@link GroupingEntity#ALL},
	 *            then its an empty string!
	 * @param regex
	 *            a regular expression used to filter the tagnames
	 * @param tags
	 * @param hash
				  a resource hash (bibtex or bookmark)
	 * @param order 
	 * @param start
	 * @param end
	 * @param search - search string
	 * @return a set of tags, en empty set else
	 */
	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search);

	/**
	 * Returns details about a tag. Those details are:
	 * <ul>
	 * <li>details about the tag itself, like number of occurrences etc</li>
	 * <li>list of subtags</li>
	 * <li>list of supertags</li>
	 * <li>list of correlated tags</li>
	 * </ul>
	 * 
	 * @param tagName name of the tag
	 * @return the tag's details, null else
	 */
	public Tag getTagDetails(String tagName);

	/**
	 * Removes the given user.
	 * 
	 * @param userName the user to delete
	 */
	public void deleteUser(String userName);

	/**
	 * Removes the given group.
	 * 
	 * @param groupName the group to delete
	 */
	public void deleteGroup(String groupName);

	/**
	 * Removes an user from a group.
	 * 
	 * @param groupName the group to change
	 * @param userName the user to remove
	 */
	public void removeUserFromGroup(String groupName, String userName);

	/**
	 * Adds a user to the database.
	 * 
	 * @param user  the user to add
	 * @return userid the user id of the created user
	 */
	public String createUser(User user);

	/**
	 * Updates a user to the database.
	 * 
	 * @param user  the user to update
	 * @return userid the user id of the updated user
	 */
	public String updateUser(User user);

	/**
	 * Adds a group to the database.
	 * 
	 * @param group  the group to add
	 * @return groupID the group id of the created group
	 */
	public String createGroup(Group group);

	/**
	 * Updates a group in the database.
	 * 
	 * @param group  the group to update
	 * @return groupID the group id of the updated group
	 */
	public String updateGroup(Group group);

	/**
	 * Adds an existing user to an existing group.
	 * 
	 * @param groupName  name of the existing group
	 * @param userName  user to add
	 */
	public void addUserToGroup(String groupName, String userName);

	/**
	 * Adds a document to an existing bibtex entry
	 * 
	 * @param doc
	 * @param resourceHash
	 * @return The hash of the created document.
	 */
	public String addDocument(Document doc, String resourceHash);

	/**
	 * Get a document from an existing Bibtex entry
	 * @param userName 
	 * @param resourceHash 
	 * @param fileName 
	 * 
	 * @return document
	 */
	public Document getDocument(String userName, String resourceHash, String fileName);

	/**
	 * Deletes an existing document out of the DB
	 * 
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public void deleteDocument(String userName, String resourceHash, String fileName);

	/**
	 * Adds an InetAddress (IP) with the given status to the list of addresses.
	 * Note that an InetAddress has exactly one status - so adding the status 
	 * really means setting it. TODO: this should be cleaned - either by renaming 
	 * the method to "setInetAddressStatus" or by allowing several states for an 
	 * InetAddress (use case?).
	 * 
	 * @param address - the address for which we want to set the status
	 * @param status  - the status of the address (e.g. "blocked") 
	 * @author rja
	 */
	public void addInetAddressStatus (InetAddress address, InetAddressStatus status);

	/** 
	 * Returns the current status of an InetAddress.
	 * 
	 * @param address - the InetAddress which status to get
	 * @return The status of the given address.
	 * @author rja
	 */
	public InetAddressStatus getInetAddressStatus (InetAddress address);

	/** Removes the address from the the list of stati for InetAddresses. Since
	 * currently one address can have only one status, it is not neccessary to
	 * say which status for that address should be removed. TODO: see comment 
	 * for {@link #addInetAddressStatus(InetAddress, InetAddressStatus)}.
	 * 
	 * @param address - the InetAddress which should be removed from the status list.
	 * @author rja
	 */
	public void deleteInetAdressStatus (InetAddress address);

	/**
	 * Retrieve statistics
	 * 
	 * @param resourceType - the requested resource type
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name
	 * @param constraint - a possible contstraint on the statistics
	 * @param search - search string
	 * @param tags - a list of tags
	 * @return an int representing a statistical information
	 * @author dbe
	 */
	public int getStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, StatisticsConstraint constraint, String search, List<String> tags);

	/**
	 * Retrieve relations
	 * 
	 * @param resourceType - the reqtested resourcetype
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name
	 * @param regex - a regex to possibly filter the relatons retrieved
	 * @param tags - a list of tags which shall be part of the relations
	 * @param status - the conceptstatus, i.e. all, picked or unpicked
	 * @param start - start index
	 * @param end - end index
	 * @return a list of concepts, i.e. tags containing their assigned subtags
     * @author dbe
	 */
	public List<Tag> getConcepts(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end);

	/**
	 * Retrieve relations
	 * 
	 * @param conceptName - the supertag of the concept
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name	
	 * @return a concept, i.e. a tag containing its assigned subtags
     * @author sts
	 */
	public Tag getConceptDetails(String conceptName, GroupingEntity grouping, String groupingName);

	/**
	 * Create a new relation/concept
	 * 
	 * @param concept - the new concept
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name
	 * @return the name of the superconcept-tag, note: if a concept already exists with the given name
	 * it will be replaced
	 * @author sts
	 */
	public String createConcept(Tag concept, GroupingEntity grouping, String groupingName);

	/**
	 * Update an existing relation/concept
	 * 
	 * @param concept - the concept to update
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name	
	 * @return the name of the superconcept-tag 
	 * @author sts
	 */
	public String updateConcept(Tag concept, GroupingEntity grouping, String groupingName);

	/**
	 * Delete an existing concept
	 * 
	 * @param concept - the concept to delete
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name	 
     * @author sts
	 */
	public void deleteConcept(String concept, GroupingEntity grouping, String groupingName);

	/**
	 * Delete an existing relation
	 * 
	 * @param upper - the concept to delete
	 * @param lower - the subtag of the conceptname
	 * @param grouping - grouping entity
	 * @param groupingName - the grouping name	 
     * @author sts
	 */
	public void deleteRelation(String upper, String lower, GroupingEntity grouping, String groupingName);

	/**
	 * @param tags
	 * @param order
	 * @param start
	 * @param end TODO
	 * @return list of user
	 */
	public List<User> getUsers (List<String> tags, Order order, final int start, int end);

	/**
	 * Returns all users that are classified to the specified state by
	 * the given classifier 
	 * 
	 * @param classifier something that classfied the user
	 * @param status the state to which the user was classified
	 * @return list of classified users
	 * @param interval 
	 * @author sts
	 */
	public List<User> getClassifiedUsers(Classifier classifier, SpamStatus status, int interval);

	/**
	 * Returns number of classfied user 
	 * 
	 * @param classifier the classifier
	 * @param status the status classifed
	 * @param interval the time period of classifications 
	 * @return count of users
	 */
	public int getClassifiedUserCount(Classifier classifier, SpamStatus status, int interval);

	/**
	 * Returns the value of the specified classifier setting
	 * 
	 * @param key The key for which to retrieve the value for
	 * @return The setting value
	 */
	public String getClassifierSettings(ClassifierSettings key);

	/**
	 * Updates the specified classifier setting
	 * 
	 * @param key the setting to update
	 * @param value the new setting value
	 */
	public void updateClassifierSettings(ClassifierSettings key, final String value);	

	/**
	 * Returns the history of classifier predictions 
	 * 
	 * @param userName the user  
	 * @return prediction history
	 */
	public List<User> getClassifierHistory(String userName);

	/**
	 * Retrieves a comparison of classification results
	 * of admins and the automatic classifier
	 * 
	 * @param interval the time period of classifications
	 * @return Userlist with spammer flag of admin and prediction of classifier 
	 */
	public List<User> getClassifierComparison(int interval);	
}