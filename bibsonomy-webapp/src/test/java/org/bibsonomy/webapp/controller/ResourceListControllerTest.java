package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.controller.ResourceListController;
import org.bibsonomy.webapp.view.Views;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO: currently only for discussion
 * 
 * @author dzo
 * @version $Id$
 */
public class ResourceListControllerTest {
	
	// TODO: move 
	private static final Set<Class<? extends Resource>> ALL_RESOURCE_CLASSES = new HashSet<Class<? extends Resource>>();
	
	static {
		ALL_RESOURCE_CLASSES.add(Bookmark.class);
		ALL_RESOURCE_CLASSES.add(BibTex.class);
		ALL_RESOURCE_CLASSES.add(GoldStandardPublication.class);
	}
	
	private static <T> Set<T> intersection(final Collection<? extends T> col1, final Collection<? extends T> col2) {
		if (!present(col1) || !present(col2)) {
			return Collections.emptySet();
		}
		
		final Set<T> set = new HashSet<T>(col1);
		set.retainAll(col2);
		return set;
	}
	
	private static class NewResourceListController extends ResourceListController {
		
		private Set<Class<? extends Resource>> supportedResources;
		private boolean handleTagsOnly;
		
		// made chooseListsToInitialize public
		@Override
		public void chooseListsToInitialize(String format, String resourcetype) {
			super.chooseListsToInitialize(format, resourcetype);
		}
		
		public Collection<Class<? extends Resource>> getListsToInitialize() {
			return this.listsToInitialise;
		}
		
		@Override
		public void setListsToInitialise(Collection<Class<? extends Resource>> listsToInitialise) {
			super.setListsToInitialise(listsToInitialise);
			
			// new: supportedResources (the controller only knows which resources he supports)
			this.supportedResources = new HashSet<Class<? extends Resource>>(listsToInitialise);
		}
		
		@Override
		public void handleTagsOnly(ResourceViewCommand cmd, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, String hash, int max, String search) {
			// no logic no request
			// super.handleTagsOnly(cmd, groupingEntity, groupingName, regex, tags, hash, max, search);
			final String tagstype = cmd.getTagstype();
			if (present(tagstype)) {
				this.handleTagsOnly = true;
			}
		}
		
		// extract the user settings to a set of resources
		public Set<Class<? extends Resource>> getUserResourcesFromSettings() {
			if (this.userSettings == null) {
				// TODO remove and make it a required attribute 
				this.userSettings = new UserSettings();
				this.userSettings.setShowBibtex(true);
				this.userSettings.setShowBookmark(true);
			}
			
			final Set<Class<? extends Resource>> resources = new HashSet<Class<? extends Resource>>();
			
			if (this.userSettings.isShowBookmark()) {
				resources.add(Bookmark.class);
			}
			
			if (this.userSettings.isShowBibtex()) {
				resources.add(BibTex.class);
			}
			
			return resources;
		}
		
		public Set<Class<? extends Resource>> getListsToInitialize(final String format, final Set<Class<? extends Resource>> urlParamResources) {
			if (this.handleTagsOnly) {
				return Collections.emptySet();
			}
			
			final Set<Class<? extends Resource>> supportFormat = intersection(this.supportedResources, this.getResourcesForFormat(format));
			final Set<Class<? extends Resource>> supportParam = intersection(this.supportedResources, urlParamResources);
			final Set<Class<? extends Resource>> supportUser = intersection(this.supportedResources, this.getUserResourcesFromSettings());
			
			if (present(supportFormat)) {
				final Set<Class<? extends Resource>> supportFormatParam = intersection(supportFormat, supportParam);
				if (present(supportFormatParam)) {
					return supportFormatParam;
				}
				
				final Set<Class<? extends Resource>> supportFormatUser = intersection(supportFormat, supportUser);
				if (present(supportFormatUser)) {
					return supportFormatUser;
				}
				
				return supportFormat;
			}
			
			return Collections.emptySet();
		}

		private Set<? extends Class<? extends Resource>> getResourcesForFormat(final String format) {
			if (Views.isBibtexOnlyFormat(format)) {
				return Collections.singleton(BibTex.class);
			}
			
			if (Views.isBookmarkOnlyFormat(format)) {
				return Collections.singleton(Bookmark.class);
			}
			
			return new HashSet<Class<? extends Resource>>(ALL_RESOURCE_CLASSES);
		}
	}
	
	private static Set<Class<? extends Resource>> STANDARD_VIEW_CLASSES;
	private static Set<Class<? extends Resource>> ALL_CLASSES;
	private static Set<Class<? extends Resource>> BOOKMARK_CLASS;
	private static Set<Class<? extends Resource>> PUBLICATION_CLASS;
	private static Set<Class<? extends Resource>> GOLD_PUB_CLASS;
	
