/**
 * 
 */
package finalproject.poc.persistence;

/**
 * @author Phil
 *
 */
public enum Achievements {
	
	BRONZE(300), SILVER(600), GOLD(1000);
	
	private int awardThreshold;
	
	private Achievements(int awardThreshold){
		this.awardThreshold = awardThreshold;
	}
	
	public int getAwardThreshold(){
		return awardThreshold;
	}
	
	public void setAwardThreshold(int awardThreshold){
		this.awardThreshold = awardThreshold;
	}
	
	@Override
	public String toString(){
		String string = super.toString();		
		return string.charAt(0) + string.substring(1, string.length()).toLowerCase();
	}

}
