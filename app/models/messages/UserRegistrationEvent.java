package models.messages;

public class UserRegistrationEvent {

	private String userName;

	public UserRegistrationEvent(String userName) {
		this.userName = userName;
	}
	
	
	public String userName() {
		return userName;
	}
}
