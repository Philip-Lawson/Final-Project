package finalproject.poc.classloading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class LoadCalculationClassRequestHandler extends
		AbstractServerRequestHandler {

	private static ServerRequest serverRequest = ServerRequest.LOAD_CALCULATOR_CLASS;

	public LoadCalculationClassRequestHandler() {
		setServerRequest(serverRequest);
	}

	@Override
	protected void handleHere(ObjectInputStream input) {
		// TODO Auto-generated method stub
		ClassLoader classLoader = new AppClassLoader(input);
		Class<?> c = null;
		Class<? extends Calculator> calculator = null;

		try {
			String className = (String) input.readObject();

			System.out.println(className);
			c = classLoader.loadClass(className);	
			
			System.out.println("Class loaded");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			if (c != null) {
				calculator = c.asSubclass(Calculator.class);
				Constructor<? extends Calculator> ctor = calculator.getConstructor();
				Calculator calc = ctor.newInstance();
				calc.execute();

			} else {
				System.out.println("Null class");
			}

		} catch (InstantiationException  | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void delegate(int requestNum, ObjectInputStream input) {

	}

}
