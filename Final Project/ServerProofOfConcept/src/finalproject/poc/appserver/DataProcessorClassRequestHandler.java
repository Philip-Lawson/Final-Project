/**
 * 
 */
package finalproject.poc.appserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import finalproject.poc.calculationclasses.DataProcessorClassWriter;

/**
 * @author Phil
 *
 */
public class DataProcessorClassRequestHandler extends
		AbstractClientRequestHandler {
	
	private DataProcessorClassWriter classWriter;
	
	public DataProcessorClassRequestHandler(){
		super();
	}
	
	public DataProcessorClassRequestHandler(DataProcessorClassWriter classWriter){
		super();
		this.classWriter = classWriter;
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#getRequestNum()
	 */
	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ClientRequest.REQUEST_CALCULATION_CLASS.getRequestNum();
	}

	/* (non-Javadoc)
	 * @see finalproject.poc.appserver.AbstractClientRequestHandler#handleHere(java.io.ObjectInputStream, java.io.ObjectOutputStream)
	 */
	@Override
	protected void handleHere(ObjectInputStream input, ObjectOutputStream output) {
		// TODO Auto-generated method stub
		try {
			output.reset();
			output.writeInt(ServerRequest.LOAD_CALCULATOR_CLASS.getRequestNum());
			output.writeObject(classWriter.getClassBytes());
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
