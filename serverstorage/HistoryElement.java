package serverstorage;

public class HistoryElement {
	String ip_address;
	int version_number;
	char character_pressed;
	int position;
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public int getVersion_number() {
		return version_number;
	}
	public void setVersion_number(int version_number) {
		this.version_number = version_number;
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
	
	
}
