import java.io.*;
import java.net.*;


public class DocumentServer {

	int number_of_clients;
	final int SERVER_PORT=5000;

	//This is required to send the entire Html
	public void establishInitialConnection() throws FileNotFoundException , IOException{
		FileReader html_file = new FileReader("page.html"); 
		BufferedReader html_file_br = new BufferedReader(html_file);

		String line_in_file, full_html = "";
		while( (line_in_file=html_file_br.readLine() ) != null){
			full_html+=line_in_file;
		}

		System.out.println("Waiting for connection ");
        ServerSocket Server = new ServerSocket (SERVER_PORT);
        Socket connection = Server.accept();
		PrintWriter outToClient = new PrintWriter(connection.getOutputStream(),true);
		String header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "\r\n";
 		outToClient.println(header +  full_html);
		connection.close();
		System.out.println("Sent the html ");
	}

	public void establishPersistentConnection() throws  FileNotFoundException , IOException {

        ServerSocket Server = new ServerSocket (SERVER_PORT);
		for(int i =0;i<this.number_of_clients ;i++){
        	Socket clientSocket = Server.accept();
			//String persistent_header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "Connection:Keep-Alive\r\n" + "\r\n";

       		new Thread( new ServerThreadedWorker(clientSocket, "Persistent")).start();
			System.out.println("Started new thread for worker ...  ! ");
	   }

	}

	public void startServer(){
		
		//Send Html for all the clients
		for(int i = 0;i<this.number_of_clients;i++){
			try{
				establishInitialConnection();
			}catch(FileNotFoundException e){
				System.out.println("Cannot establish initial connection");
			}catch(IOException e){
				System.out.println("Cannot establish initial connection!");
			}
		}

		//Establish Persistent connection with all the clients 
		try{
			establishPersistentConnection();
		}catch(FileNotFoundException e){
			System.out.println("Could not establish persistent connection");
		}catch(IOException e){
			System.out.println("Could not establish persistent connection");
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
