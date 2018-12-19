package game.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.models.ScoreBar.Score;
import game.models.Turn.Letter;
import game.models.tiles.CharacterTile;
import game.models.tiles.FourLetterTile;
import game.models.tiles.FourWordTile;
import game.models.tiles.HandCharacter;
import game.models.tiles.MiddleTile;
import game.models.tiles.SixLetterTile;
import game.models.tiles.ThreeWordTile;
import game.models.tiles.Tile;
import game.models.tiles.TwoLetterTile;
import javafx.scene.canvas.GraphicsContext;

public class Board {

	private static Map<Character, Integer> charSetValues;
	private Tile[] tiles = new Tile[225];
	private static int boardSize, tileAmount;
	private ScoreBar scoreBar;
	private static int xOffset, yOffset;
	
	public Board(int xOffset, int yOffset, int boardSize) {
		Board.xOffset = xOffset;
		Board.yOffset = yOffset;
		charSetValues = new HashMap<Character, Integer>();
		initCharSet();
		Board.boardSize = boardSize;
		tileAmount = 15;
		initBoard();
		scoreBar = new ScoreBar();
	}
	
	public int getWidth() {
		return boardSize;
	}
	
	public static int getBoardSize() {
		return boardSize;
	}
	
	public static void setOffset(int xo, int yo) {
		xOffset = xo;
		yOffset = yo;
	}
	
	public void initBoard() {
		for (int y = 0; y < 15; y++){
            for (int x = 0; x < 15; x++){
            	tiles[y * tileAmount + x] = getTile(x, y);
            }
        }
	}
	
	private void initCharSet(){
		charSetValues.put('A', 2);
		charSetValues.put('B', 3);
		charSetValues.put('C', 6);
		charSetValues.put('D', 2);
		charSetValues.put('E', 1);
		charSetValues.put('F', 6);
		charSetValues.put('G', 4);
		charSetValues.put('H', 5);
		charSetValues.put('I', 2);
		charSetValues.put('J', 5);
		charSetValues.put('K', 4);
		charSetValues.put('L', 4);
		charSetValues.put('M', 3);
		charSetValues.put('N', 1);
		charSetValues.put('O', 2);
		charSetValues.put('P', 5);
		charSetValues.put('Q', 20);
		charSetValues.put('R', 2);
		charSetValues.put('S', 3);
		charSetValues.put('T', 2);
		charSetValues.put('U', 3);
		charSetValues.put('V', 5);
		charSetValues.put('W', 6);
		charSetValues.put('X', 8);
		charSetValues.put('Y', 9);
		charSetValues.put('Z', 6);
	}
	
	public List<Letter> getPlacedCharacters(Match match){
		List<Score> scores = checkForWord();
		updateScore(scores);
		if(scores != null) {
			List<Letter> chars = new ArrayList<Letter>();
			for(int i = 0; i < tiles.length; i++) {
				if(tiles[i] instanceof HandCharacter) {
					char c = ((HandCharacter)tiles[i]).getCharacter();
					List<Integer> ids = Match.getLetterIds(match.getGameId(), Match.getLastTurn(match.getGameId()), c);
					for(int j = 0; j < chars.size(); j++) {
						if(ids.contains(chars.get(j).getLetterId())) {
							ids.remove(Integer.valueOf("" + chars.get(j).getLetterId()));
						}
					}
					if(ids.size() > 0) {
						chars.add(new Letter(ids.get(0), c, tiles[i].getTileX(), tiles[i].getTileY()));
					}
				}
			}
			return chars;
		}else return null;
	}
	
	public int getScore() {
		if(scoreBar != null) return scoreBar.getTotalScore();
		return 0;
	}
	
	public static int getValueFromChar(char c) {
		if(charSetValues.containsKey(c)){
			return charSetValues.get(c);
		}else return 0;
	}
	
