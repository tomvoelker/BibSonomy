package org.bibsonomy.wiki;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.AbstractWikiModel;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.ITableOfContent;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.util.TagStack;

import java.util.Collections;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.tags.AbstractTag;
import org.bibsonomy.wiki.tags.group.GroupImageTag;
import org.bibsonomy.wiki.tags.group.MembersTag;
import org.bibsonomy.wiki.tags.shared.ImageTag;
import org.bibsonomy.wiki.tags.shared.NameTag;
import org.bibsonomy.wiki.tags.shared.RegDateTag;
import org.bibsonomy.wiki.tags.shared.post.BookmarkListTag;
import org.bibsonomy.wiki.tags.shared.post.PublicationListTag;
import org.bibsonomy.wiki.tags.user.BirthdayTag;
import org.bibsonomy.wiki.tags.user.HobbieTag;
import org.bibsonomy.wiki.tags.user.InstitutionTag;
import org.bibsonomy.wiki.tags.user.InterestsTag;
import org.bibsonomy.wiki.tags.user.LocationTag;
import org.bibsonomy.wiki.tags.user.ProfessionTag;

/**
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public class CVWikiModel extends AbstractWikiModel {

	static {
		/* About-Me Tags */
		register(new LocationTag());
		register(new BirthdayTag());
		register(new InstitutionTag());
		register(new InterestsTag());
		register(new HobbieTag());
		register(new ProfessionTag());
		
		/* Group Tags */
		register(new MembersTag());
		register(new GroupImageTag());
		
		/* Shared Tags */
		register(new NameTag());
		register(new ImageTag());
		register(new RegDateTag());
		register(new BookmarkListTag());
		register(new PublicationListTag());
		
	}

	private static void register(final AbstractTag tag) {
		Configuration.DEFAULT_CONFIGURATION.addTokenTag(tag.getName(), tag);
	}

	private User requestedUser;
	private Group requestedGroup;

	private LogicInterface logic;

	private LayoutRenderer<Layout> layoutRenderer;

	/**
	 * Default Constructor
	 */
	public CVWikiModel() {
		super(Configuration.DEFAULT_CONFIGURATION, null, null);
	}

	@Override
	/*
	 * TODO: add Comment (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.model.AbstractWikiModel#appendHead(java.lang.String,
	 * int, boolean, int, int, int)
	 */
	public ITableOfContent appendHead(final String rawHead, final int headLevel, final boolean noToC, final int headCounter, final int startPosition, final int endPosition) {
		final TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		final WPTag headTagNode = new WPTag("h" + headLevel);
		final TagNode spanTagNode = new TagNode("span");
		// Example:
		// <h2><span class="mw-headline" id="Header_level_2">Header level
		// 2</span></h2>
		spanTagNode.addChildren(localStack.getNodeList());
		headTagNode.addChild(spanTagNode);
		final String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeDotUrl(tocHead);
		this.createTableOfContent(false);
		if (!noToC && headCounter > 3) {
			this.fTableOfContentTag.setShowToC(true);
		}
		if (this.fToCSet.contains(anchor)) {
			String newAnchor = anchor;
			for (int i = 2; i < Integer.MAX_VALUE; i++) {
				newAnchor = anchor + '_' + Integer.toString(i);
				if (!this.fToCSet.contains(newAnchor)) {
					break;
				}
			}
			anchor = newAnchor;
		}

		if (this.getRecursionLevel() == 1) {
			this.buildEditLinkUrl(this.fSectionCounter++);
		}

		spanTagNode.addAttribute("class", "mw-headline", true);
		spanTagNode.addAttribute("id", anchor, true);

		this.append(headTagNode);
		return this.fTableOfContentTag;
	}

	@Override
	public Set<String> getLinks() {
		return Collections.emptySet();
	}

	@Override
	public INamespace getNamespace() {
		return null;
	}

	@Override
	public void parseInternalImageLink(final String imageNamespace, final String rawImageLink) {
		// nothing to do
	}

	/**
	 * set the LogicInterface
	 * 
	 * @param logic
	 *            the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return the LogicInterface
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/**
	 * set the user
	 * 
	 * @param user
	 *            the user to add
	 */
	public void setRequestedUser(final User user) {
		this.requestedUser = user;
	}

	/**
	 * @return the user
	 */
	public User getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @return the layoutRenderer
	 */
	public LayoutRenderer<Layout> getLayoutRenderer() {
		return this.layoutRenderer;
	}

	/**
	 * @param layoutRenderer
	 *            the layoutRenderer to set
	 */
	public void setLayoutRenderer(final LayoutRenderer<Layout> layoutRenderer) {
		this.layoutRenderer = layoutRenderer;
	}

	/**
	 * @return the requestedGroup
	 */
	public Group getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup
	 *            the requestedGroup to set
	 */
	public void setRequestedGroup(final Group requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

}
