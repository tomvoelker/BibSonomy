package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.bibsonomy.model.User;

import org.bibsonomy.common.enums.ClassifierMode;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Robert Jaeschke
 * @author Stefan Stützer
 * @version $Id$
 */
@Ignore
public class AdminDatabaseManagerTest extends AbstractDatabaseManagerTest {

	/**
	 * tests getInetAddressStatus
	 */
	@Test
	public void getInetAddressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.0.1");
			assertEquals(InetAddressStatus.UNKNOWN, this.adminDb.getInetAddressStatus(address, this.dbSession));
		} catch (UnknownHostException ignore) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests addInetAdressStatus
	 */
	@Test
	public void addInetAdressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.1.1");
			final InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
			// write
			this.adminDb.addInetAddressStatus(address, status, this.dbSession);
			// read
			final InetAddressStatus writtenStatus = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(status, writtenStatus);
		} catch (UnknownHostException ignore) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests deleteInetAdressStatus
	 */
	@Test
	public void deleteInetAdressStatus() {
		try {
			final InetAddress address = InetAddress.getByName("192.168.1.1");
			// read
			InetAddressStatus status = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// delete
			this.adminDb.deleteInetAdressStatus(address, this.dbSession);
			// read
			status = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(InetAddressStatus.UNKNOWN, status);
		} catch (UnknownHostException ex) {
			// ignore, we don't need host name resolution
		}
	}

	/**
	 * tests getClassifierSettings
	 */
	@Test
	public void getClassifierSettings() {
		final ClassifierSettings settingsKey = ClassifierSettings.ALGORITHM;
		final String value = this.adminDb.getClassifierSettings(settingsKey, this.dbSession);
		assertEquals("weka.classifiers.lazy.IBk", value);
	}

	/**
	 * tests updateClassifierSettings
	 */
	@Test
	public void updateClassifierSettings() {
		final ClassifierSettings settingsKey = ClassifierSettings.MODE;
		final String value = ClassifierMode.NIGHT.getAbbreviation();

		this.adminDb.updateClassifierSettings(settingsKey, value, this.dbSession);

		final String result = this.adminDb.getClassifierSettings(settingsKey, this.dbSession);
		assertEquals(value, result);
	}
	
	/**
	 * tests logging when flagging and unflagging spammers
	 */
	@Test
	public void updatePredictionLogs(){
		final User user = new User(); 
		user.setName("beate"); 
		user.setSpammer(0);
		user.setToClassify(1); 
		user.setPrediction(0);
		user.setMode("D"); 
		user.setAlgorithm("testlogging"); 
		user.setUpdatedBy("classifier");
		//flag spammer (flagging does not change: user is no spammer)
		final String result = this.adminDb.flagSpammer(user, "classifier", "on", this.dbSession);
		assertEquals(user.getName(), result); 
	}
}