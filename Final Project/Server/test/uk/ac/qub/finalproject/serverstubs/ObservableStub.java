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
	
	private boolean isObserved = false;
	
	private Object sentObject;
	
	private int timesNotified = 0;
	
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
		sentObject = arg1;
		timesNotified++;
	}

	public Object getSentbject() {
		return sentObject;
	}

	public void setSentObject(Object sentObject) {
		this.sentObject = sentObject;
	}

	public int getTimesNotified() {
		return timesNotified;
	}

	public void setTimesNotified(int timesNotified) {
		this.timesNotified = timesNotified;
	}

}
