import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import serverstorage.*;

public class ServerThreadedWorker implements  Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected PrintWriter outputChannel  = null;
 	static char char_typed;
	protected String thread_type;
	protected InputStream input_stream;
	protected OutputStream output_stream;
	static int semaphore=0;
	static int num_of_clients;
	static int persistent_thread_count;
	static int countOfPersistentThreads=0;
	public static ArrayBlockingQueue<Message>  queue = new ArrayBlockingQueue<Message>(100);
	public static ArrayBlockingQueue<AckBroadcast>  ack_broadcast_queue = new ArrayBlockingQueue<AckBroadcast>(100);
	static int persistentThreadStatus[] = new int[100]; 		//There can be a max of 100 Persistent threads
	static int global_client_number =1;
	static int current_revision_number_for_client[] = new int[100];
	int current_client_number = 0;


	public ServerThreadedWorker(Socket clientSocket , String serverText, int num_of_clients){
		this.clientSocket = clientSocket;
		this.thread_type = serverText;
		ServerThreadedWorker.num_of_clients = num_of_clients;
		ServerThreadedWorker.persistent_thread_count = num_of_clients;
		setPersistentThreadStatusAllThreads();


		try{
			//Set up the input & output channels
			output_stream = clientSocket.getOutputStream(); 
			outputChannel = new PrintWriter( output_stream ,true);
			input_stream= clientSocket.getInputStream();
			clientSocket.setKeepAlive(true);
		}catch(IOException e){
			System.out.println("Could not capture the input/output stream ");
		}

		if(serverText == "Persistent"){
			//Reset the persistentThreadStatus array
			for(int i=0;i<100;i++)
				persistentThreadStatus[i] = 0;
			//Thread.currentThread().setName(Integer.toString(countOfPersistentThreads++) );
			//Thread.currentThread().setName("PUSHKAR " + Integer.toString(countOfPersistentThreads++ ) );
			this.current_client_number = global_client_number++;

			for(int i =0;i<100;i++)
				current_revision_number_for_client[i]=0;
		}
	}

	public ServerThreadedWorker( String serverText){
		this.thread_type = serverText;
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

	synchronized int getCurrentRevisionNum(int client_id){
		return current_revision_number_for_client[client_id];
	}

	synchronized void setCurrentRevisionNum(int client_id, int revision_num){
			current_revision_number_for_client[client_id] = revision_num;
	}

	synchronized void setChar( Message client_msg ){
		//System.out.println("Char written by Thread : " + Thread.currentThread() );
		//System.out.println("Char written : " + client_msg.getCharacter_pressed() + ", pos : "+ client_msg.getPosition() +" , revnum : "+ client_msg.getClient_version_number() + " , cid : " +client_msg.getClient_id() );
		ServerThreadedWorker.char_typed = char_typed;
		char temp[] = new char[2];
		temp[0] = char_typed;
		try{
			int client_id = client_msg.getClient_id();
			int revision_num = client_msg.getClient_version_number();
			System.out.println("Client id : "+ client_id + " , rev num : "+ revision_num );
			int loop_counter=0;
			while(revision_num > getCurrentRevisionNum(client_id)+1){
				Thread.currentThread().sleep(20);
				System.out.println("sleeping ! Revnum of this client : " + revision_num + " , global rev num : "+ current_revision_number_for_client[client_id] );
			loop_counter++;
			if(loop_counter>100)	//To prevent infinite loop
				break;
			}
			queue.put( client_msg );
			setCurrentRevisionNum(client_id,revision_num);
		}catch(InterruptedException e){
		}
		//setPersistentThreadStatusAllThreads();
		//this.semaphore++;
	}
	

	synchronized AckBroadcast getProcessedMessage(){
		AckBroadcast op = null;
		//System.out.println("String : " + Thread.currentThread().getName() );
		String threadName[] = Thread.currentThread().getName().split("-");
		int threadId=0;
		if(threadName[1].length()>0) 
			threadId = Integer.parseInt( threadName[1] );
		try{
			op = ack_broadcast_queue.take();
			resetPersistentThreadStatus( threadId);
		}catch(InterruptedException e){
		}
		return op;
			//return char_typed;
	}

	synchronized void increment(){
		ServerThreadedWorker.semaphore++;
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
		String persistent_header="<script>ThreadId-"+ Integer.toString(current_client_number) +"-</script>";

 		outputChannel.println( persistent_header);

		try{
			output_stream.flush();
		}catch(IOException e){
		}
		
		String threadName[] = Thread.currentThread().getName().split("-");
		int threadId = 0;
		if(threadName[1].length()>0) 
			threadId=Integer.parseInt( threadName[1] );

		while(true){
			try{
				Thread.currentThread().sleep(20);
			}catch(Exception e){

			}

			//int threadId = Integer.parseInt( Thread.currentThread().getName()) ;
			if(getPersistentThreadStatus(threadId)){
				
				
				AckBroadcast processed_message = getProcessedMessage();
				
				//the below code is to ensure that the same thread does not pop same message from abstract queue	 
				resetPersistentThreadStatus(threadId);
				persistent_thread_count--;
				
				if(persistent_thread_count==0){
					persistent_thread_count=num_of_clients;
					setPersistentThreadStatusAllThreads();
				}
				
				
				int clientId = processed_message.getThread_id();
				char typed_char = processed_message.getCharacter_pressed();
				if(clientId != threadId + 1) {
					//Send as broadcast else skip below step
					//processed_message.setOriginal_client_version_number(0);										
				}
				
				//System.out.println("Thread details : " + Thread.currentThread() + " Thread name ; " + Thread.currentThread().getName() );
				System.out.println("CHAR SENT : " + typed_char + " , To client : " + Thread.currentThread().getName() );

				//Output message format - |Char,position,clientId,RevisionNum|
				int position = processed_message.getPosition();
				int server_revision_number = processed_message.getServer_version_number();
				int original_client_version_number = processed_message.getOriginal_client_version_number();
				String output_message="|"+typed_char+","+position + ","+clientId+","+ server_revision_number +","+ original_client_version_number+"|";
				System.out.println("Message sent : " + output_message );
 				outputChannel.println(output_message);
				try{
					output_stream.flush();
				}catch(IOException e){
				}
			}
		}

	}

	void runKeyPressThread(){
		try{
			//System.out.println("Num of chars available : " + input_stream.available() );
			int num_of_chars = input_stream.available();
		   while( num_of_chars<=0){
               try{
               Thread.currentThread().sleep(10);
               }catch(InterruptedException e){
                   System.out.println("NOT able to sleep :( :( :( ");
               }
               num_of_chars = input_stream.available();
           }
			char input_char_seq[] = new char[num_of_chars];
			for(int i =0;i<input_stream.available() ; i++){
				input_char_seq[i] = (char) input_stream.read();
			}
			String client_string = new String(input_char_seq);
			//System.out.println( "str : " + client_string );
			
			int position_of_question =client_string.indexOf( '?' ,  client_string.indexOf("POST") );
			if(client_string.length() == 0 || position_of_question <=0 ){
				System.out.println("\n\nMESSAGE MISSING !!!!!!!!!!!!!!\n\n\n");
			}else{
				String query_params = client_string.substring(position_of_question+1,client_string.indexOf("&param=EOS"));
				//System.out.println("Query param : " + query_params );
				String q_params[] = query_params.split("&");
				String keyPressed="";
				int position=0, clientId=0, clientRevNum =0,serverRevNum =0, bufferRevNum=0;
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
					}else if( q_params[i].contains("server_revno")){
						String  revNumArr[] = q_params[i].split("=") ;
						serverRevNum = Integer.parseInt(revNumArr[1] );
					}
					else if( q_params[i].contains("client_revno")){
						String  revNumArr[] = q_params[i].split("=") ;
						clientRevNum = Integer.parseInt(revNumArr[1] );
					}
				}
				Message client_msg = new Message(keyPressed.charAt(0) , position, serverRevNum, clientRevNum , clientId) ;
				//System.out.println("Client rev nu : "+ clientRevNum +" , serverRevnum L : " + serverRevNum);
				System.out.println("Char : "+ keyPressed.charAt(0)  + " , from client : " + clientId);
				setChar( client_msg );
			}

		}catch(IOException e){
			
		}

		try{
			outputChannel.println("");
			output_stream.close();
			input_stream.close();
			clientSocket.close();	
		}catch(Exception e){
			
		}
	}

	void runServerThread(){
		DataHandler handler = new DataHandler();
		AckBroadcast ack_broadcast = null;
		
		while(true){
			Message op = null;
			try{
				op = queue.take();			
			}catch(InterruptedException e){
			}
			ack_broadcast = handler.InsertDataIntoServerStore(op);
			for(int i=0;i<num_of_clients;i++){
				try {
					ack_broadcast_queue.put(ack_broadcast);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	
	public void run(){
		if(this.thread_type == "Persistent" )
			runPersistentConnectionThread();
		else if(this.thread_type == "Key")
			runKeyPressThread();
		else if(this.thread_type == "Server")
			runServerThread();
		
	}
}
