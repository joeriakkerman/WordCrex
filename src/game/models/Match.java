package game.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Main;
import game.models.ScoreBar.Score;
import game.models.Turn.Letter;
import game.models.tiles.HandCharacter;

public class Match {
	
	private int gameId;
	private List<Turn> turns;
	private String gameState, lettersetCode, playerOne, playerTwo, answerPlayerTwo, winner;
	
	public Match(int gameId, String gameState, String lettersetCode, String playerOne, String playerTwo, String answerPlayerTwo, String winner, List<Turn> turns) {
		this.gameId = gameId;
		this.gameState = gameState;
		this.lettersetCode = lettersetCode;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.answerPlayerTwo = answerPlayerTwo;
		this.winner = winner;
		this.turns = turns;
	}
	
	public boolean isMyTurn() {
		if(turns.size() == 0) return false;
		Turn lastTurn = turns.get(turns.size() - 1);
		if(playerOne.equals(Main.user.getUsername())) {
			return (lastTurn.getTurnPlayerOne().getLetters().size() == 0 && !lastTurn.getTurnPlayerOne().hasPassed());
		}else {
			return (lastTurn.getTurnPlayerTwo().getLetters().size() == 0 && !lastTurn.getTurnPlayerTwo().hasPassed());
		}
	}
	
	public String getWordFromTurn(int turnId, int playerId) {
		int xo = Board.getX();
		int yo = Board.getY();
		Board board = new Board(0, 0, Board.getBoardSize());
		for(int i = 0; i < turnId; i++) {
			board.placeWord(turns.get(i).getBoardLetters());
		}
		List<Letter> letters = new ArrayList<Letter>();
		if(playerId == 1) {
			letters = turns.get(turnId).getTurnPlayerOne().getLetters();
		}else {
			letters = turns.get(turnId).getTurnPlayerTwo().getLetters();
		}
		
		int tileSize = Board.getTileSize();
		for(int i = 0; i < letters.size(); i++) {
			int x = letters.get(i).getX() * tileSize;
			int y = letters.get(i).getY() * tileSize;
			board.placeOnTile(x, y, new HandCharacter(x, y, tileSize, letters.get(i).getCharacter()));
		}
		List<Score> scores = board.checkForWord();
		Board.setOffset(xo, yo);
		if(scores != null && scores.size() > 0) {
			Score score = scores.get(0);
			return score.word;
		}
		return "";
	}
	
	public List<List<Letter>> getBoardLetters(){
		List<List<Letter>> boardLetters = new ArrayList<List<Letter>>();
		for(int i = 0; i < turns.size(); i++)
			boardLetters.add(turns.get(i).getBoardLetters());
		return boardLetters;
	}
	
	public int getTotalScorePlayerOne() {
		int totalScore = 0;
		
		for (Turn turn : turns) {
			totalScore += turn.getTurnPlayerOne().getScore();
			totalScore += turn.getTurnPlayerOne().getBonus();
		}
		
		return totalScore;
	}
	
	public int getTotalScorePlayerTwo() {
		int totalScore = 0;
		
		for (Turn turn : turns) {
			totalScore += turn.getTurnPlayerTwo().getScore();
			totalScore += turn.getTurnPlayerTwo().getBonus();
		}
		
		return totalScore;
	}
	
