/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Phil
 *
 */
public class DuplicatesNumChangeListener implements ChangeListener<Integer> {

	private AbstractWorkPacketDrawer workPacketManager;

	public DuplicatesNumChangeListener() {

	}

	public DuplicatesNumChangeListener(
			AbstractWorkPacketDrawer workPacketManager) {
		this.workPacketManager = workPacketManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue
	 * , java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> arg0, Integer oldValue,
			Integer newValue) {
		if (newValue > 0){
			workPacketManager.setNumberOfCopies(newValue);
		} 
	}

}
