package org.bibsonomy.database.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.ValidationUtils;

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
	/** 
	 * The ID of a group which by default is invalid.
	 */
	private int groupId;
	/** If we're searching for a group this is used for the name of the group */
	private String requestedGroupName;
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
	
	private final Map<String, SystemTag> systemTags;

	/**
	 * sets default values
	 */
	public GenericParam() {
		this.tagIndex = new ArrayList<TagIndex>();
		this.numSimpleTags = 0;
		this.numSimpleConcepts = 0;
		this.numTransitiveConcepts = 0;
		this.numSimpleConceptsWithParent = 0;
		this.numSimpleConceptsWithAncestors = 0;
		this.numCorrelatedConcepts = 0;
		this.caseSensitiveTagNames = false;
		/*
		 * set groupId to -1
		 */
		setGroupId(GroupID.INVALID);
		this.idsType = ConstantID.IDS_UNDEFINED_CONTENT_ID;
		this.limit = 10;
		this.offset = 0;
		this.friendOf = false;
		this.simHash = HashID.SIM_HASH; // the default hash type
		
		this.grouping = GroupingEntity.ALL;
		
		this.groups =  new HashSet<Integer>();
		this.groupNames =  new HashSet<String>();
		//when using this field the value of days must be greater 0 
		this.days = -1;
		
		this.systemTags = new HashMap<String, SystemTag>();
	}
	
	/**
	 * @return the caseSensitiveTagNames
	 */
	public boolean isCaseSensitiveTagNames() {
		return this.caseSensitiveTagNames;
	}

	/**
	 * @param caseSensitiveTagNames the caseSensitiveTagNames to set
	 */
	public void setCaseSensitiveTagNames(boolean caseSensitiveTagNames) {
		this.caseSensitiveTagNames = caseSensitiveTagNames;
	}

	private void addToTagIndex(final String tagName) {
		this.tagIndex.add(new TagIndex(tagName, this.tagIndex.size() + 1));
	}

	/**
	 * adds a tag
	 * @param tagName the name of the tag to add
	 */
	public void addTagName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleTags++;
	}
	
	/**
	 * TODO: improve docu
	 * @param tagName the name of the tag to add
	 */
	public void addSimpleConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConcepts++;
	}
	
	/**
	 * TODO: improve docu
	 * @param tagName the name of the tag to add
	 */
	public void addTransitiveConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numTransitiveConcepts++;
	}
	
	/**
	 * TODO: improve docu
	 * @param tagName
	 */
	public void addSimpleConceptWithParentName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConceptsWithParent++;
	}
	
	/**
	 * TODO: improve docu
	 * @param tagName
	 */
	public void addSimpleConceptwithAncestorsName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numSimpleConceptsWithAncestors++;
	}
	
	/**
	 * TODO: improve docu
	 * @param tagName
	 */
	public void addCorrelatedConceptName(final String tagName) {
		this.addToTagIndex(tagName);
		this.numCorrelatedConcepts++;
	}
	
	/**
	 * @return the tagIndex
	 */
	public List<TagIndex> getTagIndex() {
		return this.tagIndex;
	}

	/**
	 * @param tagIndex the tagIndex to set
	 */
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
	 * @return TODO
	 */
	public int getMaxTagIndex() {
		// TODO: if this methods name was intuitive, size-1 should be returned
		// because tagIndex[size] is out of bounds
		return this.tagIndex.size();
	}
	
	/**
	 * @return the search
	 */
	public String getSearch() {
		return this.search;
	}

	/**
	 * @return the rawSearch
	 */
	public String getRawSearch() {
		return this.rawSearch;
	}

	/**
	 * sets the rawsearch to search and prepares the search param for the database query
	 * 
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		if (search != null) {
			this.rawSearch = search;
			this.search = search.replaceAll("([\\s]|^)([\\S&&[^-]])", " +$2");
		}
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return this.limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return this.offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
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
	
	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * This setter sets the group id using the GroupID enum.
	 * 
	 * @param groupID
	 */
	public void setGroupId(final GroupID groupID) {
		this.groupId = groupID.getId();
	}

	/**
	 * If you need the ID of the friends group in a statement, 
	 * use this method.  
	 * 
	 * @return The ID of the friends group.
	 */
	public int getGroupTypeFriends() {
		return GroupID.FRIENDS.getId();
	}
	
	/**
	 * If you need the ID of the public group in a statement, 
	 * use this method.  
	 * 
	 * @return The ID of the public group.
	 */
	public int getGroupTypePublic() {
		return GroupID.PUBLIC.getId();
	}	
	
	// TODO: what hash?, what for?, why in genericparam and not in
	// resource-field?
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the simHash
	 */
	public int getSimHash() {
		return this.simHash.getId();
	}

	/**
	 * @param simHash the simHash to set
	 */
	public void setSimHash(HashID simHash) {
		this.simHash = simHash;
	}
	
	/**
	 * @return the requestedContentId
	 */
	public int getRequestedContentId() {
		return this.requestedContentId;
	}

	/**
	 * @param requestedContentId the requestedContentId to set
	 */
	public void setRequestedContentId(int requestedContentId) {
		this.requestedContentId = requestedContentId;
	}

	// TODO: why in genericparam and not in resource-field?
	/**
	 * @return the requestedUserName
	 */
	public String getRequestedUserName() {
		return this.requestedUserName;
	}

	/**
	 * @param requestedUserName the requestedUserName to set
	 */
	public void setRequestedUserName(String requestedUserName) {
		this.requestedUserName = requestedUserName;
	}

	/**
	 * @return the requestedGroupName
	 */
	public String getRequestedGroupName() {
		return this.requestedGroupName;
	}

	/**
	 * @param requestedGroupName the requestedGroupName to set
	 */
	public void setRequestedGroupName(String requestedGroupName) {
		this.requestedGroupName = requestedGroupName;
	}

	/**
	 * @return the id of the idsType
	 */
	public int getIdsType() {
		return this.idsType.getId();
	}
	
	/**
	 * @param idsType the idsType to set
	 */
	public void setIdsType(ConstantID idsType) {
		this.idsType = idsType;
	}

	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return this.tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the newContentId
	 */
	public int getNewContentId() {
		return this.newContentId;
	}

	/**
	 * @param newContentId the newContentId to set
	 */
	public void setNewContentId(int newContentId) {
		this.newContentId = newContentId;
	}

	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return this.tag;
	}

	/**
	 * sets also the tagName field to the name of the tag
	 * 
	 * @param tag the tag to set
	 */
	public void setTag(Tag tag) {
		this.tag = tag;
		this.tagName = tag.getName();
	}

	/**
	 * @return the friendOf
	 */
	public boolean getFriendOf() {
		return this.friendOf;
	}

	/**
	 * @param friendOf the friendOf to set
	 */
	public void setFriendOf(boolean friendOf) {
		this.friendOf = friendOf;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * TODO: change method name to contentTypeId
	 * 
	 * @return the id of the content type
	 */
	public int getContentType() {
		return this.contentType.getId();
	}

	/**
	 * TODO: change method name to getContentType
	 * 
	 * @return the content type
	 */
	public ConstantID getContentTypeConstant() {
		return this.contentType;
	}
	
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(ConstantID contentType) {
		this.contentType = contentType;
	}

	/**
	 * sets the content type by the nativeContentType param
	 * 
	 * @param nativeContentType
	 */
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

	/**
	 * @return if the tag is not null the name of the tag else the tagName
	 */
	public String getTagName() {
		if (tag != null) {
			return tag.getName();
		}
		return this.tagName;
	}

	/**
	 * sets the tag name to the param and sets the tag to null
	 * 
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tag = null;
		this.tagName = tagName;
	}

	/**
	 * FIXME: only used by test method
	 * @return {@link #getTagName()} lower case
	 */
	public String getTagNameLower() {
		return this.getTagName().toLowerCase();
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @return the grouping
	 */
	public GroupingEntity getGrouping() {
		return this.grouping;
	}

	/**
	 * @param grouping the grouping to set
	 */
	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	/**
	 * @return the filter
	 */
	public FilterEntity getFilter() {
		return this.filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(FilterEntity filter) {
		this.filter = filter;
	}

	/**
	 * @return the numSimpleConcepts
	 */
	public Integer getNumSimpleConcepts() {
		return this.numSimpleConcepts;
	}

	/**
	 * @return the numSimpleTags
	 */
	public Integer getNumSimpleTags() {
		return this.numSimpleTags;
	}
	
	/**
	 * @param numSimpleTags the numSimpleTags to set
	 */
	public void setNumSimpleTags(int numSimpleTags) {
		this.numSimpleTags = numSimpleTags;
	}

	/**
	 * @return numTransitiveConcepts
	 */
	public Integer getNumTransitiveConcepts() {
		return this.numTransitiveConcepts;
	}

	/**
	 * @return numSimpleConceptsWithParent
	 */
	public int getNumSimpleConceptsWithParent() {
		return this.numSimpleConceptsWithParent;
	}

	/**
	 * @return the numCorrelatedConcepts
	 */
	public int getNumCorrelatedConcepts() {
		return this.numCorrelatedConcepts;
	}

	/**
	 * @return the numSimpleConceptsWithAncestors
	 */
	public int getNumSimpleConceptsWithAncestors() {
		return this.numSimpleConceptsWithAncestors;
	}
	
	/**
	 * @param numTransitiveConcepts the numTransitiveConcepts to set
	 */
	public void setNumTransitiveConcepts(int numTransitiveConcepts) {
		this.numTransitiveConcepts = numTransitiveConcepts;
	}

	/**
	 * @param numSimpleConcepts the numSimpleConcepts to set
	 */
	public void setNumSimpleConcepts(int numSimpleConcepts) {
		this.numSimpleConcepts = numSimpleConcepts;
	}
	
	/**
	 * adds a group to the group list
	 * @param groupId the id of the group to add
	 */
	public void addGroup(Integer groupId) {
		this.groups.add(groupId);
	}
	
	/**
	 * adds all groups to the group list 
	 * @param groups the id's of the groups to add
	 */
	public void addGroups(Collection<Integer> groups) {
		this.groups.addAll(groups);
	}
	
	/**
	 * @return the searchEntity
	 */
	public SearchEntity getSearchEntity() {
		return this.searchEntity;
	}

	/**
	 * @param searchEntity the searchEntity to set
	 */
	public void setSearchEntity(SearchEntity searchEntity) {
		this.searchEntity = searchEntity;
	}

	/**
	 * @return the days
	 */
	public int getDays() {
		return this.days;
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(int days) {
		this.days = days;
	}

	/**
	 * @return the bibtexKey
	 */
	public String getBibtexKey() {
		return this.bibtexKey;
	}

	/**
	 * @param bibtexKey the bibtexKey to set
	 */
	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}

	/**
	 * add group ids and groupnames of groups this user may see
	 * 
	 * @param groups - a list of groups
	 */
	public void addGroupsAndGroupnames(Collection<Group> groups) {
		// add groupids + groupnames
		String groupName = "";
		for (final Group g : groups) {
			this.groups.add(g.getGroupId());
			groupName = g.getName()==null ? "group_" + g.getGroupId() : g.getName().toLowerCase();
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
	 * adds a system tag to the map
	 * uses the system tag name as key
	 * @param tag
	 */
	public void addToSystemTags(final SystemTag tag) {
		if (tag != null) {
			this.systemTags.put(tag.getName(), tag);
		}
	}
	
	/**
	 * adds a collection of system tags to system tags
	 * 
	 * @param systemTags	the collection to add to system tags
	 */
	public void addAllToSystemTags(final Collection<SystemTag> systemTags) {
		if (ValidationUtils.present(systemTags)) {
			for (final SystemTag tag : systemTags) {
				this.addToSystemTags(tag);
			}
		}
	}

	/**
	 * @return a map of system tags [tag name => systemTag, …]
	 */
	public Map<String, SystemTag> getSystemTags() {
		return Collections.unmodifiableMap(this.systemTags);
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
}