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
		
	protected abstract void handleHere(ObjectInputStream input, ObjectOutputStream output, Client client);
	
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output, Client client){
		if (requestNum == request.getRequestNum()){
			handleHere(input, output, client);
		} else {
			delegate(requestNum, input, output, client);
		}
	}
	
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output, Client client){
		nextHandler.processRequest(requestNum, input, output, client);
	}
	
	public void setNextHandler(AbstractServerRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
	
	protected void setServerRequest(ServerRequest request){
		this.request = request;
	}

}
