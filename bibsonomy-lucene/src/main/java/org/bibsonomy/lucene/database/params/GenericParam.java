package org.bibsonomy.lucene.database.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

/**
 * This is the most generic param. All fields which are not specific to
 * bookmarks or BibTexs are collected here. The parameter-objects are used by
 * iBATIS in the SQL-statements to fill in values; they are put at the position
 * of ?-marks.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Christian Kramer
 * @author Christian Schenk
 * @version $Id$
 */
public abstract class GenericParam {
	/**
	 * A set of tags
	 */
	private Set<Tag> tags;
	
	/**
	 * A single tag
	 */
	private Tag tag;
	
	/**
	 * A tag name
	 */
	private String tagName;
	
	/**
	 * FIXME what is this for?
	 */
	private String title;
	
	/**
	 * List of (tagname, index)-pairs, where tagname can be both a name of a tag
	 * or concept.
	 */
	private  List<TagIndex> tagIndex;
	
	/**
	 * corresponds to -->[tagName]
	 */
	private int numTransitiveConcepts;
	
	/**
	 * corresponds to ->[tagName] 
	 */
	private int numSimpleConcepts;
	
	/**
	 * corresponds to [tagName]
	 */
	private int numSimpleTags;
	
	/**
	 * corresponds to [tagName]-> 
	 */
	private int numSimpleConceptsWithParent;
	
	/**
	 * corresponds to [tagName]-->
	 */
	private int numSimpleConceptsWithAncestors;
	
	/**
	 * corresponds to <->[tagName]
	 */
	private int numCorrelatedConcepts;
	
	/** 
	 * List of the groups the user belongs to 
	 * 
	 * we store this as a set, because a user can of course 
	 * be only just once a member of each group; but as IBATIS
	 * expects a List to loop over, getGroups returns a List
	 * 
	 * */
	private Set<Integer> groups;
	
	
	/**
	 * List of groupnames the user belongs to. 
	 */
	private Set<String> groupNames;
	
	/**
	 * Should tagnames (names of tags and concepts) be case sensitive; by
	 * default this is false, i.e. tagnames aren't case sensitive.
	 */
	private boolean caseSensitiveTagNames;
	/** creation-date */
	private Date date;
	/** If a contentId is updated or deleted we need this as reference */
	private int requestedContentId;
	/**
	 * The hash of a post, e.g. a bookmark or a BibTex TODO: really of the post
	 * and not of the resource? and for what kind of hash is this used? isn't it
	 * resource-specific and shouldn't it be set in the resource-field?
	 */
	private String hash;

	/**
	 * This is used to restrict simHashes, i.e. to limit the overall resultset.
	 * The default simHash is defined in {@link HashID}.
	 */
	private HashID simHash;	

	/* modified search parameter */
	private String search;

	/* not modified search parameter */
	private String rawSearch;
	
	/** This is the current user. */
	private String userName;
	private String description;
	private String extension;
	/**
	 * The current user, who would be identified by userName, can look at other
	 * people's content. This requested user is identified by this string.
	 */
	private String requestedUserName;
	/** ID of a group; by default it's invalid */
	private int groupId;
	/** If we're searching for a group this is used for the name of the group */
	private String requestedGroupName;
	/** The type of a group is by default public */
	private GroupID groupType;
	/** The SQL-Limit which is by default 10 */
	private int limit;
	/** The SQL-Offset which is by default 0 */
	private int offset;
	/* FIXME: what the hell does the following comment mean? */
	/*is a user a friend of person x, true will be true*/
	private  boolean friendOf;
	/** The type of a ID is by default DS_CONTENT_ID * */
	private ConstantID idsType;
	private int newContentId;
	private String url;
	private ConstantID contentType;
	
	private Order order;
	private GroupingEntity grouping;
	private FilterEntity filter;
	private SearchEntity searchEntity;
	/*
     * the days of a popular resource
     * TODO: please document use better. This are not really
     * the "days", but more or less the position in the list
     * of available days?!
	 */
	private int days;
	
	/*
	 * retrieve resources via their bibtexkey 
	 */
	private String bibtexKey;
	
