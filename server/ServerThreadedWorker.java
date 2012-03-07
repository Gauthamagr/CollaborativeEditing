import java.io.*;
import java.net.*;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected PrintWriter outputChannel  = null;
 	static char char_typed;
	protected String thread_type;
	protected InputStream input_stream;
	static int semaphore=0;
	int num_of_clients;


	public ServerThreadedWorker(Socket clientSocket , String serverText, int num_of_clients){
		this.clientSocket = clientSocket;
		this.thread_type = serverText;
		this.num_of_clients = num_of_clients;
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

	synchronized void setChar(char char_typed){
		System.out.println("Char written by Thread : " + Thread.currentThread() );
		this.char_typed = char_typed;
		this.semaphore++;
	}
	synchronized int getCharTyped(){
			return char_typed;
	}
	synchronized void increment(){
		this.semaphore++;
	}
	synchronized int getSemaphore(){
		if(semaphore==0){
			return semaphore;
		}
		semaphore--;
		System.out.println("Sem returned : " + semaphore + " , for thread : " + Thread.currentThread());
		return this.semaphore + 1;
	}


	public void runPersistentConnectionThread(){
		int counter =0;
		String persistent_header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "Connection:Keep-Alive\r\n" + "\r\n";
 		outputChannel.println( persistent_header);
		while(true){
			//try{
				if(getSemaphore()>0){
					char typed = (char )getCharTyped();
					System.out.println("Thread details : " + Thread.currentThread() + " Thread name ; " + Thread.currentThread().getName() );
					System.out.println("CHAR SENT ! Semaphore ="+ this.semaphore);
 					outputChannel.println(typed);
				}
			//}catch(InterruptedException e){
			//}
		}

	}

	void runKeyPressThread(){
		try{
			//System.out.println("Num of chars available : " + input_stream.available() );
			int num_of_chars = input_stream.available();
			char input_char_seq[] = new char[num_of_chars];
			for(int i =0;i<input_stream.available() ; i++){
				input_char_seq[i] = (char) input_stream.read();
			}
			String client_string = new String(input_char_seq);
			//System.out.println( "str : " + client_string );
			
			int position_of_question =client_string.indexOf( '?' ,  client_string.indexOf("POST") );
			String query_params = client_string.substring(position_of_question + 1 , client_string.indexOf("&param=EOS") );
			//System.out.println("Query param : " + query_params );
			String q_params[] = query_params.split("&");
			for(int i=0;i<q_params.length; i++){
				if( q_params[i].contains("key") ){
					System.out.println("Semaphore value b4 : " + this.semaphore);
					String  key[] = q_params[i].split("=") ;
					System.out.println("Key : " + key[1] );
					setChar(  key[1].charAt(0) );
					System.out.println("Semaphore value after : " + this.semaphore);

				}
			}

		}catch(IOException e){
			
		}
		try{
			input_stream.close();
			outputChannel.println("a");
			clientSocket.close();	
		}catch(Exception e){
			
		}
	}

	public void run(){
		if(this.thread_type == "Persistent" )
			runPersistentConnectionThread();
		else if(this.thread_type == "Key")
			runKeyPressThread();
		
	}
}
