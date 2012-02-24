import java.io.*;
import java.net.*;


public class DocumentServer {
	
	public void startServer(int number_of_clients){

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

		DocumentServer collab_doc_server = new DocumentServer();

		System.out.println("Starting the server.");
		collab_doc_server.startServer(number_of_clients);
	
	}
}
