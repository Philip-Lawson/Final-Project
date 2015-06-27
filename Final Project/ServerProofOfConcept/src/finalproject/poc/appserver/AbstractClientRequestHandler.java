/**
 * 
 */
package finalproject.poc.appserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public abstract class AbstractClientRequestHandler {
	
	private ClientRequest request;
	private AbstractClientRequestHandler nextHandler;
	
	public AbstractClientRequestHandler(){
		
	}
		
	protected abstract void handleHere(ObjectInputStream input, ObjectOutputStream output);
	
	public void processRequest(int requestNum, ObjectInputStream input, ObjectOutputStream output){
	System.out.println("Processing request");
		
		if (requestNum == request.getRequestNum()){
			handleHere(input, output);
		} else {
			delegate(requestNum, input, output);
		}
	}
	
	protected void delegate(int requestNum, ObjectInputStream input, ObjectOutputStream output){
		nextHandler.processRequest(requestNum, input, output);
	}
	
	public void setNextHandler(AbstractClientRequestHandler nextHandler){
		this.nextHandler = nextHandler;
	}
	
	protected void setClientRequest(ClientRequest request){
		this.request = request;
	}

}
