package org.bibsonomy.util.tomcat.listener;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.jdbc.Connection;


/**
 * Cleans some things up after undeploying a webapp.
 * 
 * In particular, shuts down the MySQL cancellation timers and AWT (started by JabRef).
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class CleanupListener implements LifecycleListener {

	private static final Log log = LogFactory.getLog(CleanupListener.class);

	/*
	 * These variables must be set in the Tomcat Context configuration to
	 * be able to access the database. 
	 */
	private String dbUrl;
	private String dbUser;
	private String dbPass;
	private final MemoryMXBean mx;

	public CleanupListener() {
		mx = ManagementFactory.getMemoryMXBean();
//		mx.setVerbose(true);
	}

	public void lifecycleEvent(final LifecycleEvent event) {
		/*
		 * handle the different event types
		 */
		final String type = event.getType();
		if (Lifecycle.AFTER_STOP_EVENT.equals(type)) {
			handleAfterStopEvent(event);
		} else if (Lifecycle.BEFORE_START_EVENT.equals(type)) {
			handleBeforeStartEvent(event);
		} else if (Lifecycle.BEFORE_STOP_EVENT.equals(type)) {
			handleBeforeStopEvent(event);
		}
	}
	
	
	private void foo() {
		ThreadLocal<Object> foo;
		
		
	}


	/**
	 * Handles {@link Lifecycle#BEFORE_STOP_EVENT}.
	 * 
	 * @param event
	 */
	private void handleBeforeStopEvent(final LifecycleEvent event) {
		log.info("handling before stop event");
		/*
		 * print all running threads
		 */
//		printRunningThreads(true);
	}

	/**
	 * Prints all running threads from this thread group to log.info.
	 */
	private void printRunningThreads(boolean verbose) {
		log.info("printing running threads");
		final Thread[] list = new Thread[50];
		final int enumerate = Thread.currentThread().getThreadGroup().enumerate(list);

		log.info("got " + enumerate + " threads in this thread group");
		if (enumerate >= list.length) {
			log.info("this is more than the " + list.length + " threads expected ... so please adjust array size");
		}

		if (verbose) {
			for (int i = 0; i < enumerate; i++) {
				log.info("got thread " + list[i].getName());
			}
		}
	}

	/**
	 * Handles {@link Lifecycle#BEFORE_START_EVENT}
	 * 
	 * @param event
	 */
	private void handleBeforeStartEvent(final LifecycleEvent event) {
		log.info("handling before start event");

		/*
		 * loading the class org.apache.commons.logging.impl.Log4JLogger ... just to 
		 * have it loaded before the webapp starts
		 */
		final String logger = "org.apache.commons.logging.impl.Log4JLogger";
		try {
			log.info("trying to load class " + logger);
			CleanupListener.class.getClassLoader().loadClass(logger);
		} catch (ClassNotFoundException e) {
			log.error("Ouch! Failed miserably ...", e);
		}

		/*
		 * trying to initiate a MySQL connection ... just to start the timers
		 * outside the webapp
		 */
		try {
			log.info("loading MySQL JDBC driver");
			Class.forName ("com.mysql.jdbc.Driver").newInstance();

			log.info("getting connection");
			final java.sql.Connection conn = DriverManager.getConnection (dbUrl, dbUser, dbPass);
			conn.setReadOnly(true);

			log.info("closing connection");
			conn.close();

		} catch (SQLException e) {
			log.fatal("Could not open connection", e);
		} catch (InstantiationException e) {
			log.fatal("Could not instantiate MySQL driver", e);
		} catch (IllegalAccessException e) {
			log.fatal("Could not access MySQL driver", e);
		} catch (ClassNotFoundException e) {
			log.fatal("Could not find MySQL driver", e);
		}
	}

	/**
	 * stop MySQL cancellation timer
	 * 
	 * see http://bugs.mysql.com/bug.php?id=36565
	 * 
	 * Currently not used, because we ensure in {@link #handleBeforeStartEvent(LifecycleEvent)} 
	 * that MySQL is loaded outside the webapp.
	 * 
	 */
	private void cancelMySQLCancellationTimer() {
		log.info("trying to cancel the MySQL cancellation timers");
		try {
			final Field f = Connection.class.getDeclaredField("cancelTimer");
			f.setAccessible(true);
			final Timer timer = (Timer) f.get(null);
			timer.purge(); // cancel(); FIXME: what's the correct way?
			timer.cancel(); // trying both ...
			log.info("successfully cancelled timer");
			// really?
			/*
			 * TODO: if we set the field to NULL we get NPE's later, when an application is reloaded :-(
			 * But without doing it, we have a memory leak ...
			 */
//			log.info("trying to set the field to NULL, destroying the reference to the timer");
//			f.set(null, null);
//			log.info("success!");
		} catch (final Exception e) {
			log.error("Ouch! Failed miserably ...", e);
		}
	}

	private void cleanBeanIntrospector() {
		log.info("calling java.beans.Introspector.flushCaches()");
		try {
			java.beans.Introspector.flushCaches();
			log.info("success!");
		} catch (final Exception e) {
			log.error("Ouch! Failed miserably ...", e);
		}

	}

	/**
	 * shut down syncTimer of java.util.prefs.FileSystemPreferences
	 */
	private void cancelFileSystemPreferencesTimer() {
		final String prefs = "java.util.prefs.FileSystemPreferences";
		log.info("trying to cancel the " + prefs + " syncTimer");
		try {
			final Class<?> clazz = CleanupListener.class.getClassLoader().loadClass(prefs);
			final Field f = clazz.getDeclaredField("syncTimer");
			f.setAccessible(true);
			final Timer timer = (Timer) f.get(null);
			timer.cancel();
			log.info("successfully cancelled timer");
			log.info("trying to set the field to NULL, destroying the reference to the timer");
			f.set(null, null);
			log.info("success!");
		} catch (Exception e) {
			log.error("Ouch! Failed miserably ...", e);
		}
	}


	/**
	 * We need to clean the logger from the StandardContext of BibSonomy,
	 * because it is loaded via the WebAppClassLoader (for whatever reason).
	 * 
	 * bug:   https://issues.apache.org/bugzilla/show_bug.cgi?id=46221
	 * patch: https://issues.apache.org/bugzilla/attachment.cgi?id=22985
	 * 
	 */
	private void nullifyTomcatLoggers(final LifecycleEvent event) {
		log.info("trying to clean the logger from the StandardContext");
		log.info("lifecycle: " + event.getLifecycle());
		log.info("data:      " + event.getData());
		log.info("source:    " + event.getSource());
		final Lifecycle lifecycle = event.getLifecycle();
		log.info("trying to cast the lifecycle to StandardContext");
		final StandardContext standardContext = (StandardContext) lifecycle;
		log.info("success! got StandardContext for " + standardContext.getDisplayName());

		try {
			/*
			 * StandardContext
			 */
			log.info("trying to clean the logger in the " + ContainerBase.class);
			Field f = ContainerBase.class.getDeclaredField("logger");
			f.setAccessible(true);
			f.set(standardContext, null);
			log.info("success!");

			/*
			 * StandardManager
			 */
			log.info("trying to clean the log in the " + StandardManager.class);
			f = ManagerBase.class.getDeclaredField("log");
			f.setAccessible(true);
			f.set(standardContext.getManager(), null);
			log.info("success!");

			/*
			 * StandardWrapper
			 */
//			log.info("trying to clean the logger in the " + StandardWrapper.class);
//			f = ContainerBase.class.getDeclaredField("logger");
//			f.setAccessible(true);
//			f.set(standardContext.get, null);
//			log.info("success!");


			/*
			 * ValveBase
			 */
			log.info("trying to clean the containerLog in the " + ValveBase.class);
			f = ValveBase.class.getDeclaredField("containerLog");
			f.setAccessible(true);
			f.set(standardContext.getPipeline().getBasic(), null);
			log.info("success!");


		} catch (final Exception e) {
			log.error("Ouch! Failed miserably ...", e);
		}
	}

	/**
	 * Handles {@link Lifecycle#AFTER_STOP_EVENT}
	 * 
	 * @param event
	 */
	private void handleAfterStopEvent(final LifecycleEvent event) {


		log.info("handling after stop event");
		/*
		 * print all running threads
		 */
//		printRunningThreads(true);

		// disabled, because MySQL is loaded outside the webapp
		// rja, 2009-04-17: enabled again, because we now have a chain to iBatis ...
		// rja, 2009-05-12: disabled again, because otherwise reloaded BibSonomy gets not DB connections 
//		cancelMySQLCancellationTimer();

		/*
		 * check, if the timer thread really got cancelled 
		 */
//		printRunningThreads(false);
		
		/*
		 * if not ... do it the hard way (kill the thread)
		 */
		cancelMySQLCancellationTimerHard();
		
		
		/*
		 * rja, 2009-04-17: only necessary, when loaded by webapp
		 */
//		deregisterJDBCDriver();

		cancelFileSystemPreferencesTimer();

		nullifyTomcatLoggers(event);

		cleanBeanIntrospector();

		// disabled, because we patched JabRef to not start AWT
		// disableAWT();

		log.info("heap usage:" + mx.getHeapMemoryUsage());
		log.info("forcing garbage collection using System.gc()");
		System.gc();
		log.info("heap usage:" + mx.getHeapMemoryUsage());
		log.info("forcing garbage collection using mx.gc()");
		mx.gc();
		log.info("heap usage:" + mx.getHeapMemoryUsage());


		log.info("Finished!");
	}

	private void cancelMySQLCancellationTimerHard() {
		final Thread[] list = new Thread[50];
		final int enumerate = Thread.currentThread().getThreadGroup().enumerate(list);

		if (enumerate >= list.length) {
			log.info("this is more than the " + list.length + " threads expected ... so please adjust array size");
		}

		for (int i = 0; i < enumerate; i++) {
			
			final Thread thread = list[i];
			
			final String name = thread.getName();
			
			if (name.contains("MySQL Statement Cancellation Timer")) {
				log.info("Caught thread " + name + " still running! Trying to cancel ...");
				try {
					thread.interrupt();
					log.info("successfully interrupted the thread");
					log.info("threas is alive: " + thread.isAlive());
				} catch (final Exception e) {
					log.error("Ouch! Failed miserably ...", e);
				}
			}
			
			
		}
	}
	
	/**
	 * Deregisters the JDBC driver
	 * 
	 * This is only necessary, when the driver is loaded from within the webapp,
	 * since the driver is in the webapp, but the drivermanager in the container
	 * holding a reference to the driver ... and thus a strong reference. 
	 * 
	 * @see http://opensource.atlassian.com/confluence/spring/pages/viewpage.action?pageId=2669
	 * 
	 * 
	 */
	private void deregisterJDBCDriver() {
		log.info("trying to deregister JDBC drivers");
		for (final Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) { 
			final Driver driver = e.nextElement(); 
			/*
			 * FIXME: only unload those drivers, which are in the bibsonomy webapp!
			 */

			try { 
				if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
					log.info("trying to deregister driver " + driver);
					DriverManager.deregisterDriver(driver);          
				} 
			} catch (final Exception ex) { 
				log.error("Ouch! Failed miserably ...", ex);
			} 

		} 
	}

	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}
}

