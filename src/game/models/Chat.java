package game.models;

import java.util.List;

import game.Main;

public class Chat {
	private int gameId;
	
	public Chat(int gameId) {
		this.gameId = gameId;
	}
	
	public String createMessage(String message) {
		if (!message.equals("")) {
			String replacedMessage = message.replace("'", "''");
			String secondReplacedMessage = replacedMessage.replace("\\", "\\\\");
			
			if (Database.insertChatMessage(this.gameId, Main.user.getUsername(), secondReplacedMessage)) {
				return null;
			} else {
				return "Er is iets misgegaan.";
			}
		} else {
			return "Vul een bericht in.";
		}
	}
	public List<Message> getMessages() {
		return Database.getMessages(gameId);
	}
}
