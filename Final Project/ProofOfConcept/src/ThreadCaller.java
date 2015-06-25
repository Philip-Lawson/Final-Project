
public class ThreadCaller extends Thread {

	private Calculator calc;
	
	public ThreadCaller (Calculator calc){
		this.calc = calc;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int count=0; count<100; count++){
			System.out.println(count);	
			
		}		

	}

}
