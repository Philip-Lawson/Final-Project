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
public abstract class AbstractServerRequestHandler {
	
	
	private AbstractServerRequestHandler nextHandler;
	
	public AbstractServerRequestHandler(){
		
	}
	
	public void setNextHandler(AbstractServerRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
	
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		System.out.println("Handling request");
		if (requestNum == getRequestNum()){
			handleHere(input, output);
		} else {
			delegate(requestNum, input, output);
		}
	}
	
	protected abstract int getRequestNum();
	
	protected abstract void handleHere(ObjectInputStream input, ObjectOutputStream output);
	
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		nextHandler.processRequest(requestNum, input, output);
	}
	
	
	
	
	

}
