package game.models;

import java.util.List;
import game.Main;
import game.models.Database;

public class User {
	
	private String username;
	private boolean player, admin, moderator, observer;
	
	public User(String username) {
		setUsername(username);
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isPlayer() {
		return player;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public boolean isModerator() {
		return moderator;
	}
	
	public boolean isObserver() {
		return observer;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setRoles(boolean player, boolean admin, boolean moderator, boolean observer) {
		this.player = player;
		this.admin = admin;
		this.moderator = moderator;
		this.observer = observer;
	}
	
	public static String register(String username, String password, String confirmPassword) {
		if(username.length() >= 5 && username.length() <= 25 && password.length() >= 5 && password.length() <= 25) {
			if(checkString(username) && checkString(password)) {
				if(password.equals(confirmPassword)) {
					if(Database.register(username, password)) {
						// Als je deze tekst aanpast moet je het ook in de RegisterController aanpassen
						return "U bent geregistreerd. Ga terug om in te loggen.";
					} else {
						return "Gebruikersnaam bestaat al.";
					}
				}else {
					return "Wachtwoorden zijn niet gelijk.";
				}
			}else {
				return "Gebruikersnaam en wachtwoord kunnen alleen letters en cijfers bevatten.";
			}
		}else {
			return "Gebruikersnaam en wachtwoord moeten 5 tot 25 karakters lang zijn.";
		}
	}
	
	public static void login(String username, String password) {
		UserRow userRow = Database.login(username, password);
		Main.user = new User(userRow.username);
		Main.user.setRoles(userRow.player, userRow.admin, userRow.moderator, userRow.observer);
	}
	
	private static boolean checkString(String s) {
		char[] arr = s.toCharArray();
		for(int i = 0; i < arr.length; i++) {
			if(!((arr[i] >= 48 && arr[i] <= 57) || (arr[i] >= 65 && arr[i] <= 90) || (arr[i] >= 97 && arr[i] <= 122))) return false;
		}
		return true;
	}
	
	public static String changePassword(String username, String newPassword, String confirmPassword) {
		if(newPassword.length() >= 5 && newPassword.length() <= 25) {
			if(checkString(newPassword)) {
				if(newPassword.equals(confirmPassword)) {
					// Als je deze tekst aanpast moet je het ook in de SettingsController aanpassen
					if (Database.updatePassword(username, newPassword)) return "Wachtwoord is gewijzigd.";
					else return "Er is iets misgegaan met de database.";
				}else {
					return "Wachtwoorden zijn niet gelijk.";
				}
			}else {
				return "Het wachtwoord kan alleen letters en cijfers bevatten.";
			}
		}else {
			return "Het wachtwoord moet 5 tot 25 karacters lang zijn.";
		}
	}
	
	public boolean addRole(String role) {
		return Database.insertRole(getUsername(), role);
	}
	
	public String removeRole(String role) {
		if (!isPlayer() && !isAdmin() && !isModerator() && !isObserver()) {
			return "Een gebruiker moet minimaal 1 rol hebben.";
		} else {
			if (Database.deleteRole(getUsername(), role)) {
				return "";
			} else {
				return "Er is iets misgegaan.";
			}
		}
	}
	
	public static List<String> getAllUsers() {
		return Database.getAllUsers(Main.user.getUsername());
	}
	
	public static List<String> getSearchedUsers(String searchText) {
		return Database.searchUsers(Main.user.getUsername(), searchText);
	}
	
	public static UserRow getUserFromUsername(String username) {
		return Database.getUser(username);
	}
	
	public static List<String> getRoles() {
		return Database.getRoles();
	}
	
	public void setRole(String role, boolean hasRole) {
		switch (role) {
		case "player":
			player = hasRole;
			break;
		case "administrator":
			admin = hasRole;
			break;
		case "moderator":
			moderator = hasRole;
			break;
		case "observer":
			observer = hasRole;
			break;
		default:
			break;
		}
	}
}
