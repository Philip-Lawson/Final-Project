/**
 * 
 */
package uk.ac.qub.finalproject.persistence;

/**
 * This enum represents the different achievement statuses that a client can
 * achieve.
 * 
 * @author Phil
 *
 */
public enum Achievements {

	BRONZE(300), SILVER(600), GOLD(1000);

	private int awardThreshold;

	private Achievements(int awardThreshold) {
		this.awardThreshold = awardThreshold;
	}

	/**
	 * Returns the number of contributions needed to send a user a
	 * congratulatory email.
	 * 
	 * @return
	 */
	public int getAwardThreshold() {
		return awardThreshold;
	}

	/**
	 * Allows the user to change the number of contributions needed to send a
	 * user a congratulatory email.
	 * 
	 * @param awardThreshold
	 */
	public void setAwardThreshold(int awardThreshold) {
		this.awardThreshold = awardThreshold;
	}

	@Override
	public String toString() {
		String string = super.toString();
		return string.charAt(0)
				+ string.substring(1, string.length()).toLowerCase();
	}

}
