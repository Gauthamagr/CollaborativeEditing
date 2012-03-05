import java.io.*;
import java.net.*;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected PrintWriter outputChannel  = null;
 	static int shared_var;
	protected String thread_type;
	protected InputStream input_stream;


	public ServerThreadedWorker(Socket clientSocket , String serverText){
		this.clientSocket = clientSocket;
		this.thread_type = serverText;
		try{
			//Set up the input & output channels
			outputChannel = new PrintWriter(clientSocket.getOutputStream(),true);
			input_stream= clientSocket.getInputStream();
		}catch(IOException e){
			System.out.println("Could not capture the imput/output stream ");
		}
	}

	public ServerThreadedWorker( String serverText){
		this.serverText = serverText;
	}

	synchronized void increment(){
		shared_var++;
	}
	synchronized int getSharedVar(){
			return shared_var;
	}

	public void runPersistentConnectionThread(){
		int counter =0;
		while(true){
			increment();
			//System.out.println("Thread details : " + Thread.currentThread() + " , shared : " + getSharedVar() );
			try{
				String content = "AA"+counter;
				counter++;
 				outputChannel.println(content);
				Thread.currentThread().sleep(4000);
			}catch(InterruptedException e){
			}
		}

	}

	void runKeyPressThread(){
		try{
			System.out.println("Num of chars available : " + input_stream.available() );
			int num_of_chars = input_stream.available();
			char input_char_seq[] = new char[num_of_chars];
			for(int i =0;i<input_stream.available() ; i++){
				input_char_seq[i] = (char) input_stream.read();
			}
			String client_string = new String(input_char_seq);
			System.out.println( "str : " + client_string );
			
			int position_of_question =client_string.indexOf( '?' ,  client_string.indexOf("POST") );
			String query_params = client_string.substring(position_of_question + 1 , client_string.indexOf("&param=EOS") );
			System.out.println("Query param : " + query_params );
			String q_params[] = query_params.split("&");
			for(int i=0;i<q_params.length; i++)
				System.out.println("q : " + q_params[i]  );

		}catch(IOException e){
			
		}
		
	}

	public void run(){
		if(this.thread_type == "Persistent" )
			runPersistentConnectionThread();
		else if(this.thread_type == "Key")
			runKeyPressThread();
		
	}
}
