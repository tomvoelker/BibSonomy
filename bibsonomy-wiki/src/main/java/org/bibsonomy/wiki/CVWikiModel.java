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
//import org.bibsonomy.wiki.tags.shared.DesignTag;
import org.bibsonomy.wiki.tags.shared.ImageTag;
import org.bibsonomy.wiki.tags.shared.NameTag;
import org.bibsonomy.wiki.tags.shared.resource.BookmarkListTag;
import org.bibsonomy.wiki.tags.shared.resource.PublicationListTag;
import org.bibsonomy.wiki.tags.user.BirthdayTag;
import org.bibsonomy.wiki.tags.user.HobbyTag;
import org.bibsonomy.wiki.tags.user.InstitutionTag;
import org.bibsonomy.wiki.tags.user.InterestsTag;
import org.bibsonomy.wiki.tags.user.LocationTag;
import org.bibsonomy.wiki.tags.user.ProfessionTag;
import org.bibsonomy.wiki.tags.user.RegDateTag;

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
		register(new HobbyTag());
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
//		register(new DesignTag());
		
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

	/*
	 * defines the look and feel of the section headlines. can be changed by the span class mw-headline.
	 * 
	 * @param rawHead a pure title from the wiki syntax, without the enclosing =
	 * @param headLevel the number of =, indicating the position in the section hierarchy of this title
	 * @param noToC good question. TODO: FIX THIS! Welcher Wahnsinnige verwendet eigentlich negierte boolsche Variablen?!
	 * @param headCounter
	 * 
	 * @see info.bliki.wiki.model.AbstractWikiModel#appendHead(java.lang.String,
	 * int, boolean, int, int, int)
	 */
	@Override
	public ITableOfContent appendHead(final String rawHead, final int headLevel, final boolean noToC, final int headCounter, final int startPosition, final int endPosition) {
		// In rawHead steht eh immer nur genau ein Section title, man kann von diesem Stack also einfach nur
		// das erste (weil einzige) Element nehmen.
		// * Der noToC-Parameter ist grundsätzlich auf "true" gesetzt, da wir keine ToC wollen. (Warum eigetnlich nicht?)
		final TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		final WPTag headTagNode = new WPTag("h" + headLevel);
//		final TagNode spanTagNode = new TagNode("span");
		// Example:
		// <h2><span class="mw-headline" id="Header_level_2">Header level
		// 2</span></h2>
//		spanTagNode.addChild(localStack.getNodeList().get(0));
//		headTagNode.addChild(spanTagNode);
		headTagNode.addChild(localStack.getNodeList().get(0));
		
		// Hier steht nur der tatsaechliche Content den spanTagNode.
		/*
		 * FIXME: Was passiert, wenn man als Titel einer Section ein HTML-Tag eingibt? Das macht doch bestimmt was kaputt!
		 * Ja, da passiert einiges. Ist ein pures HTML-Tag im Titel, so wird das entfernt und nur der Inhalt dieses Tags zurueckgegeben.
		 * Ist Text mit einem HTML-Tag im Titel, so wird das HTML-Tag einfach entfernt.
		 * Ist da irgendein non-HTML-Tag (also bspw. <ba>blub</ba>), so bleibt einfach alles stehen, das Tag wird also NICHT entfernt!
		 */
		final String tocHead = headTagNode.getBodyString();
		// tocHead wird halt in HTML-Form gebracht, nichts besonderes hier. Leerzeichen werden durch Unterstriche ersetzt, usw.
		String anchor = Encoder.encodeDotUrl(tocHead);
		
//		// Warum geht das nicht? Bzw., was sollte da passieren?
//		this.createTableOfContent(!noToC);
//		this.fTableOfContentTag.setShowToC(true);
//		
//		// Was ist das hier?
//		if (!noToC && headCounter > 3) {
//			this.fTableOfContentTag.setShowToC(true);
//		}
//		
//		// Hier suchen wir ... gar nichts, da fToCSet nie befuellt wird.
//		if (this.fToCSet.contains(anchor)) {
//			String newAnchor = anchor;
//			// Das ist ja wohl ein bisschen uebertrieben.
//			for (int i = 2; i < Integer.MAX_VALUE; i++) {
//				newAnchor = anchor + '_' + Integer.toString(i);
//				// Warum schaut man hier nicht einfach nach, ob eine bestimmte Zahl schon vorhanden ist
//				// anstelle diese daemliche Schleife durchlaufen zu lassen? Werden die irgendwo
//				// wieder entfernt?!
//				if (!this.fToCSet.contains(newAnchor)) {
//					break;
//				}
//			}
//			anchor = newAnchor;
//		}

		// Das erstellt einen Link, um diese Section editieren zu koennen. Funktioniert aber irgendwie gar nicht.
		// Abgesehen davon: Wozu?! Im Moment bringt uns das rein gar nichts.
//		if (this.getRecursionLevel() == 1) {
//			this.buildEditLinkUrl(this.fSectionCounter++);
//		}

		// add attributes to the tags
//		spanTagNode.addAttribute("class", "mw-headline", true);
		headTagNode.addAttribute("class", "mw-headline", true);
		
		// FIXME: Same title --> double ids
//		spanTagNode.addAttribute("id", anchor, true);

		// add the generated heading node to the local stack for later processing
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