	public Tile getTileFromPos(int x, int y) {
		for(int i = 0; i < tiles.length; i++)
			if(tiles[i].contains(x, y)) return tiles[i];
		return null;
	}
	
	public boolean middleTileContainsTile() {
		return !(getTile(112) instanceof MiddleTile);
	}
	
	public boolean placeOnTile(int x, int y, Tile tile){
		for(int i = 0; i < tiles.length; i++){
			if(tile instanceof HandCharacter || tile instanceof CharacterTile) {
				if(!(tiles[i] instanceof CharacterTile || tiles[i] instanceof HandCharacter) && tiles[i].contains(x, y)){
					Tile t = tiles[i];
					tile.setX(t.getX());
					tile.setY(t.getY());
					if(tile instanceof HandCharacter) ((HandCharacter)tile).setSize(Board.getTileSize());
					tiles[i] = tile;
					return true;
				}
			}else {
				if(tiles[i].contains(x, y)) {
					tile.setX(tiles[i].getX());
					tile.setY(tiles[i].getY());
					tiles[i] = tile;
					return true;
				}
			}
		}
		return false;
	}
	
	public HandCharacter isPressed(int x, int y) {
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] instanceof HandCharacter && tiles[i].contains(x, y)) {
				return (HandCharacter) tiles[i];
			}
		}
		return null;
	}
	
	public static int getIdFromTile(int x, int y) {
		int tileSize = getTileSize();
		int id = tileAmount * (y / tileSize) + (x / tileSize);
		return id;
	}
	
	public void placeBackAllHandCharacters(Hand hand){
		int tileSize = Board.getTileSize();
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] instanceof HandCharacter) {
				int x = tiles[i].getX(), y = tiles[i].getY();
				HandCharacter hc = ((HandCharacter)tiles[i]);
				placeOnTile(x, y, Board.getTile(x / tileSize, y / tileSize));
				hc.setDefaultSize();
				hc.setDefaultPosition();
				hand.placeBack(hc);
			}
		}
		updateScore(null);
	}
	
	public void placeWord(List<Letter> word) {
		int tileSize = getTileSize();
		for(int i = 0; i < word.size(); i++) {
			int x = word.get(i).getX() * tileSize, y = word.get(i).getY() * tileSize;
			placeOnTile(x, y, new CharacterTile(x, y, word.get(i).getCharacter()));
		}
	}
	
	private int getAmountOfCharacterTiles() {
		int amount = 0;
		for(int i = 0; i < tiles.length; i++)
			if(tiles[i] instanceof CharacterTile) amount++;
		return amount;
	}
	
	private List<HandCharacter> getHandCharacters(){
		List<HandCharacter> chars = new ArrayList<HandCharacter>();
		for(int i = 0; i < tiles.length; i++)
			if(tiles[i] instanceof HandCharacter) chars.add(((HandCharacter)tiles[i]));
		return chars;
	}
	
	private boolean hcAllConnected(List<HandCharacter> chars, boolean horizontal) {
		HandCharacter first = null, last = null;
		for(int i = 0; i < chars.size(); i++) {
			if(first == null) first = chars.get(i);
			if(last == null) last = chars.get(i);
			
			if(horizontal) {
				if(chars.get(i).getX() < first.getX()) first = chars.get(i);
				if(chars.get(i).getX() > last.getX()) last = chars.get(i);
			}else {
				if(chars.get(i).getY() < first.getY()) first = chars.get(i);
				if(chars.get(i).getY() > last.getY()) last = chars.get(i);
			}
		}
		
		int tileSize = Board.getTileSize();
		if(horizontal) {
			for(int i = first.getX(); i <= last.getX(); i += tileSize) {
				if(!(tiles[getIdFromTile(i, first.getY())] instanceof CharacterTile)) 
					return false;
			}
			return true;
		}else {
			for(int i = first.getY(); i <= last.getY(); i += tileSize) {
				if(!(tiles[getIdFromTile(first.getX(), i)] instanceof CharacterTile)) 
					return false;
			}
			return true;
		}
	}
	
	public List<Score> checkForWord(){
		List<HandCharacter> hc = getHandCharacters();
		List<Score> scores = new ArrayList<Score>();
		
		if(hc.size() == 0) return null;
		if(hc.size() > 1) {
			boolean same = true, horizontal = true;
			for(int i = 1; i < hc.size(); i++) {
				if(hc.get(i).getX() != hc.get(i-1).getX()) {
					same = false;
					break;
				}
			}
			
			if(!same) {
				for(int i = 1; i < hc.size(); i++) {
					if(hc.get(i).getY() != hc.get(i-1).getY()) {
						return null;
					}
				}
			}else horizontal = false;
			
			if(!hcAllConnected(hc, horizontal)) return null;
		}
		
		for(int i = 0; i < hc.size(); i++) {
			Score scH = getScoreHorizontal(hc.get(i));
			Score scV = getScoreVertical(hc.get(i));
			
			if(Word.wordInWordList(scH.word)) {
				if(scores.size() > 0) {
					for(int j = 0; j < scores.size(); j++) {
						if(scores.get(j).equals(scH)) break;
						else if(j == scores.size() - 1) {
							scores.add(scH);
							break;
						}
					}
				}else scores.add(scH);
			}else if(scH.word.length() > 1) return null;

			if(Word.wordInWordList(scV.word)) {
				if(scores.size() > 0) {
					for(int j = 0; j < scores.size(); j++) {
						if(scores.get(j).equals(scV)) break;
						else if(j == scores.size() - 1) {
							scores.add(scV);
							break;
						}
					}
				}else scores.add(scV);
			}else if(scV.word.length() > 1) return null;
		}
		
		int length = 0;
		for(int i = 0; i < scores.size(); i++)
			length += scores.get(i).word.length();
		
		if(length <= hc.size() && getAmountOfCharacterTiles() > hc.size()) return null;
		return scores;
	}
	
	private Score getScoreHorizontal(HandCharacter hc) {
		String word = "";
		List<Tile> chars = new ArrayList<Tile>();
		int x = hc.getX();
		int y = hc.getY();
		int tileSize = getTileSize();
		int startX = x;
		int startY = y;
		while((x - tileSize) >= 0) {
			x -= tileSize;
			Tile tile = tiles[getIdFromTile(x, y)];
			if(tile instanceof CharacterTile) {
				char c = ((CharacterTile)tile).getCharacter();
				word = c + word;
				chars.add(0, tile);
				startX = x;
			}else break;
		}
		
		chars.add(hc);
		word += hc.getCharacter();

		x = hc.getX();
		y = hc.getY();
		int width = x - startX + tileSize;
		int height = tileSize;
		while((x + tileSize) < tileAmount * tileSize) {	
			x += tileSize;
			Tile tile = tiles[getIdFromTile(x, y)];
			if(tile instanceof CharacterTile) {
				char c = ((CharacterTile)tile).getCharacter();
				word = word + c;
				chars.add(tile);
				width += tileSize;
			}else break;
		}
		return scoreBar.new Score(startX + xOffset, startY + yOffset, width, height, getScoreFromWord(chars), word);
	}
	
	private Score getScoreVertical(HandCharacter hc) {
		String word = "";
		List<Tile> chars = new ArrayList<Tile>();
		int x = hc.getX();
		int y = hc.getY();
		int startX = x;
		int startY = y;
		int tileSize = getTileSize();
		while((y - tileSize) >= 0) {
			y -= tileSize;
			Tile tile = tiles[getIdFromTile(x, y)];
			if(tile instanceof CharacterTile) {
				char c = ((CharacterTile)tile).getCharacter();
				word = c + word;
				chars.add(0, tile);
				startY = y;
			}else break;
		}
		
		chars.add(hc);
		word += hc.getCharacter();
		
		x = hc.getX();
		y = hc.getY();
		int width = tileSize;
		int height = y - startY + tileSize;
		while((y + tileSize) < tileAmount * tileSize) {
			y += tileSize;
			Tile tile = tiles[getIdFromTile(x, y)];
			if(tile instanceof CharacterTile) {
				char c = ((CharacterTile)tile).getCharacter();
				word = word + c;
				chars.add(tile);
				height += tileSize;
			}else break;
		}

		return scoreBar.new Score(startX + xOffset, startY + yOffset, width, height, getScoreFromWord(chars), word);
	}
	
	private int getScoreFromWord(List<Tile> tiles) {
		int score = 0, wordMultiplier = 0, letterMultiplierScore = 0;
		int tileSize = getTileSize();
		for(Tile t : tiles) {
			CharacterTile tile = ((CharacterTile)t);
			int s = getValueFromChar(tile.getCharacter());
			score += s;
			if(t instanceof HandCharacter) {
				Tile boardTile = getTile(t.getX() / tileSize, t.getY() / tileSize);
				String multiplier = boardTile.getMultiplier();
				if(!multiplier.equals("")) {
					if(multiplier.charAt(1) == 'W') {
						wordMultiplier += Integer.valueOf("" + multiplier.charAt(0));
					}else if(multiplier.charAt(1) == 'L') {
						letterMultiplierScore += (Integer.valueOf("" + multiplier.charAt(0)) - 1) * s;
					}
				}
			}
		}
		
		score += letterMultiplierScore;
		if(wordMultiplier > 0) score = wordMultiplier * score;
		return score;
	}
	
	public void updateScore(List<Score> score) {
		scoreBar.updateScore(score);
	}
	
	public Tile getTile(int id) {
		if(id < 0 || id > tiles.length - 1) return null;
		return tiles[id];
	}
	
	public static int getX() {
		return xOffset;
	}
	
	public static int getY() {
		return yOffset;
	}
	
	public static Tile getTile(int x, int y){ //tiles without xOffset and yOffset
		int tileSize = getTileSize();
		if (y == 7 && x == 7) {
			return new MiddleTile(x * tileSize, y * tileSize);
		} else if (((y == 1 || y == 13) && (x == 2 || x == 6 || x == 8 || x == 12)) || ((y == 3 || y == 11) && (x == 7)) || ((y == 6 || y == 8) && (x == 6 || x == 8)) || y == 7 && (x == 2 || x == 12)) {
			return new TwoLetterTile(x * tileSize, y * tileSize);
		} else if (((y == 0 || y == 14) && x == 7) || ((y == 2 || y == 12) && (x == 3 || x == 11)) || ((y == 3 || y == 11) && (x == 5 || x == 9)) || ((y == 4 || y == 10) && (x == 1 || x == 13)) || ((y == 5 || y == 9) && x == 7) || (y == 7 && (x == 5 || x == 9))) {
			return new FourLetterTile(x * tileSize, y * tileSize);
		} else if (((y == 0 || y == 14) && (x == 0 || x == 14)) || ((y == 5 || y == 9) && (x == 4 || x == 10)) || ((y == 6 || y == 8) && (x == 1 || x == 13))) {
			return new SixLetterTile(x * tileSize, y * tileSize);
		} else if (((y == 0 || y == 14) && (x == 4 || x == 10)) || ((y == 2 || y == 12) && (x == 0 || x == 14)) || ((y == 4 || y == 10) && (x == 3 || x == 11))) {
			return new ThreeWordTile(x * tileSize, y * tileSize);
		} else if (y == 7 && (x == 0 || x == 14)) {
			return new FourWordTile(x * tileSize, y * tileSize);
		} else {
			return new Tile(x * tileSize, y * tileSize);
		}
	}
	
	public static int getTileSize(){
		return boardSize / tileAmount;
	}
	
	public void render(GraphicsContext gc){
		for(int i = 0; i < tiles.length; i++)
			tiles[i].render(gc);
		scoreBar.render(gc);
	}
	
}
