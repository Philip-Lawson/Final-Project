/**
 * 
 */
package finalproject.poc.classloading;

import java.io.ObjectInputStream;

/**
 * @author Phil
 *
 */
public abstract class AbstractServerRequestHandler {
	
	private ServerRequest request;
	private AbstractServerRequestHandler nextHandler;
	
	public AbstractServerRequestHandler(){
		
	}
		
	protected abstract void handleHere(ObjectInputStream input);
	
	public void processRequest(int requestNum, ObjectInputStream input){
		if (requestNum == request.getRequestNum()){
			handleHere(input);
		} else {
			delegate(requestNum, input);
		}
	}
	
	protected void delegate(int requestNum, ObjectInputStream input){
		nextHandler.processRequest(requestNum, input);
	}
	
	public void setNextHandler(AbstractServerRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
	
	protected void setServerRequest(ServerRequest request){
		this.request = request;
	}

}
