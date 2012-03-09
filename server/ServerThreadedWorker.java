import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import serverstorage.Message;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected PrintWriter outputChannel  = null;
 	static char char_typed;
	protected String thread_type;
	protected InputStream input_stream;
	protected OutputStream output_stream;
	static int semaphore=0;
	int num_of_clients;
	static int countOfPersistentThreads=0;
	public static ArrayBlockingQueue<Message>  queue = new ArrayBlockingQueue<Message>(100);
	static int persistentThreadStatus[] = new int[100]; 		//There can be a max of 100 Persistent threads
	static int global_client_number =1;
	int current_client_number = 0;


	public ServerThreadedWorker(Socket clientSocket , String serverText, int num_of_clients){
		this.clientSocket = clientSocket;
		this.thread_type = serverText;
		this.num_of_clients = num_of_clients;
		try{
			//Set up the input & output channels
			output_stream = clientSocket.getOutputStream(); 
			outputChannel = new PrintWriter( output_stream ,true);
			input_stream= clientSocket.getInputStream();
			clientSocket.setKeepAlive(true);
		}catch(IOException e){
			System.out.println("Could not capture the imput/output stream ");
		}

		if(serverText == "Persistent"){
			//Reset the persistentThreadStatus array
			for(int i=0;i<100;i++)
				persistentThreadStatus[i] = 0;
			//Thread.currentThread().setName(Integer.toString(countOfPersistentThreads++) );
			//Thread.currentThread().setName("PUSHKAR " + Integer.toString(countOfPersistentThreads++ ) );
			this.current_client_number = global_client_number++;
		}
	}

	public ServerThreadedWorker( String serverText){
		this.serverText = serverText;
	}

	synchronized void setPersistentThreadStatus(int threadId){
		persistentThreadStatus[threadId] = 1;
	}
	synchronized void resetPersistentThreadStatus(int threadId){
		persistentThreadStatus[threadId] = 0;
	}
	synchronized void setPersistentThreadStatusAllThreads(){
		for(int i=0;i<100;i++)	
			persistentThreadStatus[i] = 1;
	}
	synchronized void resetPersistentThreadStatusAllThreads(){
		for(int i=0;i<100;i++)	
			persistentThreadStatus[i] = 0;
	}
	synchronized boolean getPersistentThreadStatus(int threadId){
		if( persistentThreadStatus[threadId] == 1)
			return true;
		else return false;
	}

	synchronized void setChar( Message client_msg ){
		//System.out.println("Char written by Thread : " + Thread.currentThread() );
		System.out.println("Char written : " + client_msg.getCharacter_pressed() + ", pos : "+ client_msg.getPosition() +" , revnum : "+ client_msg.getClient_version_number() + " , cid : " +client_msg.getClient_id() );
		this.char_typed = char_typed;
		char temp[] = new char[2];
		temp[0] = char_typed;
		try{
			queue.put( client_msg );
			queue.put( client_msg );
		}catch(InterruptedException e){
		}
		setPersistentThreadStatusAllThreads();
		//this.semaphore++;
	}
	synchronized char getCharTyped(){
		Message op = null;
		//System.out.println("String : " + Thread.currentThread().getName() );
		String threadName[] = Thread.currentThread().getName().split("-");
		int threadId=0;
		if(threadName[1].length()>0) 
			threadId = Integer.parseInt( threadName[1] );
		try{
			op = queue.take();
			resetPersistentThreadStatus( threadId);
		}catch(InterruptedException e){
		}
		return op.getCharacter_pressed();
			//return char_typed;
	}
	synchronized void increment(){
		this.semaphore++;
	}
	synchronized int getSemaphore(){
		System.out.println("Notified !");
		//return (int)queue.take();
		//if(semaphore==0){
			return semaphore;
		//}
		//semaphore--;
		//System.out.println("Sem returned : " + semaphore + " , for thread : " + Thread.currentThread());
		//return this.semaphore + 1;
	}


	public void runPersistentConnectionThread(){
		int counter =0;
		//String persistent_header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "Connection:Keep-Alive\r\n" + "\r\n";
		String persistent_header="ThreadId-"+ Integer.toString(current_client_number) ;
 		outputChannel.println( persistent_header);

		try{
			output_stream.flush();
		}catch(IOException e){
		}

		while(true){
			String threadName[] = Thread.currentThread().getName().split("-");
			int threadId = 0;
			if(threadName[1].length()>0) 
				threadId=Integer.parseInt( threadName[1] );
			//int threadId = Integer.parseInt( Thread.currentThread().getName()) ;
			if(getPersistentThreadStatus(threadId)){
				char typed = getCharTyped();
				System.out.println("Thread details : " + Thread.currentThread() + " Thread name ; " + Thread.currentThread().getName() );
				System.out.println("CHAR SENT : " + typed );
				//String persistent_header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "Connection:Keep-Alive\r\n" + "\r\n";
 				outputChannel.println(typed);
				try{
					output_stream.flush();
				}catch(IOException e){
				}
 				//outputChannel.println( persistent_header + typed.charAt(0));
			}
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
			if(client_string.length() == 0 || position_of_question <=0 ){
			}else{
				String query_params = client_string.substring(position_of_question+1,client_string.indexOf("&param=EOS"));
				//System.out.println("Query param : " + query_params );
				String q_params[] = query_params.split("&");
				String keyPressed="";
				int position=0, clientId=0, clientRevNum =0, bufferRevNum=0;
				for(int i=0;i<q_params.length; i++){
					if( q_params[i].contains("key") ){
						String  key[] = q_params[i].split("=") ;
						keyPressed = key[1];
					}else if( q_params[i].contains("pos")){
						String  positionArr[] = q_params[i].split("=") ;
						position = Integer.parseInt(positionArr[1] );
					}else if( q_params[i].contains("cid")){
						String  clientIdArr[] = q_params[i].split("=") ;
						clientId = Integer.parseInt( clientIdArr[1] );
					}else if( q_params[i].contains("revno")){
						String  revNumArr[] = q_params[i].split("=") ;
						clientRevNum = Integer.parseInt(revNumArr[1] );
					}
				}
				Message client_msg = new Message(keyPressed.charAt(0) , position, 0, clientRevNum , clientId) ;
				setChar( client_msg );
			}

		}catch(IOException e){
			
		}

		try{
			System.out.println("Printing to the output tream ");
			outputChannel.println("");
			output_stream.close();
			input_stream.close();
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