	public List<Turn> getTurns(){
		return turns;
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public String getPlayerOne() {
		return playerOne;
	}
	
	public String getPlayerTwo() {
		return playerTwo;
	}
	
	public String getLettersetCode() {
		return lettersetCode;
	}
	
	public String getGameState() {
		return gameState;
	}
	
	public String getAnswerPlayerTwo() {
		return answerPlayerTwo;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public static List<String> getGameStates(){
		return Database.getGameStates();
	}
	
	public String getOpponentsUsername() {
		if(!playerOne.equals(Main.user.getUsername())) return playerOne;
		else return playerTwo;
	}
	
	public int getPlayerIdInGame() {
		if(playerOne.equals(Main.user.getUsername())) return 1;
		else if(playerTwo.equals(Main.user.getUsername())) return 2;
		return -1;
	}
	
	public boolean addPlayerTurn(int score) {
		return Database.addTurn(getPlayerIdInGame(), gameId, Main.user.getUsername(), 0, score, "play");
	}
	
	public boolean pass() {
		return Database.addTurn(getPlayerIdInGame(), gameId, Main.user.getUsername(), 0, 0, "pass");
	}
	
	public boolean addBoardLetters(List<Letter> wordCharacters, int player) {
		return Database.addBoardLetters(wordCharacters, gameId, Main.user.getUsername(), getPlayerIdInGame());
	}
	
	public static List<Match> getAllMatches(){
		return Database.getAllMatches();
	}
	
	public static boolean createMatch(String invitedPlayer) {
		return Database.createMatch(Main.user.getUsername(), invitedPlayer);
	}
	
	public static List<Match> getPlayingMatches() {
		return Database.getMatchesByState(Main.user.getUsername(), "playing");
	}
	
	public static List<Match> getRequestMatches(){
		return Database.getMatchesByState(Main.user.getUsername(), "request");
	}
	
	public static List<Match> getInactiveMatches(){
		return Database.getInactiveMatches(Main.user.getUsername());
	}
	
	public static List<Match> getInvitedMatches() {
		return Database.getInvitedMatches(Main.user.getUsername());
	}
	
	public static boolean hasMatchWith(String opponent) {
		int result = Database.getMatchesByPlayer(Main.user.getUsername(), opponent);
		
		return result > 0;
	}
	
	public static boolean addLetterSet(int gameId, String letterSetCode) {
		return Database.addLetterSet(gameId, letterSetCode);
	}
	
	public static void addHandLetters(int gameId, int turnId, List<Integer> letters) {
		Database.addHandLetters(gameId, turnId, letters);
	}
	
	public boolean updateAnswerPlayerTwo(int gameId, String answer) {
		return Database.updateAnswerFromInvite(gameId, answer);
	}
	
	public static List<Integer> getRandomLetterFromLetterSet(int gameId) {
		return Database.getRandomLetterFromLetterSet(gameId);
	}
	
	public static char[] getHandLetters(int gameId, int turnId) {
		return Database.getHandLetters(gameId, turnId);
	}
	
	public static boolean addNewTurn(int gameId) {
		return Database.addTurn(gameId);
	}
	
	public static int getLastTurn(int gameId) {
		return Database.getLastTurn(gameId);
	}
	
	public static List<Integer> getLetterIds(int gameId, int turnId, char c){
		return Database.getLetterIdFromSymbol(gameId, turnId, c);
	}
	
	public static List<Integer> initNewHand(int gameId) {
		List<Integer> letters = new ArrayList<Integer>();
		if(Match.addNewTurn(gameId)) {
			List<Integer> randomLetters = getRandomLetterFromLetterSet(gameId);
			for(int i = 0; i < 7; i++) {
				if(randomLetters.size() > 0) {
					int random = new Random().nextInt(randomLetters.size());
					int letterId = randomLetters.get(random);
					letters.add(letterId);
					randomLetters.remove(random);
				}else break;
			}
			Match.addHandLetters(gameId, Match.getLastTurn(gameId), letters);
			return letters;
		}else return null;
	}
	
	public static boolean updateGameState(int gameId, String gameState, String winner) {
		return Database.updateGameState(gameId, gameState, winner);
	}
	
	public static void isMatchAccepted() {
		for (Match match : Match.getRequestMatches()) {
			if(match.getAnswerPlayerTwo().equals("accepted") && match.getPlayerOne().equals(Main.user.getUsername())) {
				int gameId = match.getGameId();
				Match.updateGameState(gameId, "playing", null);
				Match.addLetterSet(gameId, match.getLettersetCode());
				initNewHand(gameId);
			}
		}
	}
	
	public int getAmountOfLettersLeft() {
		return Database.getAmountOfLettersLeft(gameId);
	}
	
	public boolean updateBonus() {
		int id = getPlayerIdInGame();
		if(id == 1) id = 2;
		else id = 1;
		return Database.updateBonus(gameId, id);
	}
	
	public boolean resign() {
		if (turns.size() > 0) {
			boolean hasturnplayer = false;
			int playerId = getPlayerIdInGame();
			if(playerId == 1) hasturnplayer = !((turns.get(turns.size() - 1).getTurnPlayerOne().getLetters().size() == 0) && !turns.get(turns.size() - 1).getTurnPlayerOne().hasPassed());
			else if(playerId == 2) hasturnplayer = !((turns.get(turns.size() - 1).getTurnPlayerTwo().getLetters().size() == 0) && !turns.get(turns.size() - 1).getTurnPlayerTwo().hasPassed());
			else return false;
			
			if(hasturnplayer) {
				if (Database.updateTurnactionType(getPlayerIdInGame(), gameId, "resign")) {
					if (Match.updateGameState(gameId, "resigned", null)) {
						return true;
					}
				}
			}else {
				if(Database.addTurn(playerId, gameId, Main.user.getUsername(), 0, 0, "resign")){
					if (Database.updateGameState(gameId, "resigned", null)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