	private Date fromDate;
	private Date toDate;

	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.numSimpleTags = 0;
		this.numSimpleConcepts = 0;
		this.numTransitiveConcepts = 0;
		this.numSimpleConceptsWithParent = 0;
		this.numSimpleConceptsWithAncestors = 0;
		this.numCorrelatedConcepts = 0;
		this.caseSensitiveTagNames = false;
		this.groupId = GroupID.INVALID.getId();
		this.groupType = GroupID.PUBLIC;
		this.idsType = ConstantID.IDS_UNDEFINED_CONTENT_ID;
		this.limit = 10;
		this.offset = 0;
		this.friendOf=false;
		this.simHash = HashID.SIM_HASH; // the default hash type
		
		this.grouping = GroupingEntity.ALL;
		
		this.groups =  new HashSet<Integer>();
		this.groupNames =  new HashSet<String>();
		//when using this field the value of days must be greater 0 
		this.days = -1;
		
	}

	/**
	 * Implementations of this class will have to implement this method to
	 * identify their content type.
	 */
	public boolean isCaseSensitiveTagNames() {
		return this.caseSensitiveTagNames;
	}

	public void setCaseSensitiveTagNames(boolean caseSensitive) {
		this.caseSensitiveTagNames = caseSensitive;
	}
	
	private void addToTagIndex(final String tagName) {
		this.tagIndex.add(new TagIndex(tagName, this.tagIndex.size() + 1));
	}

	public void addTagName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleTags++;
	}
	
	public void addSimpleConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConcepts++;
	}
	
	public void addTransitiveConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numTransitiveConcepts++;
	}
	
	public void addSimpleConceptWithParentName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConceptsWithParent++;
	}
	
	public void addSimpleConceptwithAncestorsName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConceptsWithAncestors++;
	}
	
	public void addCorrelatedConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numCorrelatedConcepts++;
	}	

	public List<TagIndex> getTagIndex() {
		//return Collections.unmodifiableList(this.tagIndex);
		return this.tagIndex;
	}

	public void setTagIndex(List<TagIndex> tagIndex) {
		this.tagIndex = tagIndex;
	}
	/**
	 * This is used to determine the max. amount of join-indices for the
	 * iteration of the join-index; e.g. if we're searching for tag names. If we
	 * have only one tag, we don't need a join index, if we got two then we need
	 * one, if we got three then we need two, and so on.<br/> We had to
	 * introduce this because iBATIS can only call methods that are true getter
	 * or setter. A call to tagIndex.size() is not possible. An attempt fails
	 * with "There is no READABLE property named 'size' in class
	 * 'java.util.ArrayList'".
	 */
	public int getMaxTagIndex() {
		// TODO: if this methods name was intuitive, size-1 should be returned
		// because tagIndex[size] is out of bounds
		return this.tagIndex.size();
	}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(GroupID groupType) {
		this.groupType = groupType;
	}

	public String getSearch() {
		return this.search;
	}

	public String getRawSearch() {
		return this.rawSearch;
	}

	public void setSearch(String search) {
		if (search != null) {
			this.rawSearch = search;
			this.search = search.replaceAll("([\\s]|^)([\\S&&[^-]])", " +$2");
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * returns the list of groups a user is member of
	 * 
	 * ATTENTION: this is not just a plain getter - we transform 
	 * the set of groups into a list of groups for IBATIS compatibility
	 * 
	 * @return a list of groups
	 */
	public List<Integer> getGroups() {
		return new ArrayList<Integer>(this.groups);
	}

	/**
	 * set the groups
	 *
	 * wrapper method for setting the groups set by a list
	 * 
	 * @param groups a LIST of group ids
	 */
	public void setGroups(Collection<Integer> groups) {
		this.groups = new HashSet<Integer>(groups);
	}	

	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	// TODO: what hash?, what for?, why in genericparam and not in
	// resource-field?
	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public int getSimHash() {
		return this.simHash.getId();
	}

	public void setSimHash(HashID simHash) {
		this.simHash = simHash;
	}


	// TODO: why in genericparam and not in resource-field?
	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	public void setRequestedUserName(String requestedUserName) {
		this.requestedUserName = requestedUserName;
	}

	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}

	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}

	public int getRequestedContentId() {
		return this.requestedContentId;
	}

	public void setRequestedContentId(int requestedContentId) {
		this.requestedContentId = requestedContentId;
	}

	public int getIdsType() {
		return this.idsType.getId();
	}

	public void setIdsType(ConstantID idsType) {
		this.idsType = idsType;
	}

	public int getNewContentId() {
		return this.newContentId;
	}

	public void setNewContentId(int newContentId) {
		this.newContentId = newContentId;
	}

	public Set<Tag> getTags() {
		return this.tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Tag getTag() {
		return this.tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		this.tagName = tag.getName();
	}

	public boolean getFriendOf() {
		return this.friendOf;
	}

	public void setFriendOf(boolean friendOf) {
		this.friendOf = friendOf;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getContentType() {
		return this.contentType.getId();
	}

	public void setContentType(ConstantID contentType) {
		this.contentType = contentType;
	}
	
	public void setContentTypeByClass(Class<? extends Resource> nativeContentType) {
		if (BibTex.class.isAssignableFrom(nativeContentType)) {
			setContentType(ConstantID.BIBTEX_CONTENT_TYPE);
		} else if (Bookmark.class.isAssignableFrom(nativeContentType)) {
			setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
		} else if (Resource.class.isAssignableFrom(nativeContentType)) {
			setContentType(ConstantID.ALL_CONTENT_TYPE);
		} else {
			throw new UnsupportedResourceTypeException();
		}
	}

	public String getTagName() {
		if (tag != null) {
			return tag.getName();
		}
		return this.tagName;
	}

	public void setTagName(String tagName) {
		this.tag = null;
		this.tagName = tagName;
	}

	public String getTagNameLower() {
		return this.getTagName().toLowerCase();
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public GroupingEntity getGrouping() {
		return this.grouping;
	}

	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}	

	public FilterEntity getFilter() {
		return this.filter;
	}

	public void setFilter(FilterEntity filter) {
		this.filter = filter;
	}

	public Integer getNumSimpleConcepts() {
		return this.numSimpleConcepts;
	}

	public Integer getNumSimpleTags() {
		return this.numSimpleTags;
	}

	public Integer getNumTransitiveConcepts() {
		return this.numTransitiveConcepts;
	}

	public int getNumSimpleConceptsWithParent() {
		return this.numSimpleConceptsWithParent;
	}

	public int getNumCorrelatedConcepts() {
		return this.numCorrelatedConcepts;
	}

	public int getNumSimpleConceptsWithAncestors() {
		return this.numSimpleConceptsWithAncestors;
	}

	public void setNumTransitiveConcepts(int numTransitiveConcepts) {
		this.numTransitiveConcepts = numTransitiveConcepts;
	}

	public void setNumSimpleConcepts(int numSimpleConcepts) {
		this.numSimpleConcepts = numSimpleConcepts;
	}

	public void setNumSimpleTags(int numSimpleTags) {
		this.numSimpleTags = numSimpleTags;
	}
	
	public void addGroup(Integer groupId) {
		this.groups.add(groupId);
	}
	
	public void addGroups(Collection<Integer> groups) {
		this.groups.addAll(groups);
	}

	public int getDays() {
		return this.days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public void setSearchEntity(SearchEntity searchEntity) {
		this.searchEntity = searchEntity;
	}

	public SearchEntity getSearchEntity() {
		return searchEntity;
	}

	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}

	public String getBibtexKey() {
		return bibtexKey;
	}
	
	/**
	 * add group ids and groupnames of groups this user may see
	 * 
	 * @param groups - a list of groups
	 */
	public void addGroupsAndGroupnames(Collection<Group> groups) {
		// add groupids + groupnames
		String groupName = "";
		for (Group g : groups) {
			this.groups.add(g.getGroupId());
			groupName = (g.getName()==null)?"group_"+g.getGroupId():g.getName().toLowerCase();
			// TODO warum kann der Gruppenname (im Test) null sein? 
			this.groupNames.add(groupName);
			// this.groupNames.add(g.getName().toLowerCase());
		}
	}

	/**
	 * @return the groupNames
	 */
	public Set<String> getGroupNames() {
		return this.groupNames;
	}

	/**
	 * @param groupNames the groupNames to set
	 */
	public void setGroupNames(Set<String> groupNames) {
		this.groupNames = groupNames;
	}
	
	/**
	 * Introspect the current param object and return a string representation of the form attribute = value
	 * for all attributes of this object.
	 * 
	 * @return - a string representation of the given object by introspection.
	 */
	public String toStringByReflection() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getToDate() {
		return toDate;
	}
}