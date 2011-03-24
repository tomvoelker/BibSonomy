package org.bibsonomy.database.systemstags;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.systemstags.executable.ExecutableSystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * @author sdo
 * @version $Id$
 */
public class SystemTagsExtractor {

    /**
     * Removes all systemTags from a given set of tags
     * Warning: the given Collection must support iterator.remove()
     * @param tags = the set of tags
     * @return number of tags, that were removed
     */
    public static int removeAllSystemTags(final Collection<Tag> tags) {
	int removeCounter = 0;
	for (final Iterator<Tag> iter= tags.iterator(); iter.hasNext();) {
	    final Tag tag = iter.next();
	    if (SystemTagsUtil.isSystemTag(tag.getName())) {
		iter.remove();
		removeCounter++;
	    }
	}
	return removeCounter;
    }

    /**
     * Removes all non-systemTags from a given set of tagNames
     * Warning: the given Collection must support iterator.remove()
     * @param tagNames = the set of tagNames
     * @return number of tags, that were removed
     */
    public static int removeAllNonSystemTags(final Collection<String> tagNames) {
	int removeCounter = 0;
	for  (final Iterator<String> iter = tagNames.iterator(); iter.hasNext();) {
	    final String tagName = iter.next();
	    if ( !SystemTagsUtil.isSystemTag( tagName ) ) {
		iter.remove();
		removeCounter++;
	    }
	}
	return removeCounter;
    }

    /**
     * Returns a new List containing the names of all systemTags of a given Collection of tagNames
     * 
     * @param tagNames collection of tags
     * @return a new list with all system tags which are contained in input tags
     */
    public static List<String> extractSystemTags(final Collection<String> tagNames) {
	final List<String> sysTags = new LinkedList<String>();
	for( final String tagName : tagNames ) {
	    if (SystemTagsUtil.isSystemTag(tagName)) {
		sysTags.add(tagName);
	    }
	}
	return sysTags;
    }

    /**
     * Returns a list of all executable systemTags of a post that have not previously been executed
     * @param alreadyExecutedTags - the list of all tags, that have been executed and therefore shall not be used again
     * @param tags
     * @return a list 
     */
    public static List<ExecutableSystemTag> extractExecutableSystemTags(final Set<Tag> tags, final Set<Tag> alreadyExecutedTags) {
	final List<ExecutableSystemTag> sysTags = new ArrayList<ExecutableSystemTag>();
	for (final Tag tag : tags) {
	    final ExecutableSystemTag stt = SystemTagsUtil.createExecutableTag(tag);
	    if (present(stt) && !alreadyExecutedTags.contains(stt)) {
		sysTags.add(stt);
	    }
	}
	return sysTags;
    }

    /**
     * Parses a given string for system tags
     * @param search = a string to be searched for system tags
     * @param delim = the delimiter by which the string is to be tokenized
     * @return a list of Strings that were recognized as SearchSystemTags
     */
    public static List<String> extractSearchSystemTagsFromString(final String search, final String delim) {
	final List<String> sysTags = new ArrayList<String>();
	if (search == null) {
	    return sysTags;
	}
	for (String s : search.split(delim)) {
	    s = s.trim();
	    if (SystemTagsUtil.isSearchSystemTag(s)) {
		sysTags.add(s);
	    }
	}
	return sysTags;
    }


     /**
     * Go through a collection of tags and removes all hidden System Tags
     * @param <T>
     * @param posts
     * @param loginUserName
     */
    public static <T extends Resource> void removeHiddenSystemTags(Collection<Tag> tags) {
	for (final Iterator<Tag> iter = tags.iterator(); iter.hasNext();) {
	    Tag tag = iter.next();
	    SystemTag sysTag = SystemTagsUtil.createSystemTag(tag);
	    if (present(sysTag) && sysTag.isToHide()) {
		// We have found a system tag that should be hidden
		iter.remove();
	    }
	}
    }
   
    
    private static <T extends Resource> void separateHiddenSystemTags(Post<T> post) {
	for (final Iterator<Tag> iter = post.getTags().iterator(); iter.hasNext();) {
	    Tag tag = iter.next();
	    SystemTag sysTag = SystemTagsUtil.createSystemTag(tag);
	    if (present(sysTag) && sysTag.isToHide()) {
		// We have found a system tag that should be hidden
		post.addHiddenSystemTag(tag);
	    } else {
		post.addVisibleTag(tag);
	    }
	}
	
    }
    
    /**
     * Go through a list of posts and removes all hidden System Tags
     * if the loginUser is not the posts owner
     * @param <T>
     * @param posts
     * @param loginUserName
     */
    public static <T extends Resource> void handleHiddenSystemTags(Collection<Post<T>> posts, String loginUserName) {
	for (Post<T> post: posts) {
	    if (!present(loginUserName) || !loginUserName.equals(post.getUser().getName())) {
		removeHiddenSystemTags(post.getTags());
	    } else {
		post.setHiddenSystemTags(new HashSet<Tag>());
		post.setVisibleTags(new HashSet<Tag>());
		separateHiddenSystemTags(post);
	    }
	}
    }

}
