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
public class PacketsNumChangeListener implements ChangeListener<Integer> {

	private AbstractWorkPacketDrawer workPacketsManager;
	
	public PacketsNumChangeListener(AbstractWorkPacketDrawer workPacketsManager){
		this.workPacketsManager = workPacketsManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue
	 * , java.lang.Object, java.lang.Object)
	 */
	@Override
	public void changed(ObservableValue<? extends Integer> arg0,
			Integer oldValue, Integer newValue) {
		if (newValue > 0) {
			workPacketsManager.setPacketsPerList(newValue);
		}
	}

}
