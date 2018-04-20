package org.bibsonomy.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.enums.Gender;

/**
 * A PersonMatch object contains the id's of two persons which might be equal and a flag if they are equal
 *
 * @author jhi
 */
public class PersonMatch implements Serializable, Comparable<PersonMatch> {
	
	private static final long serialVersionUID = -470932185819510145L;
	public static final int denieThreshold = 5;
	
	private Person person1;
	private Person person2;
	private int state; //0 open, 1 denied, 2 already merged
	private int matchID;
	private List<String> userDenies;
	private List<Post> person1Posts;
	private List<Post> person2Posts;
	
	/**
	 * @return the matchID
	 */
	public int getMatchID() {
		return this.matchID;
	}
	/**
	 * @param matchID the matchID to set
	 */
	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}
	/**
	 * @return the person1
	 */
	public Person getPerson1() {
		return this.person1;
	}
	/**
	 * @param person1 the person1 to set
	 */
	public void setPerson1(Person person1) {
		this.person1 = person1;
	}
	/**
	 * @return the person2
	 */
	public Person getPerson2() {
		return this.person2;
	}
	/**
	 * @param person2 the person2 to set
	 */
	public void setPerson2(Person person2) {
		this.person2 = person2;
	}
	/**
	 * @return the deleted
	 */
	public int getState() {
		return this.state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the userDenies
	 */
	public List<String> getUserDenies() {
		return this.userDenies;
	}
	/**
	 * @param userDenies the userDenies to set
	 */
	public void setUserDenies(List<String> userDenies) {
		this.userDenies = userDenies;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PersonMatch match) {
		if ((this.person1.getPersonId().equals(match.getPerson1().getPersonId()) && this.person2.getPersonId().equals(match.getPerson2().getPersonId()))
				|| (this.person1.getPersonId().equals(match.getPerson2().getPersonId()) && this.person2.getPersonId().equals(match.getPerson1().getPersonId()))) {
			return 0;
		}
		return -1;
	}
	/**
	 * @return the person1Posts
	 */
	public List<Post> getPerson1Posts() {
		return this.person1Posts;
	}
	/**
	 * @param person1Posts the person1Posts to set
	 */
	public void setPerson1Posts(List<Post> person1Posts) {
		this.person1Posts = person1Posts;
	}
	/**
	 * @return the person2Posts
	 */
	public List<Post> getPerson2Posts() {
		return this.person2Posts;
	}
	/**
	 * @param person2Posts the person2Posts to set
	 */
	public void setPerson2Posts(List<Post> person2Posts) {
		this.person2Posts = person2Posts;
	}
	
	/**
	 * returns a map that contains for each match in matches a list
	 * @param matches
	 * @return
	 */
	public static Map<Integer, PersonMergeFieldConflict[]> getMergeConflicts(List<PersonMatch> matches){
		//A map with a list of conflicts for every match of a person
		//If a match does not have any conflict it has an entry with an empty list
		Map<Integer, PersonMergeFieldConflict[]> map = new HashMap<Integer, PersonMergeFieldConflict[]>();
		for(PersonMatch match : matches){
			//the list of all fields that are holding a conflict
			List<PersonMergeFieldConflict> conflictFields = new LinkedList<PersonMergeFieldConflict>();
			try {
				for (String fieldName : Person.fieldsWithResolvableMergeConflicts) {
					PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
					Object person1Value = desc.getReadMethod().invoke(match.getPerson1());
					Object person2Value = desc.getReadMethod().invoke(match.getPerson2());
					if (person1Value != null && person2Value != null) {
						//test if the values are different and add them to the list
						if (person1Value.getClass().equals(String.class)) {
							if (!((String) person1Value).equals((String) person2Value)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, (String)person1Value, (String)person2Value));
							}
						} else if (person1Value.getClass().equals(PersonName.class)) {
							String person1Name = ((PersonName) person1Value).getLastName() + ", " +((PersonName) person1Value).getFirstName();
							String person2Name = ((PersonName) person2Value).getLastName() + ", " +((PersonName) person2Value).getFirstName();
							if (!person1Name.equals(person2Name)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, person1Name, person2Name));
							}
						} else if (person1Value.getClass().equals(Gender.class)) {
							if (!((Gender) person1Value).equals((Gender) person2Value)) {
								conflictFields.add(new PersonMergeFieldConflict(fieldName, ((Gender) person1Value).name(), ((Gender) person2Value).name()));
							}
						} else {
							System.err.println(
									"Missing " + person1Value.getClass() + " class case for merge conflict detection");
						}
					}
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
					| IntrospectionException e) {
				System.err.println(e);
			}
			PersonMergeFieldConflict[] p = new PersonMergeFieldConflict[conflictFields.size()];
			conflictFields.toArray(p);
			map.put(new Integer(match.getMatchID()), p);
		}
		return map;
	}
	
	
}
