package game.models;

import java.util.List;

import game.Main;

public class Word {
	private String word, lettersetCode, state, username;
	
	public Word(String word, String lettersetCode, String state, String username) {
		this.word = word;
		this.lettersetCode = lettersetCode;
		this.state = state;
		this.username = username;
	}
	
	public String getWord() {
		return word;
	}
	
	public String getLettersetCode() {
		return lettersetCode;
	}
	
	public String getState() {
		return state;
	}
	
	public String getTranslatedState() {
		switch (state) {
		case "pending":
			return "Wachtend";
		case "denied":
			return "Afgewezen";
		case "accepted":
			return "Geaccepteerd";
		default:
			return "";
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public static String addWord(String word, String lettersetCode, String username) {
		if (!word.equals("")) {
			if (Database.insertWord(word, lettersetCode, username)) {
				// Als je deze tekst aanpast moet je het ook in de SettingsController aanpassen
				return "Het woord is ingediend.";
			} else {
				return "Het woord bestaat al of is al afgewezen.";
			}
		} else {
			return "Vul een woord in.";
		}
	}
	
	public static List<Word> getPendingWords() {
		return Database.getPendingWords();
	}
	
	public static List<Word> getMyWords() {
		return Database.getMyWords(Main.user.getUsername());
	}
	
	public static boolean wordInWordList(String word) {
		return Database.wordInWordList(word);
	}
	
	public static boolean updateWord(String word, boolean accepted) {
		String state;
		if (accepted) {
			state = "accepted";
		} else {
			state = "denied";
		}
		
		return Database.updateWord(word, "NL", state);
	}
}
