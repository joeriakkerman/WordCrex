package game.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.models.tiles.HandCharacter;
import javafx.scene.canvas.GraphicsContext;

public class Hand {
	
	private List<HandCharacter> chars;
	
	public Hand(int x, int y, int width, int height, char[] hand){
		chars = new ArrayList<HandCharacter>();
		int size = width / 8;
		int xOffset = (width - hand.length * size) / 2;
		if(size > height) {
			size = height;
			xOffset = (width - (height * hand.length)) / 2;
		}
		for(int i = 0; i < hand.length; i++)
			chars.add(new HandCharacter((int)(x + xOffset + ((i + 0.5) * size)), y + height - size, size, hand[i]));
	}
	
	public void render(GraphicsContext gc){
		for(HandCharacter character : chars)
			if(character != null) character.renderInHand(gc);
	}
	
	public HandCharacter isPressed(int x, int y){
		for(HandCharacter character : chars)
			if(character != null && character.contains(x, y)) return character;
		return null;
	}
	
	public void deleteCharacterFromHand(HandCharacter hc){
		for(int i = 0; i < chars.size(); i++)
			if(chars.get(i) == hc) chars.set(i, null);
	}
	
	public boolean containsTile(HandCharacter hc) {
		for(HandCharacter character : chars)
			if(character == hc) return true;
		return false;
	}
	
	public void placeBack(HandCharacter hc) {
		for(int i = 0; i < chars.size(); i++) {
			if(chars.get(i) == null) {
				chars.set(i, hc);
				break;
			}
		}
	}
	
	public void shuffle() {
		List<Character> c = new ArrayList<Character>();
		for(int i = 0; i < chars.size(); i++)
			if(chars.get(i) != null) c.add(chars.get(i).getCharacter());
		Collections.shuffle(c);
		int id = 0;
		for(int i = 0; i < chars.size(); i++) {
			if(chars.get(i) != null) {
				chars.get(i).setCharacter(c.get(id));
				if(id < c.size()-1) id++;
			}
		}
	}
}
