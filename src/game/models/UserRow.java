package game.models;

public class UserRow {
	public String username;
	public boolean player, admin, moderator, observer;
	
	public UserRow(String username, boolean player, boolean admin, boolean moderator, boolean observer) {
		this.username = username;
		this.player = player;
		this.admin = admin;
		this.moderator = moderator;
		this.observer = observer;
	}
}
