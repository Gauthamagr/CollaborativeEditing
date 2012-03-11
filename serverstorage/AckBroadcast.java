package serverstorage;

public class AckBroadcast {


	char character_pressed;
	int position;
	int server_version_number;
	int original_client_version_number; 
	//String ip_address;
	int thread_id;
	
	public AckBroadcast(char character_pressed, int position,
			int server_version_number, int original_client_version_number,
			int thread_id) {
		super();
		this.character_pressed = character_pressed;
		this.position = position;
		this.server_version_number = server_version_number;
		this.original_client_version_number = original_client_version_number; // is 0 to indicate it is a Broadcast, has value for ACK
		this.thread_id = thread_id;
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
	public int getServer_version_number() {
		return server_version_number;
	}
	public void setServer_version_number(int server_version_number) {
		this.server_version_number = server_version_number;
	}
	public int getOriginal_client_version_number() {
		return original_client_version_number;
	}
	public void setOriginal_client_version_number(int original_client_version_number) {
		this.original_client_version_number = original_client_version_number;
	}
	public int getThread_id() {
		return thread_id;
	}
	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

}
