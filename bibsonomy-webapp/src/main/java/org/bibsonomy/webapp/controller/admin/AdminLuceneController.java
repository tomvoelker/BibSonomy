package org.bibsonomy.webapp.controller.admin;

import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.lucene.index.LuceneBibTexIndex;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneBibTexManager;
import org.bibsonomy.lucene.index.manager.LuceneBookmarkManager;
import org.bibsonomy.lucene.index.manager.LuceneGoldStandardPublicationManager;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.search.LuceneSearchBibTex;
import org.bibsonomy.lucene.search.LuceneSearchBookmarks;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.admin.AdminLuceneViewCommand;
import org.bibsonomy.webapp.command.admin.AdminStatisticsCommand;
import org.bibsonomy.webapp.command.admin.AdminViewCommand;
import org.bibsonomy.webapp.command.admin.LuceneIndexSettingsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for lucene admin page
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class AdminLuceneController implements MinimalisticController<AdminLuceneViewCommand> {
	private static final Log log = LogFactory.getLog(AdminLuceneController.class);
	
	private static final String NOTSET = "not set";

	private LogicInterface logic;
	
	@SuppressWarnings("unused") // FIXME: currently unused
	private UserSettings userSettings;
	
	private List<LuceneBibTexIndex> bibTexIndices;

	@Override
	public View workOn(AdminLuceneViewCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin lucene");

		
		LuceneGoldStandardPublicationManager managerGS = LuceneGoldStandardPublicationManager.getInstance();
		LuceneResourceIndex<GoldStandardPublication> indexGS = managerGS.getResourceIndeces().get(0);		

		
		/** 
		 *  Store Bookmark-Index info
		 * */
		LuceneBookmarkManager bookmarkManager = (LuceneBookmarkManager) LuceneBookmarkManager.getInstance();
		LuceneResourceIndex<Bookmark> bookmarkIndex = bookmarkManager.getResourceIndeces().get(0);
		
		LuceneIndexSettingsCommand bookmarkIndexCommand = command.getBookmarksIndex();
		bookmarkIndexCommand.setNumDocs(bookmarkIndex.getNumberOfStoredDocuments());
		bookmarkIndexCommand.setNumDeletedDocs(bookmarkIndex.getNumberOfDeletedDocuments());
		bookmarkIndexCommand.setNewestDate(new Date(bookmarkIndex.getLastDate()).toString());
		bookmarkIndexCommand.setLastModified(bookmarkIndex.getLastLogDate());
		

		/** 
		 *  Store Bibtex-Index info
		 * */
		LuceneBibTexManager bibTexManager = LuceneBibTexManager.getInstance();
		LuceneResourceIndex<BibTex> bibTexIndex = bibTexManager.getResourceIndeces().get(0);
		
		LuceneIndexSettingsCommand publicationsIndexCommand = command.getPublicationsIndex();
		publicationsIndexCommand.setNumDocs(bibTexIndex.getNumberOfStoredDocuments());
		publicationsIndexCommand.setNumDeletedDocs(bibTexIndex.getNumberOfDeletedDocuments());
		publicationsIndexCommand.setNewestDate(new Date(bibTexIndex.getLastDate()).toString());
		publicationsIndexCommand.setLastModified(bibTexIndex.getLastLogDate());
		
		
		
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			command.setEnvContextString("java:/comp/env");
			
			try {
				command.setLuceneBookmarksPath((String) envContext.lookup("luceneIndexPathBoomarks"));
			} catch (NamingException e) {
				command.setLuceneBookmarksPath(NOTSET);
			}
			
			try {
				command.setLucenePublicationsPath((String) envContext.lookup("luceneIndexPathPublications"));
			} catch (NamingException e) {
				command.setLucenePublicationsPath(NOTSET);
			}
			
			try {
				BasicDataSource envDataSource = ((BasicDataSource) initContext.lookup("java:/comp/env/jdbc/bibsonomy_lucene"));
				command.setLuceneDataSourceUrl(envDataSource.getUrl());
				command.setLuceneDataSourceUsername(envDataSource.getUsername());
			} catch (NamingException e) {
				command.setLuceneDataSourceUrl(NOTSET);
				command.setLuceneDataSourceUsername(NOTSET);
			}
		
		} catch (NamingException e) {
			command.setEnvContextString(NOTSET);
		}
		
		LuceneResourceSearch<Bookmark> bookmarksIndex    = LuceneSearchBookmarks.getInstance();
		LuceneResourceSearch<BibTex>   publicationsIndex = LuceneSearchBibTex.getInstance();

		// Infos über die einzelnen Indexe
		// Anzahl Einträge, letztes Update, ...
		// in extra methode: Parameter=Index

		command.getBookmarksIndex().setInstance(bookmarksIndex.toString());
		command.getPublicationsIndex().setInstance(publicationsIndex.toString());
		
//		command.bookmarksIndex.setIndexStatistics(bookmarksIndex.getStatistics());
//		command.publicationsIndex.setIndexStatistics(publicationsIndex.getStatistics());

		

		return Views.ADMIN_LUCENE;
	}

	@Override
	public AdminLuceneViewCommand instantiateCommand() {
		return new AdminLuceneViewCommand();
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param userSettings the userSettings to set
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	
	@SuppressWarnings("unused")  // FIXME: currently unused
	private void setStatistics(AdminViewCommand cmd) {
		AdminStatisticsCommand command = cmd.getStatisticsCommand();

		for (int interval : cmd.getInterval()) {
			command.setNumAdminSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.SPAMMER, interval));
			command.setNumAdminNoSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.ADMIN, SpamStatus.NO_SPAMMER, interval));
			command.setNumClassifierSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER, interval));
			command.setNumClassifierSpammerUnsure(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.SPAMMER_NOT_SURE, interval));
			command.setNumClassifierNoSpammerUnsure(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER_NOT_SURE, interval));
			command.setNumClassifierNoSpammer(Long.valueOf(interval), this.logic.getClassifiedUserCount(Classifier.CLASSIFIER, SpamStatus.NO_SPAMMER, interval));
		}
	}

}