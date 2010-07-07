package resources;



import helpers.parser.TagStringLexer;
import helpers.parser.TagStringParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * This class stores tag assignments to resources, this includes
 * <ul>
 *  <li> all tags added to the resource
 *  <li> all relations added to the resource
 *  <li> an upper or a lower tag, if a user tags a tag (i.e. a tag page of bibsonomy)
 *  <li> all users the post should be copied to
 *  </ul>
 *
 */
@Deprecated
public class Tag implements Cloneable {
	
	private Set<String> tags;                // the tags as a set
	private Set<TagRelation> tagrelations;   // all tag relations as a set
	private Set<String> forUsers;            // all users this post has to be copied to
	private String lower;                    // the lower tag when "tagging tags"
	private String upper;		             // the upper tag when "tagging tags"
	
	public static final int MAX_TAGS_ALLOWED = 100; // more tags are not allowed (they get lost)
	
	public static final String EMPTY_TAG    = "system:unfiled";	
	public static final String IMPORTED_TAG = "imported";
	
	/**
	 * Cleans all attributes of this object and generates new empty sets.
	 */
	private void cleanMe() {
		tags         = new TreeSet<String>();
		tagrelations = new TreeSet<TagRelation>();
		forUsers     = new TreeSet<String>();
		lower        = null;
		upper        = null;
	}

	/**
	 * Simple constructor which generates new sets for tags, relations and users.
	 */
	public Tag(){
		cleanMe();
	}
	
	
	/**
	 * Initializes the object with the tags, relations and "for:" users found in the tag string. 
	 * The tag string is parsed by the {@link helpers.parser.TagStringParser}.
	 * 
	 * @param tagString the string which should be parsed
	 */
	public Tag(String tagString) {
		this();
		parse(tagString);		
	}
			
	
	/*
	 * TODO: filtering and tag checking should be done in clean
	 * filter manner (ask chs)
	 * routines affected: all using filterPlus, isForTag, isValidTag, ...
	 */
	
	/**
	 * Parses the given tagstring and
	 * <ul>
	 *  <li> extracts relations
	 *  <li> adds tags and relations
	 *  <li> stops, if too many tags (TODO: really?)
	 * </ul>
	 * 
	 * @param tagString the string to be parsed
	private void parse(String tagString) {
		if (tagString != null) {
			StringReader    r = new StringReader (tagString);
			TagStringLexer  l = new TagStringLexer(r);
			TagStringParser p = new TagStringParser(l, this);
			try {
				p.tagstring();
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	 */
	
