/**
 * 
 */
package com.example.appproofofconcept;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public abstract class AbstractServerRequestHandler {
	
	private AbstractServerRequestHandler nextHandler;
	
	public AbstractServerRequestHandler(){
		
	}
	
	public void setNextHandler(AbstractServerRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
		
	
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output, Client client){
		if (requestNum == getRequestNum()){
			handleHere(input, output, client);
		} else {
			delegate(requestNum, input, output, client);
		}
	}
	
	protected abstract int getRequestNum();
	
	protected abstract void handleHere(ObjectInputStream input, ObjectOutputStream output, Client client);
		
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output, Client client){
		nextHandler.processRequest(requestNum, input, output, client);
	}
	
	
	
	

}
