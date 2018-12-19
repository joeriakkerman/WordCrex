package game.models.tiles;

import game.models.Board;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class HandCharacter extends CharacterTile {
	
	private final int defX, defY, defSize;
	private int size;
	private boolean renderInHand;
	
	public HandCharacter(int x, int y, int size, char c){
		super(x, y, c);
		margin = 1;
		arcSize = 10;
		defX = x;
		defY = y;
		defSize = size;
		this.size = size;
		renderInHand = true;
	}
	
	public void renderInHand(GraphicsContext gc){
		renderInHand = true;
		gc.setFill(Paint.valueOf("0xffffff"));
		gc.fillRoundRect((double)x + margin, (double)y + margin, size - margin*2, size - margin*2, arcSize, arcSize);
		gc.setFill(Paint.valueOf("0x000000"));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Font.font("Open Sans", FontWeight.BOLD, 30));
		gc.fillText("" + character, (double)(x + size * 0.4 + margin), (double)(y + size * 0.4 + margin), (size * 0.4) - margin*2);
		gc.setFont(Font.font("Open Sans", FontWeight.BOLD, 12));
		gc.fillText("" + Board.getValueFromChar(character), (double)(x + size * 0.7 + margin), (double)(y + size * 0.7 + margin), (size * 0.2) - margin*2);
	}
	
	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);
		renderInHand = false;
	}
	
	public void setDefaultPosition(){
		x = defX;
		y = defY;
	}
	
	public void setDefaultSize() {
		size = defSize;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setCharacter(char c){
		this.character = c;
	}
	
	public int getSize(){
		return size;
	}
	
	public char getCharacter(){
		return character;
	}
	
	public boolean contains(int x, int y){
		if(!renderInHand) return super.contains(x, y);
		if(y >= this.y && y < this.y + size)
			if(x >= this.x && x < this.x + size)
				 return true;
		return false;
	}
	
}
