package game.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import game.models.Turn.Letter;
import game.models.Turn.TurnPlayer;

public class Database {
	
	private static final String JDBC_URL = "jdbc:mysql://databases.aii.avans.nl/";
	private static final String JDBC_DB = "2018_vsoprj2_wordcrex_efgh";
	private static final String JDBC_USER = "42in02vtsoh";
	private static final String JDBC_PASSWORD = "hoofdfunctie";
	private static Connection connection;
	
	public static boolean connect() {
		try {
			connection = DriverManager.getConnection(
					JDBC_URL + JDBC_DB + "?useSSL=false",
					JDBC_USER,
					JDBC_PASSWORD);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean register(String username, String password) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO account VALUES ('" + username + "', '" + password + "');");
			if(result > 0) result = statement.executeUpdate("INSERT INTO accountrole VALUES ('" + username + "', (SELECT role FROM role WHERE role LIKE 'player'));");
			else {
				statement.close();
				return false;
			}
			statement.close();
			if(result > 0) return true;
			return false;
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean addTurn(int tableId, int gameId, String username, int bonus, int score, String turnActionType) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO turnplayer" + tableId + " (game_id, turn_id, username_player" + tableId + ", bonus, score, turnaction_type) " + 
					"VALUES (" + gameId + ", (SELECT MAX(turn_id) FROM turn WHERE game_id = " + gameId + "), '" + username  + "', " + bonus + ", " + score + ", '" + turnActionType + "');");
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getAmountOfLettersLeft(int gameId) {
		int letterId = 0;
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT COUNT(l.letter_id) AS amount FROM letter AS l LEFT JOIN turnboardletter AS tbl ON l.letter_id = tbl.letter_id AND l.game_id = tbl.game_id WHERE l.game_id = " + gameId + " AND tbl.letter_id IS NULL ORDER BY RAND() LIMIT 1");
			
			if(rs.next()) letterId = rs.getInt("amount");
			
			rs.close();
			statement.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return letterId;
	}
	
	public static List<Integer> getLetterIdFromSymbol(int gameId, int turnId, char symbol) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT handletter.game_id, turn_id, handletter.letter_id FROM handletter INNER JOIN letter ON handletter.letter_id = letter.letter_id AND handletter.game_id = letter.game_id WHERE handletter.game_id = " + gameId + " AND turn_id = " + turnId + " AND letter.symbol = '" + symbol + "';");
			
			List<Integer> letters = new ArrayList<Integer>();
			while(rs.next()) {
				letters.add(rs.getInt("letter_id"));
			}
			
			rs.close();
			statement.close();
			return letters;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean addBoardLetters(List<Letter> tiles, int gameId, String username, int tableId) {
		if(tiles.size() == 0) return false;
		try {
			Statement statement = connection.createStatement();
			String query = "INSERT INTO boardplayer" + tableId + " (game_id, username, turn_id, letter_id, tile_x, tile_y) VALUES";
			for(int i = 0; i < tiles.size(); i++) {
				query += " (" + gameId + ", '" + username + "', (SELECT MAX(turn_id) FROM turn WHERE game_id = " + gameId + "), " + tiles.get(i).getLetterId() + ", " + (tiles.get(i).getX()+1) + ", " + (tiles.get(i).getY()+1) + ")";
				if(i + 1 < tiles.size()) query += ",";
				else query += ";";
			}
			int result = statement.executeUpdate(query);
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<Match> getAllMatches(){
		try {
			List<Match> matches = new ArrayList<Match>();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM game;");

			while(rs.next()) {
				int gameId = rs.getInt("game_id");
				matches.add(new Match(gameId, rs.getString("game_state"), rs.getString("letterset_code"), rs.getString("username_player1"), rs.getString("username_player2"), rs.getString("answer_player2"), rs.getString("username_winner"), getTurns(gameId)));
			}
			
			rs.close();
			statement.close();
			return matches;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Match> getMatchesByState(String username, String state){
		try {
			List<Match> matches = new ArrayList<Match>();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM game WHERE (username_player1 = '" + username + "' OR username_player2 = '" + username + "') AND game_state = '" + state + "';");

			while(rs.next()) {
				int gameId = rs.getInt("game_id");
				matches.add(new Match(gameId, rs.getString("game_state"), rs.getString("letterset_code"), rs.getString("username_player1"), rs.getString("username_player2"), rs.getString("answer_player2"), rs.getString("username_winner"), getTurns(gameId)));
			}
			
			rs.close();
			statement.close();
			return matches;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Match> getInactiveMatches(String username) {
		try {
			List<Match> matches = new ArrayList<Match>();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM game WHERE (username_player1 = '" + username + "' OR username_player2 = '" + username + "') AND game_state != 'playing';");

			while(rs.next()) {
				int gameId = rs.getInt("game_id");
				matches.add(new Match(gameId, rs.getString("game_state"), rs.getString("letterset_code"), rs.getString("username_player1"), rs.getString("username_player2"), rs.getString("answer_player2"), rs.getString("username_winner"), getTurns(gameId)));
			}
			
			rs.close();
			statement.close();
			return matches;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Integer getMatchesByPlayer(String player1, String player2){
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS amountMatches FROM game WHERE (username_player1 = '" + player1 + "' OR username_player2 = '" + player1 + "') " + 
					"AND (username_player1 = '" + player2 + "' OR username_player2 = '" + player2 + "') " + 
					"AND (game_state = 'playing' OR answer_player2 = 'unknown');");

			rs.next();
			Integer amountMatches = rs.getInt("amountMatches");
			
			rs.close();
			statement.close();
			return amountMatches;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Match> getInvitedMatches(String username){
		try {
			List<Match> matches = new ArrayList<Match>();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM game WHERE username_player2 = '" + username + "' AND answer_player2 = 'unknown';");

			while(rs.next()) {
				int gameId = rs.getInt("game_id");
				matches.add(new Match(gameId, rs.getString("game_state"), rs.getString("letterset_code"), rs.getString("username_player1"), rs.getString("username_player2"), rs.getString("answer_player2"), rs.getString("username_winner"), getTurns(gameId)));
			}
			
			rs.close();
			statement.close();
			return matches;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static boolean addTurnBoardletters(int gameId, int turnId, int tableId) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO turnboardletter (letter_id, game_id, turn_id, tile_x, tile_y) SELECT letter_id, game_id, turn_id, tile_x, tile_y FROM boardplayer" + tableId + " WHERE game_id = " + gameId + " AND turn_id = " + turnId + ";");
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean updateBonus(int gameId, int idFirstTurnedPlayer) {
		int score1 = 0, score2 = 0;
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT score FROM turnplayer1 AS tp1 WHERE tp1.turn_id = (SELECT MAX(turn_id) FROM turn WHERE game_id = " + gameId + ") AND game_id = " + gameId + ";");
			if(rs.next()) score1 = rs.getInt("score");
			
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT score FROM turnplayer2 AS tp2 WHERE tp2.turn_id = (SELECT MAX(turn_id) FROM turn WHERE game_id = " + gameId + ") AND game_id = " + gameId + ";");
			if(rs.next()) score2 = rs.getInt("score");
			
			if(score1 == score2) {
				statement = connection.createStatement();
				int result  = statement.executeUpdate("UPDATE turnplayer" + idFirstTurnedPlayer + " SET bonus = 5 WHERE game_id = " + gameId + " AND turn_id = (SELECT MAX(turn_id) FROM turn WHERE game_id = " + gameId + ")");
				rs.close();
				statement.close();
				
				if(result > 0) {
					return addTurnBoardletters(gameId, getLastTurn(gameId), idFirstTurnedPlayer);
				}else {
					return false;
				}
			}else if(score1 > score2) {
				return addTurnBoardletters(gameId, getLastTurn(gameId), 1);
			}else {
				return addTurnBoardletters(gameId, getLastTurn(gameId), 2);
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<Turn> getTurns(int gameId){
		List<Turn> turns = new ArrayList<Turn>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT turn.game_id, " + 
					"turn.turn_id as turnid, " + 
					"tp1.bonus as bonus1, " + 
					"tp1.score as score1, " + 
					"tp1.turnaction_type as turntype1, " + 
					"tp2.bonus as bonus2, " + 
					"tp2.score as score2, " + 
					"tp2.turnaction_type as turntype2 " + 
					"FROM turn " + 
					"LEFT JOIN turnplayer1 as tp1 on turn.turn_id = tp1.turn_id and turn.game_id = tp1.game_id " + 
					"LEFT JOIN turnplayer2 as tp2 on turn.turn_id = tp2.turn_id and turn.game_id = tp2.game_id " + 
					"WHERE turn.game_id = " + gameId +";");
			
			while(rs.next()) {
				int turnId = rs.getInt("turnid");
				Turn turn = new Turn(turnId, new TurnPlayer(rs.getString("turntype1"), getLetters(gameId, turnId, "boardplayer1"), rs.getInt("bonus1"), rs.getInt("score1")), new TurnPlayer(rs.getString("turntype2"), getLetters(gameId, turnId, "boardplayer2"), rs.getInt("bonus2"), rs.getInt("score2")));
				turn.setBoardLetters(getLetters(gameId, turnId, "turnboardletter"));
				turns.add(turn);
			}
			
			rs.close();
			statement.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return turns;
	}
	
	private static List<Letter> getLetters(int gameId, int turnId, String table){
		List<Turn.Letter> letters = new ArrayList<Turn.Letter>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT letter.symbol AS symbol, bp.tile_x AS tilex, bp.tile_y AS tiley, bp.letter_id FROM " + table + " AS bp INNER JOIN letter ON letter.letter_id = bp.letter_id AND letter.game_id = bp.game_id WHERE bp.game_id = " + gameId + " AND bp.turn_id = " + turnId + ";");
			
			while(rs.next()) {
				letters.add(new Turn.Letter(rs.getInt("letter_id"), rs.getString("symbol").charAt(0), (rs.getInt("tilex")-1), (rs.getInt("tiley")-1)));
			}
			
			rs.close();
			statement.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return letters;
	}
	
	public static UserRow login(String username, String password) {
		boolean correctCredentials = false;
		
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT username FROM account WHERE BINARY username = '" + username + "' AND BINARY password = '" + password + "' LIMIT 1;");
			UserRow userRow = null;
			
			if(result.next() && result.getString("username").equals(username)) {
				correctCredentials = true;
				result = statement.executeQuery("SELECT role FROM accountrole WHERE BINARY username = '" + username + "';");
				
				boolean player = false, admin = false, mod = false, obs = false;
				while(result.next()) {
					String s = result.getString("role");
					if(s.equals("player")) player = true;
					else if(s.equals("administrator")) admin = true;
					else if(s.equals("observer")) obs = true;
					else if(s.equals("moderator")) mod = true;
				}
				userRow = new UserRow(username, player, admin, mod, obs);
			}
			
			result.close();
			statement.close();
			
			if(correctCredentials) return userRow;
			else return new UserRow("Geen rechten", false, false, false, false);
		} catch (SQLException e) {
			e.printStackTrace();
			return new UserRow("Geen rechten", false, false, false, false);
		}
	}
	
	public static boolean wordInWordList(String word) {
		boolean containsWord = false;
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT word FROM dictionary WHERE word = '" + word + "' AND state = 'accepted';");
			if (result.last() && result.getRow() > 0) containsWord = true;
			result.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return containsWord;
	}
	
	public static List<Integer> getRandomLetterFromLetterSet(int gameId) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT l.letter_id FROM letter AS l LEFT JOIN turnboardletter AS tbl ON l.letter_id = tbl.letter_id AND l.game_id = tbl.game_id WHERE l.game_id = " + gameId + " AND tbl.letter_id IS NULL ORDER BY RAND()");
			
			while(rs.next()) list.add(rs.getInt("letter_id"));
			
			rs.close();
			statement.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static boolean addLetterSet(int gameId, String letterSetCode) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO letter (`letter_id`,`game_id`,`symbol_letterset_code`,`symbol`) VALUES " + 
					"(001, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(002, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(003, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(004, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(005, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(006, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(007, " + gameId + ", '" + letterSetCode + "', 'A')," + 
					"(008, " + gameId + ", '" + letterSetCode + "', 'B')," + 
					"(009, " + gameId + ", '" + letterSetCode + "', 'B')," + 
					"(010, " + gameId + ", '" + letterSetCode + "', 'C')," + 
					"(011, " + gameId + ", '" + letterSetCode + "', 'C')," + 
					"(012, " + gameId + ", '" + letterSetCode + "', 'D')," + 
					"(013, " + gameId + ", '" + letterSetCode + "', 'D')," + 
					"(014, " + gameId + ", '" + letterSetCode + "', 'D')," + 
					"(015, " + gameId + ", '" + letterSetCode + "', 'D')," + 
					"(016, " + gameId + ", '" + letterSetCode + "', 'D')," + 
					"(017, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(018, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(019, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(020, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(021, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(022, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(023, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(024, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(025, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(026, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(027, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(028, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(029, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(030, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(031, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(032, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(033, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(034, " + gameId + ", '" + letterSetCode + "', 'E')," + 
					"(035, " + gameId + ", '" + letterSetCode + "', 'F')," + 
					"(036, " + gameId + ", '" + letterSetCode + "', 'F')," + 
					"(037, " + gameId + ", '" + letterSetCode + "', 'G')," + 
					"(038, " + gameId + ", '" + letterSetCode + "', 'G')," + 
					"(039, " + gameId + ", '" + letterSetCode + "', 'G')," + 
					"(040, " + gameId + ", '" + letterSetCode + "', 'H')," + 
					"(041, " + gameId + ", '" + letterSetCode + "', 'H')," + 
					"(042, " + gameId + ", '" + letterSetCode + "', 'I')," + 
					"(043, " + gameId + ", '" + letterSetCode + "', 'I')," + 
					"(044, " + gameId + ", '" + letterSetCode + "', 'I')," + 
					"(045, " + gameId + ", '" + letterSetCode + "', 'I')," + 
					"(046, " + gameId + ", '" + letterSetCode + "', 'J')," + 
					"(047, " + gameId + ", '" + letterSetCode + "', 'J')," + 
					"(048, " + gameId + ", '" + letterSetCode + "', 'K')," + 
					"(049, " + gameId + ", '" + letterSetCode + "', 'K')," + 
					"(050, " + gameId + ", '" + letterSetCode + "', 'K')," + 
					"(051, " + gameId + ", '" + letterSetCode + "', 'L')," + 
					"(052, " + gameId + ", '" + letterSetCode + "', 'L')," + 
					"(053, " + gameId + ", '" + letterSetCode + "', 'L')," + 
					"(054, " + gameId + ", '" + letterSetCode + "', 'M')," + 
					"(055, " + gameId + ", '" + letterSetCode + "', 'M')," + 
					"(056, " + gameId + ", '" + letterSetCode + "', 'M')," + 
					"(057, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(058, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(059, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(060, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(061, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(062, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(063, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(064, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(065, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(066, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(067, " + gameId + ", '" + letterSetCode + "', 'N')," + 
					"(068, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(069, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(070, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(071, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(072, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(073, " + gameId + ", '" + letterSetCode + "', 'O')," + 
					"(074, " + gameId + ", '" + letterSetCode + "', 'P')," + 
					"(075, " + gameId + ", '" + letterSetCode + "', 'P')," + 
					"(076, " + gameId + ", '" + letterSetCode + "', 'Q')," + 
					"(077, " + gameId + ", '" + letterSetCode + "', 'R')," + 
					"(078, " + gameId + ", '" + letterSetCode + "', 'R')," + 
					"(079, " + gameId + ", '" + letterSetCode + "', 'R')," + 
					"(080, " + gameId + ", '" + letterSetCode + "', 'R')," + 
					"(081, " + gameId + ", '" + letterSetCode + "', 'R')," + 
					"(082, " + gameId + ", '" + letterSetCode + "', 'S')," + 
					"(083, " + gameId + ", '" + letterSetCode + "', 'S')," + 
					"(084, " + gameId + ", '" + letterSetCode + "', 'S')," + 
					"(085, " + gameId + ", '" + letterSetCode + "', 'S')," + 
					"(086, " + gameId + ", '" + letterSetCode + "', 'S')," + 
					"(087, " + gameId + ", '" + letterSetCode + "', 'T')," + 
					"(088, " + gameId + ", '" + letterSetCode + "', 'T')," + 
					"(089, " + gameId + ", '" + letterSetCode + "', 'T')," + 
					"(090, " + gameId + ", '" + letterSetCode + "', 'T')," + 
					"(091, " + gameId + ", '" + letterSetCode + "', 'T')," + 
					"(092, " + gameId + ", '" + letterSetCode + "', 'U')," + 
					"(093, " + gameId + ", '" + letterSetCode + "', 'U')," + 
					"(094, " + gameId + ", '" + letterSetCode + "', 'U')," + 
					"(095, " + gameId + ", '" + letterSetCode + "', 'V')," + 
					"(096, " + gameId + ", '" + letterSetCode + "', 'V')," + 
					"(097, " + gameId + ", '" + letterSetCode + "', 'W')," + 
					"(098, " + gameId + ", '" + letterSetCode + "', 'W')," + 
					"(099, " + gameId + ", '" + letterSetCode + "', 'X')," + 
					"(100, " + gameId + ", '" + letterSetCode + "', 'Y')," + 
					"(101, " + gameId + ", '" + letterSetCode + "', 'Z')," + 
					"(102, " + gameId + ", '" + letterSetCode + "', 'Z');");
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean createMatch(String playerOne, String playerTwo) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO game (" + 
					"game_state, letterset_code, username_player1," + 
					"username_player2, answer_player2)" + 
					"VALUES (" + 
					"'request', 'NL', '" + playerOne + "'," + 
					"'" + playerTwo + "', 'unknown');");
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<String> getGameStates() {
		List<String> states = new ArrayList<String>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT state FROM gamestate;");
			while(result.next()) {
				states.add(result.getString("state"));
			}
			statement.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return states;
	}
	
	public static boolean updateGameState(int gameId, String state, String winner) {
		try {
			Statement statement = connection.createStatement();
			String query = "";
			if(winner == null || winner.equals("")) query = "UPDATE game SET game_state='" + state + "' WHERE game_id='" + gameId + "';";
			else query = "UPDATE game SET game_state='" + state + "', username_winner='" + winner + "' WHERE game_id='" + gameId + "';";
			int result = statement.executeUpdate(query);
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean addTurn(int gameId) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO turn (game_id, turn_id) VALUES (" + gameId + ", (SELECT IFNULL(MAX(t2.turn_id), 0)+1 FROM turn AS t2 WHERE t2.game_id=" + gameId + "));");
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getLastTurn(int gameId) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT MAX(turn_id) AS lastTurnId FROM turn WHERE game_id = " + gameId +";");
			rs.next();
			int result = rs.getInt("lastTurnId");
			rs.close();
			statement.close();
			return result;
		}catch(SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static boolean updateTurnactionType(int tableId, int gameId, String turnactionType) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("UPDATE turnplayer" + tableId + " SET turnaction_type='" + turnactionType + "' WHERE game_id=" + gameId + " AND turn_id=(SELECT MAX(turn_id) FROM (SELECT * FROM turnplayer" + tableId + ") AS turns WHERE game_id=" + gameId + ");");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static char getSymbolFrom(int gameId, int letterId) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT symbol FROM letter WHERE letter_id=" + letterId + " AND game_id=" + gameId + ";");
			rs.next();
			char result = rs.getString("symbol").charAt(0);
			rs.close();
			statement.close();
			return result;
		}catch(SQLException e) {
			e.printStackTrace();
			return ' ';
		}
	}
	
	public static char[] getHandLetters(int gameId, int turnId) {
		String[] letters = new String[0];
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT inhoud FROM hand WHERE game_id=" + gameId + " AND turn_id=" + turnId + ";");
			
			if(rs.next()) letters = rs.getString("inhoud").split(",");
			
			rs.close();
			statement.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		char[] cLetters = new char[letters.length];
		for(int i = 0; i < cLetters.length; i++)
			cLetters[i] = letters[i].charAt(0);
		return cLetters;
	}
	
	public static boolean addHandLetters(int gameId, int turnId, List<Integer> letters) {
		try {
			Statement statement = connection.createStatement();
			String query = "INSERT INTO handletter (game_id, turn_id, letter_id) VALUES ";
			for(int i = 0; i < letters.size(); i++) {
				query += "(" + gameId + ", " + turnId + ", " + letters.get(i) + ")";
				if(i < letters.size()-1) query += ", ";
			}
			query += ";";
			int result = statement.executeUpdate(query);
			statement.close();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean updateAnswerFromInvite(int gameId, String answer) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("UPDATE game SET answer_player2='" + answer + "' WHERE game_id=" + gameId + ";");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean updatePassword(String username, String newPassword) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("UPDATE account SET password='" + newPassword + "' WHERE username='" + username + "';");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean insertRole(String username, String role) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO accountrole (username, role) VALUES ('" + username + "', '" + role + "');");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteRole(String username, String role) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("DELETE FROM accountrole WHERE username='" + username + "' AND role='" + role + "';");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean insertWord(String word, String lettersetCode, String username) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO dictionary (word, letterset_code, state, username) VALUES ('" + word + "', '" + lettersetCode + "', (SELECT state FROM wordstate WHERE state='pending'), '" + username + "');");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean updateWord(String word, String lettersetCode, String state) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("UPDATE dictionary SET state='" + state + "' WHERE word='" + word + "' AND letterset_code='" + lettersetCode + "';");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public static List<Word> getPendingWords() {
		try {
			List<Word> words = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM dictionary WHERE state='pending';");
			while(result.next())
				words.add(new Word(result.getString("word"), result.getString("letterset_code"), result.getString("state"), result.getString("username")));
			
			result.close();
			statement.close();
			return words;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Word> getMyWords(String username) {
		try {
			List<Word> words = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM dictionary WHERE username='" + username + "' ORDER BY state DESC, word ASC LIMIT 50;");
			while(result.next())
				words.add(new Word(result.getString("word"), result.getString("letterset_code"), result.getString("state"), result.getString("username")));
			
			result.close();
			statement.close();
			return words;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> getAllUsers(String username) {
		try {
			List<String> usernames = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM account WHERE username!='" + username + "';");
			while(result.next())
				usernames.add(result.getString("username"));
			
			result.close();
			statement.close();
			return usernames;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static UserRow getUser(String username) {
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT role FROM accountrole WHERE BINARY username = '" + username + "';");
			boolean player = false, admin = false, mod = false, obs = false;
			
			while(result.next()) {
				String role = result.getString("role");
				if(role.equals("player")) player = true;
				else if(role.equals("administrator")) admin = true;
				else if(role.equals("observer")) obs = true;
				else if(role.equals("moderator")) mod = true;
			}
			
			UserRow user = new UserRow(username, player, admin, mod, obs);
			result.close();
			statement.close();
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getRoles() {
		try {
			List<String> roles = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT role FROM role;");
			while(result.next())
				roles.add(result.getString("role"));
			
			result.close();
			statement.close();
			return roles;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> searchUsers(String username, String searchText) {
		try {
			List<String> usernames = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM account WHERE username!='" + username + "' AND username LIKE '" + searchText + "%';");
			while(result.next())
				usernames.add(result.getString("username"));
			
			result.close();
			statement.close();
			return usernames;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean insertChatMessage(int gameId, String username, String message) {
		try {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate("INSERT INTO chatline (username, game_id, moment , message) VALUES ('" + username + "', '" + gameId + "', CURRENT_TIMESTAMP, '" + message + "');");
			statement.close();
			return result == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<Message> getMessages(int gameId) {
		try {
			List<Message> messages = new ArrayList<>();
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM chatline WHERE game_id ='" + gameId + "' ORDER BY moment;");
			while(result.next()) {
				messages.add(new Message(result.getInt("game_id"),result.getString("username"), result.getString("moment"), result.getString("message")));
			}
			
			result.close();
			statement.close();
			return messages;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
