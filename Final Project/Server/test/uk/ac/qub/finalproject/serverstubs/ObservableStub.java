/**
 * 
 */
package uk.ac.qub.finalproject.serverstubs;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Phil
 *
 */
public class ObservableStub implements Observer {
	
	private boolean isObserved = false;;
	
	public boolean isObserved(){
		return isObserved;
	}
	
	public void setObserved(boolean isObserved) {
		this.isObserved = isObserved;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		isObserved = true;
	}

}
