package org.bibsonomy.database.managers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bibsonomy.common.enums.InetAddressStatus;
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
	
}
