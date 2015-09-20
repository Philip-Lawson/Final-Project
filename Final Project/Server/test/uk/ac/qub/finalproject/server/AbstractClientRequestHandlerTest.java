package uk.ac.qub.finalproject.server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.finalproject.server.networking.AbstractClientRequestHandler;
import uk.ac.qub.finalproject.serverstubs.FallThroughRequestHandler;
import uk.ac.qub.finalproject.serverstubs.GenericClientRequestHandler;

/**
 * This JUnit tests the basic workings of the AbstractRequestHandler
 * 
 * To make sure certain methods work a GenericRequestHandler and a
 * FallThroughRequestHandler have been written. The Generic handler stores two
 * boolean flags - one for handleHere() being called and one for delegate()
 * being called. The FallThrough stores a flag for fall through - this is just
 * used to show that setNextHandler() works.
 * 
 * @author Phil
 *
 */
public class AbstractClientRequestHandlerTest {

	AbstractClientRequestHandler test;
	FallThroughRequestHandler fallThroughHandler;

	int validRequestNum, invalidRequestNum;

	@Before
	public void setUp() throws Exception {

		test = new GenericClientRequestHandler();
		fallThroughHandler = new FallThroughRequestHandler();

		invalidRequestNum = -1;
		validRequestNum = GenericClientRequestHandler.REQUEST_NUM;
	}

	@After
	public void tearDown() throws Exception {
		test = null;
		fallThroughHandler = null;
	}

	@Test
	public void abstractClientRequestHandler() {
		assertNotNull(test);
	}

	@Test
	public void testSetNextHandler() {
		// if stub is set as the next handler
		// when test is called to process an invalid number it should delegate
		// to the fall through handler
		test.setNextHandler(fallThroughHandler);
		test.processRequest(invalidRequestNum, null, null);

		// if the call is delegated to the fall through handler
		// that means setNextHandler worked
		assert (fallThroughHandler.hasFallenThrough());
	}

	@Test
	public void testProcessRequest() {

		test.processRequest(validRequestNum, null, null);

		// cast the abstract request handler to a generic one to have access to
		// the getHandledHere method
		GenericClientRequestHandler tester = (GenericClientRequestHandler) test;

		assert (tester.getHandledHere());
	}

	@Test
	public void testDelegate() {
		test.setNextHandler(fallThroughHandler);
		test.processRequest(invalidRequestNum, null, null);

		// cast the abstract request handler to a generic one to have access to
		// the getDelegated method
		GenericClientRequestHandler tester = (GenericClientRequestHandler) test;

		assert (tester.getDelegated());
	}

}
