import java.io.*;
import java.net.*;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected PrintWriter outputChannel  = null;
 	static int shared_var;

	synchronized void increment(){
		shared_var++;
	}
	synchronized int getSharedVar(){
			return shared_var;
	}

	public ServerThreadedWorker(Socket clientSocket , String serverText){
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		try{
		outputChannel = new PrintWriter(clientSocket.getOutputStream(),true);
		}catch(IOException e){
			System.out.println("Could not capture the output stream ");
		}
	}

	public ServerThreadedWorker( String serverText){
		this.serverText = serverText;
	}

	public void run(){
		int counter =0;
		try{
			//DataInputStream ireader = new DataInputStream( clientSocket.getInputStream() );
			//System.out.println("Num of chars available : " + ireader.read() );
			InputStream ireader= clientSocket.getInputStream();
			System.out.println("Str reade : ");
			System.out.println("Num of chars available : " + ireader.available() );
			int num_of_chars = ireader.available();
			char input_char_seq[] = new char[num_of_chars];
			for(int i =0;i<ireader.available() ; i++){
				input_char_seq[i] = (char) ireader.read();
			}
			String client_string = new String(input_char_seq);
			System.out.println( "str : " + client_string );
			
			int position_of_question =client_string.indexOf( '?' ,  client_string.indexOf("POST") );
			String query_params = client_string.substring(position_of_question + 1 , client_string.indexOf("&param=EOS") );
			System.out.println("Query param : " + query_params );
			String q_params[] = query_params.split("&");
			for(int i=0;i<q_params.length; i++)
				System.out.println("q : " + q_params[i]  );


			//for(int i =0;i<
		}catch(IOException e){
			
		}
		while(true){
			increment();
			System.out.println("Thread details : " + Thread.currentThread() + " , shared : " + getSharedVar() );
			try{
				//String header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "\r\n";
				String content = "AA"+counter;
				counter++;
 				outputChannel.println(content);
				Thread.currentThread().sleep(4000);
			}catch(InterruptedException e){
			}
		}
	}
}
