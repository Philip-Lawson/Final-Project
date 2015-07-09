/**
 * 
 */
package com.example.appproofofconcept;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import finalproject.poc.calculationclasses.ProcessingClassLoader;

/**
 * @author Phil
 *
 */
public class LoadProcessingClassRequestHandler extends AbstractServerRequestHandler {

	@Override
	protected int getRequestNum() {
		// TODO Auto-generated method stub
		return ServerRequest.LOAD_PROCESSING_CLASS;
	}

	@Override
	protected void handleHere(ObjectInputStream input,
			ObjectOutputStream output, Client client) {
		// TODO Auto-generated method stub
		try {
			byte[] classBytes = (byte[]) input.readObject();
			ProcessingClassLoader.loadClass(classBytes);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
