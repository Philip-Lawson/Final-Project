/**
 * 
 */
package com.example.testingagainnnn;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public abstract class AbstractServerRequestHandler {
	
	private ServerRequest request;
	private AbstractServerRequestHandler nextHandler;
	
	public AbstractServerRequestHandler(){
		
	}
		
	protected abstract void handleHere(ObjectInputStream input, ObjectOutputStream output);
	
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		if (requestNum == request.getRequestNum()){
			handleHere(input, output);
		} else {
			delegate(requestNum, input, output);
		}
	}
	
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		nextHandler.processRequest(requestNum, input, output);
	}
	
	public void setNextHandler(AbstractServerRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
	
	protected void setServerRequest(ServerRequest request){
		this.request = request;
	}

}
