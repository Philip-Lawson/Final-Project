package uk.ac.qub.finalproject.server.networking;

/**
 * This is a utility class that stores all the constants that represent client
 * requests to the server. It is written as a class rather than an enum to
 * maintain consistency between the client request class in the android client
 * system. It is considered bad practice to use enums in android development due
 * to memory constraints within the system.
 * 
 * @author Phil
 *
 */
public class ClientRequest {

	public static final int REGISTER = 0;
	public static final int CHANGE_EMAIL = 1;
	public static final int DEREGISTER_DEVICE = 2;
	public static final int DEREGISTER_USER = 3;
	public static final int PROCESS_RESULT = 4;
	public static final int REQUEST_WORK_PACKET = 5;
	public static final int REQUEST_PROCESSING_CLASS = 6;

}
