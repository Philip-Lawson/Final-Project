package uk.ac.qub.finalproject.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.calculationclasses.RegistrationPack;
import uk.ac.qub.finalproject.persistencestubs.DeviceVersionJDBCStub;

public class DeviceVersionManagerTest {

	DeviceVersionManager test;
	DeviceVersionJDBCStub dbStub;
	int versionCode, newVersionCode;
	String deviceID;
	
	@Before
	public void setUp() throws Exception {
		dbStub = new DeviceVersionJDBCStub();
		test = new DeviceVersionManager(dbStub);
		versionCode = 1;
		newVersionCode = versionCode + 3;
		deviceID = "Device ID";
	}

	@After
	public void tearDown() throws Exception {
		test = null;
		dbStub = null;
	}

	@Test
	public void testLoadDeviceVersions() {
		int testMapSize = test.getVersionMap().size();
		int expectedMapSize;
		
		test.loadDeviceVersions();
		expectedMapSize = testMapSize + dbStub.mapSize();
		
		assert(dbStub.mapLoaded());
		assertEquals(expectedMapSize, test.getVersionMap().size());
		
	}

	@Test
	public void testSaveDeviceVersionStoredDevice() {
		RegistrationPack registrationPack = new RegistrationPack();
		
		registrationPack.setAndroidID(deviceID);
		registrationPack.setVersionCode(versionCode);
		
		test.saveDevice(versionCode, deviceID);
		test.saveDeviceVersion(registrationPack);
		
		assert(dbStub.versionUpdated());
		assert(test.getVersionMap().get(versionCode).contains(deviceID));
	}
	
	@Test
	public void testSaveDeviceVersionUnsavedDevice() {
		RegistrationPack registrationPack = new RegistrationPack();		
		registrationPack.setAndroidID(deviceID);
		registrationPack.setVersionCode(versionCode);
				
		test.saveDeviceVersion(registrationPack);
		
		assert(dbStub.resultSaved());
		assert(test.getVersionMap().get(versionCode).contains(deviceID));
	}

	@Test
	public void testDeviceIsVersionOrAboveTrue() {
		RegistrationPack registrationPack = new RegistrationPack();		
		registrationPack.setAndroidID(deviceID);
		registrationPack.setVersionCode(versionCode);		
		
		test.saveDeviceVersion(registrationPack);
		assert(test.deviceIsVersionOrAbove(versionCode, deviceID));
	}
	
	@Test
	public void testDeviceIsVersionOrAboveFalse() {
		RegistrationPack registrationPack = new RegistrationPack();		
		registrationPack.setAndroidID(deviceID);
		registrationPack.setVersionCode(versionCode);		
		
		test.saveDeviceVersion(registrationPack);
		assertFalse(test.deviceIsVersionOrAbove(versionCode+1, deviceID));
	}
	
	@Test
	public void testIsVersionAboveDeviceNotStored(){
		assertFalse(test.deviceIsVersionOrAbove(versionCode+1, deviceID));
	}

	@Test
	public void testSaveDevice() {		
		test.saveDevice(versionCode, deviceID);		
		assertTrue(test.getVersionMap().get(versionCode).contains(deviceID));
	}

	@Test
	public void testUpdateDeviceVersion() {		
		test.saveDevice(versionCode, deviceID);
		test.updateDeviceVersion(newVersionCode, deviceID);
		
		assertTrue(test.getVersionMap().get(newVersionCode).contains(deviceID));
		assertFalse(test.getVersionMap().get(versionCode).contains(deviceID));
		assertEquals(2, test.getVersionMap().size());
	}

}
