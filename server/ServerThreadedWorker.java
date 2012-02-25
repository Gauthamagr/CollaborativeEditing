import java.io.*;
import java.net.*;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

	public ServerThreadedWorker(Socket clientSocket , String serverText){
		this.clientSocket = clientSocket;
		this.serverText = serverText;
	}

	public void run(){
		System.out.println("Thread details : " + Thread.currentThread() );
	}
}