	/*
	 * This method you need for Antlr 3.0.1 (3.05b)
	 * 
	 */
	private void parse(String tagString) {
        if (tagString != null) {
                StringReader reader = new StringReader (tagString);
                try {
                        CommonTokenStream tokens = new CommonTokenStream();
                        tokens.setTokenSource(new TagStringLexer(new ANTLRReaderStream(reader)));
                        TagStringParser parser = new TagStringParser(tokens, this);
                        parser.tagstring();
                } catch (RecognitionException e) {
                        System.out.println(e);
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
	}
	
	/* ******************************************************* * 
	 * these methods are used to implement the tagging of tags *
	 * ******************************************************* */

	/*
	 * adds the relation tag->upper or lower->tag, depending upon which 
	 * of upper or lower is set
	 * TODO: when calling addTagRelation, it tries to add both tags with 
	 *       addTag() - but this has already been done
	 */
	private boolean addTTRelation (String tag) {
		if (upper != null) {
			return addTagRelation(tag, upper);
		} else if (lower != null) {
			return addTagRelation(lower, tag);
		}
		/* 
		 * if we do nothing, we return TRUE, since this is not an error:
		 * there is just not "tag tagging" going on
		 * returning FALSE here would other methods (like addTag) to return
		 * FALSE, too
		 */ 
		return true;
	}
	
	/*
	 * goes through all tags in the tag set and adds the lower relation 
	 */
	private void addTTLowerRelation(){
		for(String tag: tags) {
			addTagRelation(lower, tag);
		}
	}
	
	/*
	 * goes through all tags in the tag set and adds the upper relation 
	 */
	private void addTTUpperRelation(){
		for(String tag: tags) {
			addTagRelation(tag, upper);
		}
	}

	/*
	 * adds the tag as upper tag for all tags included in this tag set
	 */
	public void addUpper(String upper) {
		if (isValidTag(upper) && !isForTag(upper)) {
			this.upper = filterPlus(upper);
			// only one conecpt is set
			this.lower = null;
			addTTUpperRelation();
		}
	}

	/*
	 * adds the tag as lower tag for all tags included in this tag set
	 */
	public void addLower(String lower){
		if (isValidTag(lower) && !isForTag(lower)) {
			this.lower = filterPlus(lower);
			// only one concept is set
			this.upper = null;
			addTTLowerRelation();
		}
	}

	
	
	/* *************************************** *
	 * check methods 
	 * *************************************** */
	/**
	 * checks, if a tag is valid
	 * <br>
	 * the tag must not
	 * <ul>
	 * <li> be null
	 * <li> be empty
	 * <li> contain an upper relation
	 * <li> contain a lower relation
	 * <li> contain whitespace
	 * </ul>
	 * <br>
	 * a tag additionally may not contain a + sign --
	 * this is NOT checked here so every method has
	 * to remove the plus sign by hand!
	 */
	private boolean isValidTag(String tag){
		return ! (tag == null || 
				  "".equals(tag) ||
				  tag.contains(TagRelation.LOWER_UPPER) ||
				  tag.contains(TagRelation.UPPER_LOWER) ||
				  tag.matches(".*\\s.*") 
		);
	}
	
	/**
	 * check if tag is a "for:" tag, i.e., starts with "for:"
	 * @param tag the tag to be tested
	 * @return <code>true</code> if the tag starts with "for:"
	 */
	private boolean isForTag(String tag) {
		return tag.startsWith("for:");
	}

	/**
	 * checks if the given tag is contained in this object's tag set
	 * 
	 * @param tag the tag to be tested
	 * @return <code>true</code> if the object contains the tag
	 */
	public boolean containsTag (String tag) {
		return tags.contains(tag);
	}
	
	/* *************************************** *
	 * filter methods 
	 * *************************************** */

	/**
	 * Replaces all occurences of '+' in the tag by '*'.
	 * @param tag the tag to be altered
	 * @return a new tag with all '+' replaced by '*'
	 */
	private String filterPlus (String tag) { 
		return tag;//.replace('+','*').trim();
	}

	/* *************************************** *
	 * extract methods 
	 * *************************************** */
	/**
	 * extracts the user of a "for:" tag 
	 * (see also {@link #addForTag(String)}) 
	 * @param tag
	 * @return
	 */
	private String getForUser (String tag) {
		return tag.toLowerCase().substring(4,tag.length());
	}
	
	/* *************************************** *
	 * add methods
	 * *************************************** */
	
	/**
	 * creates a new relation lower->upper
	 * the relation is only created, if the tags are ok
	 * <p>
	 * <b>NOTE:</b> The tags are not added to the tag set,
	 * this has to be done manually
	 * 
	 * this is the only method, which changes the set "tagrelations"!
	 * 
	 * @param lower : lower tag of the relation
	 *        upper : upper tag of the relation
     * @return <tt>true</tt> if both tags are valid, are no for: tags and 
     *         the relation is valid and not already contained. 
	 */
	public boolean addTagRelation(String lower, String upper){
		// check tags, don't add for: tags!
		if (isValidTag(upper) && isValidTag(lower) && !isForTag(upper) && !(isForTag(lower))) {
			/* 
			 * filter tags (s/\+/\* /) and 
			 * generate tag relation
			 */
			TagRelation relation = new TagRelation(filterPlus (lower), filterPlus (upper));
			if (relation.isValid()) {
				return tagrelations.add(relation); 
			}
		}
		return false;
	}
	
	/**
	 * Checks, cleans and adds a tag to the tagset.
	 * Additionally for:tags are treated and an existing
	 * tag-tag-relation (for tagging tags) is added (if needed).
	 * 
	 * @param tag the tag to be added
	 * @return <code>true</code> if 
	 */
	public boolean addTag(String tag){
		// if the tag is not null and not already contained ...
		if (tag != null && !containsTag(tag)) {
			if (isForTag(tag)) {
				// add user to copy this tag to list,
				// don't add tag to list, this has to be done in BookmarkHandler
				return forUsers.add(getForUser(tag));
			}
			return addTagRobust(tag) && addTTRelation(tag);
		}
		return false;
	}

	/**
	 * Adds the tag "from:<code>user</code>" to the set of tags. 
	 * It is not checked, if user name is valid.
	 * 
	 * @param user the name of the user 
	 * @return <code>true</code> if the user name is not <code>null</null>
	 * and the tag could be added
	 */
	public boolean addFromTag (String user) {
		return user != null && addTagRobust("from:" + user);
	}
	/**
	 * Adds the tag "for:<code>user</code>" to the set of tags.
	 * It is not checked, if the user name is valid.
	 * 
	 * @param user the name of the user
	 * @return <code>true</code> if the user name is not <code>null</null>
	 * and the tag could be added
	 */
	public boolean addForTag (String user) {
		return user != null && addTagRobust("for:" + user);
	}

	/** 
	 * first checks, if the tag is valid, then cleans it
	 * (substitutes + by *) and finally adds it to the tag set
	 * 
	 * this should be the ONLY methods which alters the set "tags"!
	 * 
	 * @param tag the tag which should be added to the tag set
	 * @return <tt>true</tt> if the tag is valid and could be added (i.e., is
	 * not already contained)
	 */
	private boolean addTagRobust(String tag) {
		return isValidTag(tag) && tags.add(filterPlus(tag));
	}
	
	
	
	
	/* *************************************************** *
	 * the getter methods for the main sets of this object *
	 * *************************************************** */
	
	/**
	 * Gets the set of users. Users are extracted from tags which start
	 * with "for:"; everything after "for": is regarded as a user name 
	 * and collected. This methods returns this set of users.
	 * 
	 * @return the set of users
	 */
	public Set<String> getForUsers() {
		return forUsers;
	}
	
	/**
	 * Gets the set of tag relations. 
	 * 
	 * @return the set of tag relations
	 */
	public Set<TagRelation> getTagrelations(){
		return tagrelations;
	}

	/**
	 * Gets the set of tags.
	 * 
	 * @return the set of tags
	 */
	public Set<String> getTags(){
		return tags;
	}
	
	
	
	/* **************************************************** *
	 * setter methods for tags, relations and the tagstring *
	 * (which is parsed)
	 * TODO: check, if all these methods are really needed  *
	 * **************************************************** */
	
	/**
	 * Cleans the object, parses the tag string and generates the sets of tags, relations and users.
	 * <br>
	 * Note that all attributes of the object are deleted, since a new tag string represents a new
	 * tag object. 
	 * @param tagString the string to be parsed and "converted" into a tag object
	 */
	public void setTags(String tagString){
		// remove all existing users the resource should be copied to
		// TODO: maybe call constructor here to ensure, that EVERYTHING is deleted (is this wanted?)
		cleanMe();
		// parse the given tag string and add all included tags and tagrelations
		parse(tagString);
	}
		
	/*
	 * TODO: when is a tag object valid?
	 */
	/**
	 * Checks if the tag object is valid.
	 * 
	 * @return <code>true</code> if the tag object contains at least one tag. 
	 */
	public boolean isValid() {
		return !tags.isEmpty();
	}

	
	/** 
	 * Returns a copy of this object<br>
	 * The copy does NOT contain "for:" users or (any!) relations - 
	 * only the tag set.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Tag copy = new Tag();
		// copy tag list
		copy.tags = new TreeSet<String>(tags);
		return copy;
	}

	
	/**
	 * Generates a string representation of the tag object. The string contains all
	 * tags separated by space and additionally all "for:" tags.
	 * 
	 * @return a String representation of this object including "for:" tags
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(String tag: tags){
			buffer.append(tag + " ");
		}
		for(String forUser: forUsers){
			buffer.append("for:" + forUser + " ");	
		}
		return buffer.toString();
	}

	/**
	 * Generates a string representation of the tag object. The string contains all
	 * tags separated by space.
	 * 
	 * @return a string representation of this object
	 * @see #toString()
	 */
	public String getTagString(){
		StringBuffer buffer = new StringBuffer();
		for(String tag: tags){
			buffer.append(tag + " ");
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the number of tags.
	 * 
	 * @return the number of tags this object contains
	 */
	public int tagCount () {
		return this.tags.size();
	}

}