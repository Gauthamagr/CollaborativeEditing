//TCPServer.java
import java.io.*;
import java.net.*;

class TCPServer 
{
   public static void main(String argv[]) throws Exception
      {
         String fromclient;
         String toclient;
          
         ServerSocket Server = new ServerSocket (5000);
         
         System.out.println ("TCPServer Waiting for client on port 5000");

/********************/
         	String toclient2;
         	Socket connected2 = Server.accept();
            System.out.println( " THE CLIENT"+" "+
            connected2.getInetAddress() +":"+connected2.getPort()+" IS CONNECTED ");
            
            BufferedReader inFromUser2 = 
            new BufferedReader(new InputStreamReader(System.in));    
     
            BufferedReader inFromClient2 =
               new BufferedReader(new InputStreamReader (connected2.getInputStream()));
                  
            PrintWriter outToClient2 =
               new PrintWriter(
                  connected2.getOutputStream(),true);

			String header="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/html\r\n" + "\r\n";
			//String header="HTTP/1.1 200 OK\r\n"+"Content-Type: text/html\r\n\r\n\r\n";

 		//outToClient2.println(header);
            //toclient2 = "<html> <head><title> Collaborative Document Editing</title> </head> <body> <h1 style=\"text-align:center;\"> Shared Document 1</h1> <div style=\"margin-left:200px;\"> <textarea id=\"mainDoc\" style=\"width:900px;height:400px;\"></textarea> <div style=\"margin-top:20px;\"> <span> Key pressed : <span id=\"textReplace\"> </span>  ... &nbsp; &nbsp;Location : </span> <span id=\"location\"></span> <div> <div id=\"pushkar\"> AAA </div> <script type=\"text/javascript\"> alert(\"comin here r\"); var mainDoc = document.getElementById(\"mainDoc\"); xmlhttp=new XMLHttpRequest(); xmlhttp.onreadystatechange=function(){if(xmlhttp.status==200) { document.getElementById(\"pushkar\").innerHTML=xmlhttp.responseText; } }; xmlhttp.open(\"GET\",\"192.168.0.9:5000\",true); xmlhttp.send(); </script> </div> </body> </html>";
            toclient2 = "<html> <head><title> Collaborative Document Editing</title> </head> <body> <h1 style=\"text-align:center;\"> Shared Document 1</h1> <div style=\"margin-left:200px;\"> <textarea id=\"mainDoc\" style=\"width:900px;height:400px;\"></textarea> <div style=\"margin-top:20px;\"> <span> Key pressed : <span id=\"textReplace\"> </span>  ... &nbsp; &nbsp;Location : </span> <span id=\"location\"></span> <div> <div id=\"pushkar\"> AAA </div> <script type=\"text/javascript\"> alert(\"comin here r\"); var mainDoc = document.getElementById(\"mainDoc\"); xmlhttp=new XMLHttpRequest(); xmlhttp.onreadystatechange=function(){if(xmlhttp.status==200) { document.getElementById(\"mainDoc\").innerHTML=xmlhttp.responseText; } }; xmlhttp.open(\"GET\",\"\",true); xmlhttp.send(); </script> </div> </body> </html>";
//toclient2 = "<html><head><title>Page title </title></head><body> <h1> AAsfafa fasf</h1> </body></html>";

 outToClient2.println(header +  toclient2);
            connected2.close();
            

/********************/

         while(true) 
         {
         	Socket connected = Server.accept();
            System.out.println( " THE CLIENT"+" "+
            connected.getInetAddress() +":"+connected.getPort()+" IS CONNECTED ");
            
            BufferedReader inFromUser = 
            new BufferedReader(new InputStreamReader(System.in));    
     
            BufferedReader inFromClient =
               new BufferedReader(new InputStreamReader (connected.getInputStream()));
                  
            PrintWriter outToClient =
               new PrintWriter(
                  connected.getOutputStream(),true);
            
            while ( true )
            {
            	
            	System.out.println("SEND(Type Q or q to Quit):");
            	toclient = inFromUser.readLine();
            	
            	if ( toclient.equals ("q") || toclient.equals("Q") )
            	{
            		outToClient.println(toclient);
            		connected.close();
            		break;
            	}
            	else
            	{
				String header2="HTTP/1.1: 200 OK\r\n"+"Content-Type: text/plain\r\n" + "Cache-Control: private\r\n" +"Connection: Keep-Alive\r\n" + "\r\n" ;
				header2 = "";
            	outToClient.println(header2 +toclient);
                }
            	
            	fromclient = inFromClient.readLine();
            	
                if ( fromclient.equals("q") || fromclient.equals("Q") )
                {
                	connected.close();
                	break;
                }
                	
		        else
		        {
		         System.out.println( "RECIEVED:" + fromclient );
		        } 
			    
			}  
			
          }
      }
}
