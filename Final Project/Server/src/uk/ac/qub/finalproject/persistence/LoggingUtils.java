/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import uk.ac.qub.finalproject.server.implementations.Implementations;

/**
 * This class provides utility methods for logging, including generating loggers
 * and filehandlers.
 * 
 * @author Phil
 *
 */
public class LoggingUtils {

	private static final String PATTERN = Implementations
			.getServerScreenTitle() + ".%u.%g.%h.txt";
	private static final boolean APPEND = true;

	/**
	 * Utility method that returns the logger needed for a specific class.
	 * 
	 * @param baseClass
	 * @return
	 */
	public static Logger getLogger(Class<?> baseClass) {
		try {
			Logger logger = Logger.getLogger(baseClass.getName());
			Handler fileHandler = new FileHandler(PATTERN, APPEND);
			logger.addHandler(fileHandler);
			return logger;
		} catch (IOException e) {
			return Logger.getLogger(baseClass.getName());
		}
	}

}
