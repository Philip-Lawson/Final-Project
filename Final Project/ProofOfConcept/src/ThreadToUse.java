
public class ThreadToUse implements Runnable {

	private Calculator calc;

	public ThreadToUse(Calculator calc) {
		this.calc = calc;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (calc) {
			
			try {
				calc.wait();
				System.out.println("Calculator waiting");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Interrupted");
			}
		}

	}

}
