package uk.ac.qub.finalproject.server.networking;

/**
 * THis is a utility class that stores all the constants that represent server
 * requests to the client. It is written as a class rather than an enum to
 * maintain consistency between the server request class in the android client
 * system. It is considered bad practice to use enums in android development due
 * to memory constraints within the system.
 * 
 * @author Phil
 *
 */
public class ServerRequest {
		
	public static final int PROCESS_WORK_PACKETS = 100;
	public static final int LOAD_PROCESSING_CLASS = 101;
	public static final int BECOME_DORMANT = 102;
	public static final int REGISTER_REQUEST = 103;
	public static final int CHANGE_CONFIRMED = 104;

}
