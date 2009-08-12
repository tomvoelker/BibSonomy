package org.bibsonomy.database.managers.hash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.enums.Order;

/**
 * Represents one hash element in the map
 * 
 * @param <L>
 *            Type of the fetched result entities
 * @param
 *            <P>
 *            Type of the param object
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public abstract class HashElement<L, P extends GenericParam> {

	protected static final Log log = LogFactory.getLog(HashElement.class);
	protected final GeneralDatabaseManager generalDb;
	protected final GroupDatabaseManager groupDb;

	/**
	 * abstract base constructs for a hash element
	 */
	public HashElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.groupDb = GroupDatabaseManager.getInstance();
	}

	/**
	 * Handles the request
	 */
	public abstract List<L> perform(final P param, final DBSession session);

	private boolean date = false;
	private boolean description = false;
	private boolean tagIndex = false;
	private boolean hash = false;
	private boolean search = false;
	private boolean loginNeeded = false;
	private boolean requestedUserName = false;
	private boolean requestedGroupName = true;
	private boolean numSimpleConceptsOverNull = false;
	private boolean numSimpleTagsOverNull = false;
	private boolean numTransitiveConceptsOverNull = false;
	private Set<Order> orders;

	private GroupingEntity groupingEntity = GroupingEntity.ALL;

	public boolean isOrderValid(Order order) {
		return order == null && getOrders().isEmpty() || getOrders().contains(order);
	}

	public void addToOrders(Order order) {
		getOrders().add(order);
	}

	public boolean isDate() {
		return this.date;
	}

	public void setDate(boolean date) {
		this.date = date;
	}

	public boolean isDescription() {
		return this.description;
	}

	public void setDescription(boolean description) {
		this.description = description;
	}

	public boolean isTagIndex() {
		return this.tagIndex;
	}

	public void setTagIndex(boolean tagIndex) {
		this.tagIndex = tagIndex;
	}

	public boolean isHash() {
		return this.hash;
	}

	public void setHash(boolean hash) {
		this.hash = hash;
	}

	public boolean isSearch() {
		return this.search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public boolean isRequestedUserName() {
		return this.requestedUserName;
	}

	public void setRequestedUserName(boolean requestedUserName) {
		this.requestedUserName = requestedUserName;
	}

	public boolean isRequestedGroupName() {
		return this.requestedGroupName;
	}

	public void setRequestedGroupName(boolean requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}

	public GroupingEntity getGroupingEntity() {
		return this.groupingEntity;
	}

	public void setGroupingEntity(GroupingEntity groupingEntity) {
		this.groupingEntity = groupingEntity;
	}

	public boolean isNumSimpleConceptsOverNull() {
		return this.numSimpleConceptsOverNull;
	}

	public void setNumSimpleConceptsOverNull(boolean numSimpleConceptsOverNull) {
		this.numSimpleConceptsOverNull = numSimpleConceptsOverNull;
	}

	public boolean isNumSimpleTagsOverNull() {
		return this.numSimpleTagsOverNull;
	}

	public void setNumSimpleTagsOverNull(boolean numSimpleTagsOverNull) {
		this.numSimpleTagsOverNull = numSimpleTagsOverNull;
	}

	public boolean isNumTransitiveConceptsOverNull() {
		return this.numTransitiveConceptsOverNull;
	}

	public void setNumTransitiveConceptsOverNull(boolean numTransitiveConceptsOverNull) {
		this.numTransitiveConceptsOverNull = numTransitiveConceptsOverNull;
	}

	public boolean isLoginNeeded() {
		return this.loginNeeded;
	}

	public void setLoginNeeded(boolean loginNeeded) {
		this.loginNeeded = loginNeeded;
	}

	private Set<Order> getOrders() {
		if (this.orders == null) {
			this.orders = new HashSet<Order>();
		}
		return this.orders;
	}
}