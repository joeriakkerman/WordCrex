package game.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	private int gameId;
	private String username, moment, message;
	
	public Message(int gameId, String username, String moment, String message) {
		this.gameId = gameId;
		this.username = username;
		this.message = message;
		parseDate(moment);
	}

	public int getGameId() {
		return gameId;
	}

	public String getUsername() {
		return username;
	}

	public String getMoment() {
		return moment;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder(message);

		int i = 0;
		while ((i = sb.indexOf(" ", i + 40)) != -1) {
		    sb.replace(i, i + 1, "\n");
		}
		
		return sb.toString();
	}
	
	private void parseDate(String moment) {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(moment);
		    this.moment = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
		} catch (ParseException e) {
		    e.printStackTrace();
		}
	}
}
