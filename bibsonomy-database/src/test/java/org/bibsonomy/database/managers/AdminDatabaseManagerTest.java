package org.bibsonomy.database.managers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierMode;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.User;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * @author rja
 * @version $Id$
 */
public class AdminDatabaseManagerTest extends AbstractDatabaseManagerTest {

	@Test
	public void getInetAddressStatus() {
		try {
			InetAddress address = InetAddress.getByName("192.168.1.1");
			// read
			InetAddressStatus status = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(InetAddressStatus.UNKNOWN, status);
		} catch (UnknownHostException ex) {
			// ignore, we don't need host name resolution
		}
	}
	
	@Test
	public void addInetAdressStatus() {
		try {
			InetAddress address = InetAddress.getByName("192.168.1.1");
			InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
			// write
			this.adminDb.addInetAddressStatus(address, status, this.dbSession);
			// read
			InetAddressStatus writtenStatus = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(status, writtenStatus);
		} catch (UnknownHostException ex) {
			// ignore, we don't need host name resolution
		}
		
	}
	
	@Test
	public void deleteInetAdressStatus() {
		try {
			InetAddress address = InetAddress.getByName("192.168.1.1");
			InetAddressStatus status = InetAddressStatus.WRITEBLOCKED;
			// write
			this.adminDb.addInetAddressStatus(address, status, this.dbSession);
			// read
			InetAddressStatus writtenStatus = this.adminDb.getInetAddressStatus(address, this.dbSession);
			assertEquals(status, writtenStatus);
			// delete 
			this.adminDb.deleteInetAdressStatus(address, this.dbSession);
			// read
			writtenStatus = this.adminDb.getInetAddressStatus(address, this.dbSession);
			// check
			assertEquals(InetAddressStatus.UNKNOWN, writtenStatus);
		} catch (UnknownHostException ex) {
			// ignore, we don't need host name resolution
		}		
	}
	
	@Test
	public void getClassifierSettings() {
		ClassifierSettings settingsKey = ClassifierSettings.ALGORITHM;
		String value = this.adminDb.getClassifierSettings(settingsKey, this.dbSession);
		
		assertEquals("weka.classifiers.lazy.IBk", value);
	}
	
	@Test
	public void updateClassifierSettings() {
		ClassifierSettings settingsKey = ClassifierSettings.MODE;
		String value = ClassifierMode.NIGHT.getAbbreviation();
		
		this.adminDb.updateClassifierSettings(settingsKey, value, this.dbSession);	
		
		String result = this.adminDb.getClassifierSettings(settingsKey, this.dbSession);		
		assertEquals(value, result);
	}
}