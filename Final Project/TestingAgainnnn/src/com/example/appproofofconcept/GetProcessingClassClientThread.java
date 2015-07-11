/**
 * 
 */
package com.example.appproofofconcept;

import java.io.IOException;

import finalproject.poc.calculationclasses.IDataProcessor;
import finalproject.poc.calculationclasses.ProcessingClassLoader;

/**
 * @author Phil
 *
 */
public class GetProcessingClassClientThread extends Client {

	/* (non-Javadoc)
	 * @see com.example.appproofofconcept.Client#communicateWithServer()
	 */
	@Override
	protected void communicateWithServer() throws IOException {
		// TODO Auto-generated method stub
		output.writeInt(ClientRequest.REQUEST_PROCESSING_CLASS);
		input.readInt();
		
		try {
			byte[] classBytes = (byte[]) input.readObject();
			IDataProcessor processor = ProcessingClassLoader.loadClass(classBytes);
			DataProcessor.changeProcessorClass(processor);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			cancelConnection();
		}
		

	}

}
