/**
 * 
 */
package finalproject.poc.classloading;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public class DummyRequestHandler extends AbstractServerRequestHandler {

	/* (non-Javadoc)
	 * @see finalproject.poc.classloading.AbstractServerRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.classloading.AbstractServerRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub

	}

}
