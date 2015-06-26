package finalproject.poc.classloading;

public class ConcreteCalculator extends AbstractCalculator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5217554963081096931L;

	@Override
	public void execute(){
		System.out.println("Concrete execution");
	}
}
