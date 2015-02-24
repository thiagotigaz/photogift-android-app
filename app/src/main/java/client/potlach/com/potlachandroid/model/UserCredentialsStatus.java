package client.potlach.com.potlachandroid.model;

public class UserCredentialsStatus {

	public enum UserCredentialsState {
		BOTH_AVAILABLE, USERNAME_AVAILABLE, EMAIL_AVAILABLE, NONE_AVAILABLE
	}

	private UserCredentialsState state;

	public UserCredentialsStatus(UserCredentialsState state) {
		super();
		this.state = state;
	}

	public UserCredentialsState getState() {
		return state;
	}

	public void setState(UserCredentialsState state) {
		this.state = state;
	}

}
