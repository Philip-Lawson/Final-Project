package uk.ac.qub.finalproject.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DeviceDetailsManagerTest.class, DeviceVersionManagerTest.class,
		EmailValidationStrategyTest.class, EncryptorImplTest.class,
		ResultsPacketManagerTest.class, UserDetailsManagerTest.class,
		WorkPacketDrawerImplTest.class })
public class AllTests {

}
