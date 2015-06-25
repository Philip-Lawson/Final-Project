
public class TestThreads {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calculator calc = new Calculator();

		Thread resumer = new ThreadCaller(calc);

		resumer.start();
		System.out.println("Interrupting");
		
		

		try {
			resumer.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void waitFor() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
