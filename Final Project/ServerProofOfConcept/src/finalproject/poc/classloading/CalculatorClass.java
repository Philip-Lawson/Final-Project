package finalproject.poc.classloading;

import java.io.Serializable;

public class CalculatorClass implements Calculator, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7670471368407778855L;

	public void execute(){
		System.out.println("Calculator class loaded");
	}

}
