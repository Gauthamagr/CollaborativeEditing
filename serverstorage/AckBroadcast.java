package serverstorage;

public class AckBroadcast {

	char character_pressed;
	int position;
	int server_version_number;
	int original_client_version_number; // set as 0 for Broadcast
	String ip_address; 
	
	
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
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

}
