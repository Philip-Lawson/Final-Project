package uk.ac.qub.finalproject.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.qub.finalproject.calculationclasses.GroupResultsValidatorTest;
import uk.ac.qub.finalproject.calculationclasses.ResultProcessorTest;
import uk.ac.qub.finalproject.calculationclasses.SingleResultValidatorTest;
import uk.ac.qub.finalproject.persistence.DeviceDetailsManagerTest;
import uk.ac.qub.finalproject.persistence.DeviceVersionManagerTest;
import uk.ac.qub.finalproject.persistence.EmailValidationStrategyTest;
import uk.ac.qub.finalproject.persistence.EncryptorImplTest;
import uk.ac.qub.finalproject.persistence.ResultsPacketManagerTest;
import uk.ac.qub.finalproject.persistence.UserDetailsManagerTest;
import uk.ac.qub.finalproject.persistence.WorkPacketDrawerImplTest;
import uk.ac.qub.finalproject.server.AbstractClientRequestHandlerTest;

@RunWith(Suite.class)
@SuiteClasses({ DeviceDetailsManagerTest.class, DeviceVersionManagerTest.class,
		EmailValidationStrategyTest.class, EncryptorImplTest.class,
		ResultsPacketManagerTest.class, UserDetailsManagerTest.class,
		WorkPacketDrawerImplTest.class, GroupResultsValidatorTest.class, 
		ResultProcessorTest.class, SingleResultValidatorTest.class,
		AbstractClientRequestHandlerTest.class })
public class AllTests {

}