	@BeforeClass
	public static void createSets() {
		STANDARD_VIEW_CLASSES = new HashSet<Class<? extends Resource>>();
		STANDARD_VIEW_CLASSES.add(Bookmark.class);
		STANDARD_VIEW_CLASSES.add(BibTex.class);
		
		ALL_CLASSES = new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES);
		ALL_CLASSES.add(GoldStandardPublication.class);
		
		BOOKMARK_CLASS = new HashSet<Class<? extends Resource>>();
		BOOKMARK_CLASS.add(Bookmark.class);
		
		PUBLICATION_CLASS = new HashSet<Class<? extends Resource>>();
		PUBLICATION_CLASS.add(BibTex.class);
		
		GOLD_PUB_CLASS =  new HashSet<Class<? extends Resource>>();
		GOLD_PUB_CLASS.add(GoldStandardPublication.class);
	}
	
	private final UserSettings getSettings(final boolean showPublication, final boolean showBookmark) {
		final UserSettings settings = new UserSettings();
		settings.setShowBibtex(showPublication);
		settings.setShowBookmark(showBookmark);
		return settings;
	}
	
	@Test
	public void handleTagsOnly() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		final ResourceViewCommand cmd = new ResourceViewCommand();
		cmd.setTagstype("default");
		testController.handleTagsOnly(cmd, null, null, null, null, null, 0, null);
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("", null));
			
//		assertEquals(Collections.emptySet(), testController.getListsToInitialize());
	}
	
	@Test
	public void JSONFormat() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		
		/*
		 *  NEW
		 */
		final String format = "json";
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize(format, null));
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize(format, PUBLICATION_CLASS));
		assertEquals(GOLD_PUB_CLASS, testController.getListsToInitialize(format, GOLD_PUB_CLASS));
		/*
		 * OLD
		 */
		// user settings are removing the goldstandardpublication resource class
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES));
		// format json is aviable for all resources
		testController.chooseListsToInitialize(format, "");
		
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize());
		
		// reset (not side effect free)
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES));
		testController.chooseListsToInitialize(format, "bibtex");
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize());
	}
	
	@Test
	public void wrongUsage() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		
		/*
		 *  NEW
		 */
		final String format = "bookpubl";
		// TODO: DISCUSS: bookmark vs empty list (here the format is enforcing to initialize bookmarks)
		assertEquals(BOOKMARK_CLASS, testController.getListsToInitialize(format, PUBLICATION_CLASS));
		
		/*
		 *  OLD
		 */
		// user settings are removing the goldstandardpublication resource class
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES));
		// format is bookmark only and requested resource is bibtex
		testController.chooseListsToInitialize(format, "bibtex");
		assertEquals(Collections.emptySet(), testController.getListsToInitialize());
	}
	
	@Test
	public void wrongUsagePublication() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		
		/*
		 *  NEW
		 */
		final String format = "bibtex";
		// TODO: DISCUSS: publication vs empty list (here the format is enforcing to initialize bookmarks)
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize(format, BOOKMARK_CLASS));
		
		/*
		 *  OLD
		 */
		// user settings are removing the goldstandardpublication resource class
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES));
		// format is publication only and requested resource is bookmark
		testController.chooseListsToInitialize(format, "bookmark");
		assertEquals(Collections.emptySet(), testController.getListsToInitialize());
	}
	
	@Test
	public void respectUserSettings() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(ALL_CLASSES));
		
		/*
		 * bookmark and publication settings activated
		 */
		testController.setUserSettings(getSettings(true, true));
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize("html", null));
		
		/*
		 * NEW
		 * (respects user settings)
		 */
		testController.setUserSettings(getSettings(true, false));
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize("html", null));
		
		/*
		 * NEW
		 * url param "overrides" user settings
		 */
		assertEquals(BOOKMARK_CLASS, testController.getListsToInitialize("html", BOOKMARK_CLASS));
		
		
		testController.setUserSettings(getSettings(false, true));
		
		assertEquals(PUBLICATION_CLASS, testController.getListsToInitialize("bibtex", BOOKMARK_CLASS));
		
		/*
		 * OLD
		 * (ignores user settings)
		 */
		// user settings are removing the goldstandardpublication resource class
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(STANDARD_VIEW_CLASSES));
		testController.chooseListsToInitialize("html", null);
		assertEquals(STANDARD_VIEW_CLASSES, testController.getListsToInitialize());
	}	
	
	@Test
	public void complexOne() {
		final NewResourceListController testController = new NewResourceListController();
		testController.setListsToInitialise(new HashSet<Class<? extends Resource>>(PUBLICATION_CLASS));
		
		assertEquals(Collections.emptySet(), testController.getListsToInitialize("bookpubl", PUBLICATION_CLASS));
		
		testController.chooseListsToInitialize("bookpubl", "bibtex");
		assertEquals(Collections.emptySet(), testController.getListsToInitialize());
	}
}
