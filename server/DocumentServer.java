import java.io.*;
import java.net.*;


public class DocumentServer {

	int number_of_clients;
	final int SERVER_PORT=5000;
    ServerSocket Server = null;

	//This is required to send the entire Html
	public void establishInitialConnection() throws FileNotFoundException , IOException{
		String line_in_file, full_html = "";
		FileReader html_file = new FileReader("page.html"); 
		BufferedReader html_file_br = new BufferedReader(html_file);

		while( (line_in_file=html_file_br.readLine() ) != null){
			full_html=  full_html + line_in_file + "\n";
		}

		System.out.println("Waiting for connection ");

        ServerSocket Server;
		try{
        	Server = new ServerSocket (SERVER_PORT);
			Server.setReuseAddress(true);
		}catch(Exception e){
			System.out.println("Issue with close connection ?? !!!!!!!!! " + e.toString() +"\n\n" );
			//System.out.println("Cause : " + e.getCause() );
			return;
		}

        Socket connection = Server.accept();
		connection.setSoLinger(false, 0);
		PrintWriter outToClient = new PrintWriter(connection.getOutputStream(),true);
		String header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "\r\n";
 		outToClient.println(header +  full_html);
		connection.shutdownOutput();
		outToClient.close();
		connection.close();
		if(!connection.isClosed() ) {
			System.out.println("Cannot close the connection and create another 1 ");
			return;
		}

		System.out.println("Sent the html ");
		Server.close();
		return;
	}

	public void establishPersistentConnection() throws  FileNotFoundException , IOException {
		try{
        	Server = new ServerSocket (SERVER_PORT);
			Server.setReuseAddress(true);
		}catch(BindException e){
			System.out.println("Exception in establishPersistentConnection(). Cause : " + e.toString() );
			return;
		}

		//!!!!!!!!!!! REMOVE -1 !!!!!!!!!!!!
		for(int i =0;i<this.number_of_clients - 1 ;i++){
			System.out.println("Waiting for incoming Persistent conenctions num : " + i);
			Socket clientSocket = null;
			try{
        		clientSocket = Server.accept();
			}catch(Exception e){
				System.out.println("Cannot accept connections . Cause : "+ e.toString());
			}
			System.out.println("Connect button clicked ");

       		new Thread( new ServerThreadedWorker(clientSocket, "Persistent",number_of_clients)).start();
			//System.out.println("Started new thread for worker ...  ! ");
	   }

	}

	public void processKeyPress() throws   IOException {
		/*
		try{
        	Server = new ServerSocket (SERVER_PORT);
			Server.setReuseAddress(true);
		}catch(BindException e){
			System.out.println("Exception in processKeyPress(). Cause : " + e.toString() );
			return;
		}
		*/
		while(true){
			System.out.println("Waiting for incoming key press Msgs ");
			Socket clientSocket = null;
			try{
        		clientSocket = Server.accept();
			}catch(Exception e){
				System.out.println("Cannot accept connections . Cause : "+ e.toString());
			}

			//System.out.println("Key pressed. Spawning new thread");
       		new Thread( new ServerThreadedWorker(clientSocket, "Key" , number_of_clients)).start();
	   }
	}

	public void startServer(){
		
		//Send Html for all the clients
		for(int i = 0;i<this.number_of_clients;i++){
			try{
				establishInitialConnection();
			}catch(FileNotFoundException e){
				System.out.println("Cannot establish initial connection. FileNotFoundException");
			}catch(IOException e){
				System.out.println("Cannot establish initial connection. IOException" + e.toString());
			}/*catch(InterruptedException e){
				System.out.println("Cannot establish initial connection. InterruptedException");
			}*/
		}

		System.out.println("Initial connection setup. Now setting up the persistent connection ");
		//Establish Persistent connection with all the clients 
		try{
			establishPersistentConnection();
		}catch(FileNotFoundException e){
			System.out.println("Could not establish persistent connection. FileNotFoundException");
		}catch(IOException e){
			System.out.println("Could not establish persistent connection. IOException");
		}	

		System.out.println("Set up all connections !! Now setting up Handler for key press msgs");
	
		try{
			processKeyPress();
		}catch(FileNotFoundException e){
			System.out.println("Could not establish persistent connection. FileNotFoundException");
		}catch(IOException e){
			System.out.println("Could not establish persistent connection. IOException");
		}	
	}

	public DocumentServer(int number_of_clients){
		 this.number_of_clients = number_of_clients;

	}
	
	public static void main(String args[]){
		int number_of_clients=0;
		try{
			number_of_clients = Integer.parseInt(args[0]);
		} catch(Exception e){
			System.out.println("Please mention the number of clients. Usage : DocumentServer [number] ");
		}


		if(number_of_clients == 0)
			number_of_clients = 3;

		DocumentServer collab_doc_server = new DocumentServer(number_of_clients);

		System.out.println("Starting the server.");
		collab_doc_server.startServer();
	
	}
}
