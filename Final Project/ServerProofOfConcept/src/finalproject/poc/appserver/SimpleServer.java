package finalproject.poc.appserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SimpleServer implements Runnable {

	private static final int THREAD_POOL_SIZE = 200;
	private static final int PORT = 12346;
	private static final char[] PASSWORD = "passphrase".toCharArray();
	private static final String KEYSTORE_FILE_NAME = "testkeys";
	private static final String KEY_STORE = "JKS";
	private static final String KEY = "SunX509";
	private static final String ALGORITHM = "TLS";

	private ExecutorService threadPool;
	private SSLContext context;
	private KeyManagerFactory keyFactory;
	private KeyStore keyStore;
	private SSLServerSocket server;
	private SSLServerSocketFactory socketFactory;
	private boolean listening = true;

	private SSLServerSocket getSecureSocket(int port)
			throws NoSuchAlgorithmException, KeyStoreException,
			CertificateException, FileNotFoundException, IOException,
			UnrecoverableKeyException, KeyManagementException {
		context = SSLContext.getInstance(ALGORITHM);
		keyFactory = KeyManagerFactory.getInstance(KEY);
		keyStore = KeyStore.getInstance(KEY_STORE);

		keyStore.load(new FileInputStream(KEYSTORE_FILE_NAME), PASSWORD);
		keyFactory.init(keyStore, PASSWORD);

		context.init(keyFactory.getKeyManagers(), null, null);
		socketFactory = context.getServerSocketFactory();

		return (SSLServerSocket) socketFactory.createServerSocket(port);

	}
	
	public void stopServer(){
		listening = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server = getSecureSocket(PORT);
			threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

			while (listening) {
				Socket connection = server.accept();
				threadPool.execute(new ServerThread(connection, this));
			}

		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Problem in Simple Server");
		}
	}

}
