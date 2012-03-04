package serverstorage;


import java.util.Vector;



public class DataHandler {
	
	
	StringBuffer data_store;
	int server_version_number; 
	Vector<Message> in_queue; // Input packets must be stored here
	Vector<HistoryElement> history_queue;
	
	
	DataHandler(){
		server_version_number=0;
		in_queue = new Vector<Message>();
		history_queue = new Vector<HistoryElement>();
		data_store = new StringBuffer();
		for (int i=0; i <1000; i++)
			data_store.append(" ");
	}
	
	
	
	public void InsertSimple(HistoryElement history_element, Message in_packet){
		Constants constants = new Constants();
		
		server_version_number++;
		if(in_packet.getCharacter_pressed()==constants.BACKSPACE)
			data_store.deleteCharAt(in_packet.getPosition() - 1);
		else if (in_packet.getCharacter_pressed()==constants.DELETE)
			data_store.deleteCharAt(in_packet.getPosition());
		else
			data_store.insert(in_packet.getPosition(), in_packet.getCharacter_pressed());
		
		history_element.setCharacter_pressed(in_packet.getCharacter_pressed());
		history_element.setIp_address(in_packet.getIp_address());
		history_element.setPosition(in_packet.getPosition());
		history_element.setVersion_number(server_version_number);
		
		history_queue.add(history_element);
	}
	
	
	
	public void InsertDataIntoServerStore(){
		while (!in_queue.isEmpty()){
			Message in_packet = new Message();
			HistoryElement history_element = new HistoryElement();
			
			in_packet = in_queue.remove(0);
			if (in_packet.getBuffer_version_number()==server_version_number){
				
				InsertSimple(history_element, in_packet);
				
				// PREPARE ACK back to same IP
				// PREPARE BROADCAST to all IPs
				
			}
			else if (in_packet.getBuffer_version_number()<server_version_number){
								
				for(int i=0;i<history_queue.size();i++){
					if (history_queue.elementAt(i).getVersion_number()>in_packet.getBuffer_version_number()){
						if(history_queue.elementAt(i).getIp_address()!=in_packet.getIp_address() 
								&& history_queue.elementAt(i).getPosition()<=in_packet.getPosition())
							if(history_queue.elementAt(i).getCharacter_pressed()==8 || history_queue.elementAt(i).getCharacter_pressed()==24 )
								in_packet.setPosition(in_packet.getPosition() - 1);
							else
								in_packet.setPosition(in_packet.getPosition() +	 1);
							// decimal 8 == backspace and decimal24 == delete from SOURCE
					}
				}
				
				InsertSimple(history_element, in_packet);
				
				// PREPARE ACK back to same IP
				// PREPARE BROADCAST to all IPs				
			}
			
			else
				System.out.println("Error: version numbers are corrupted \n");
		}		

	}

}
