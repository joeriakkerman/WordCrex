package game.models;

import java.util.ArrayList;
import java.util.List;

public class Turn {
	
	private int turnId;
	private TurnPlayer playerOne, playerTwo;
	private List<Letter> boardLetters; //turnboardletters - word with highest score which has been chosen and placed on the board
	
	public Turn(int turnId, TurnPlayer playerOne, TurnPlayer playerTwo) {
		this.turnId = turnId;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}
	
	public static List<Letter> orderList(List<Letter> letters) {
		if(letters.size() <= 0) return letters;
		List<Letter> order = new ArrayList<Letter>();
		for(int i = 0; i < letters.size(); i++) {
			if(order.size() > 0) {
				
				for(int j = 0; j < order.size(); j++) {
					if(letters.get(i).tileX < order.get(j).tileX) {
						order.add(j, letters.get(i));
						break;
					}else if(letters.get(i).tileY < order.get(j).tileY) {
						order.add(j, letters.get(i));
						break;
					}else if(j == order.size() - 1) {
						order.add(letters.get(i));
						break;
					}
				}
			}else {
				order.add(letters.get(i));
			}
		}
		return order;
	}
	
	private static String convertListToString(List<Letter> letters) {
		letters = orderList(letters);
		
		String word = "";
		for(int i = 0; i < letters.size(); i++)
			word += letters.get(i).letter;
		return word;
	}
	
	public int getPlayerIdFromWinner() {
		String finalWord = getFinalWord();
		if(convertListToString(getTurnPlayerOne().letters).equals(finalWord)) {
			return 1;
		}else {
			return 2;
		}
	}
	
	public String getFinalWord() {
		return convertListToString(boardLetters);
	}
	
	public void setBoardLetters(List<Letter> boardLetters) {
		this.boardLetters = boardLetters;
	}
	
	public List<Letter> getBoardLetters() {
		return boardLetters;
	}
	
	public int getTurnId() {
		return turnId;
	}
	
	public TurnPlayer getTurnPlayerOne() {
		return playerOne;
	}

	public TurnPlayer getTurnPlayerTwo() {
		return playerTwo;
	}
	
	public static class TurnPlayer {
		
		private int bonus, score;
		private String turnType;
		private List<Letter> letters;
		
		public TurnPlayer(String turnType, List<Letter> letters, int bonus, int score) {
			this.turnType = turnType;
			this.letters = letters;
			this.bonus = bonus;
			this.score = score;
		}
		
		public int getBonus() {
			return bonus;
		}
		
		public int getScore() {
			return score;
		}
		
		public String getWord() {
			return convertListToString(letters);
		}
		
		public boolean hasPlayed() {
			if(turnType == null) return false;
			return turnType.equals("play");
		}
		
		public boolean hasResigned() {
			if(turnType == null) return false;
			return turnType.equals("resign");
		}
		
		public boolean hasPassed() {
			if(turnType == null) return false;
			return turnType.equals("pass");
		}
		
		public List<Letter> getLetters(){
			return letters;
		}
	}
	
	public static class Letter {
		
		private int letterId;
		private char letter;
		private int tileX, tileY;
		
		public Letter(int letterId, char letter, int tileX, int tileY) {
			this.letterId = letterId;
			this.letter = letter;
			this.tileX = tileX;
			this.tileY = tileY;
		}
		
		public static char getSymbolFromLetterId(int gameId, int letterId) {//if returned ' ' -> error or no letter with this letterId and gameId
			return Database.getSymbolFrom(gameId, letterId);
		}
		
		public int getLetterId() {
			return letterId;
		}
		
		public int getX() {
			return tileX;
		}
		
		public int getY() {
			return tileY;
		}

		public char getCharacter() {
			return letter;
		}
		
	}

}
