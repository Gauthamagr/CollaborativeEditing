package serverstorage;

public class Message {
	char character_pressed;
	int position;
	int buffer_version_number;
	int client_version_number;
	int client_id;
	
	public Message(){
	
	}
	
	public Message(char character_pressed, int position, int buffer_version_number, int client_version_number, int client_id) {
		
		this.character_pressed = character_pressed;
		this.position = position;
		this.buffer_version_number = buffer_version_number;
		this.client_version_number = client_version_number;
		this.client_id = client_id;
	}
	
	// copy constructor	
	public Message(Message m){
		this(m.getCharacter_pressed(),m.getPosition(), m.getBuffer_version_number(),
				m.client_version_number, m.client_id);
	}
	
	public char getCharacter_pressed() {
		return character_pressed;
	}
	public void setCharacter_pressed(char character_pressed) {
		this.character_pressed = character_pressed;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getBuffer_version_number() {
		return buffer_version_number;
	}
	public void setBuffer_version_number(int buffer_version_number) {
		this.buffer_version_number = buffer_version_number;
	}
	public int getClient_version_number() {
		return client_version_number;
	}
	public void setClient_version_number(int client_version_number) {
		this.client_version_number = client_version_number;
	}
	public int getClient_id() {
		return client_id;
	}
	public void setClientId(int client_id) {
		this.client_id = client_id;
	}

	
	
}
