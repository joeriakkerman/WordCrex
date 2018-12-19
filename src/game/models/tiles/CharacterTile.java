package game.models.tiles;

import game.models.Board;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CharacterTile extends Tile {

	protected char character;
	
	public CharacterTile(int x, int y, char character) {
		super(x, y);
		this.character = character;
		paint = Paint.valueOf("0xffffff");
	}
	
	public char getCharacter(){
		return character;
	}
	
	public void setCharacter(char character){
		this.character = character;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(paint);
		double tileSize = (double)Board.getTileSize();
		int x = this.x + Board.getX();
		int y = this.y + Board.getY();
		gc.fillRoundRect((double)x + margin, (double)y + margin, tileSize - margin*2, tileSize - margin*2, arcSize, arcSize);
		gc.setFill(Paint.valueOf("0x000000"));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Font.font("Open Sans", FontWeight.BOLD, 30));
		gc.fillText("" + character, (double)(x + 0.4 * tileSize + margin), (double)(y + 0.4 * tileSize + margin), (tileSize * 0.4) - margin*2);
		gc.setFont(Font.font("Open Sans", FontWeight.BOLD, 12));
		gc.fillText("" + Board.getValueFromChar(character), (double)(x + 0.7 * tileSize + margin), (double)(y + 0.7 * tileSize + margin), (tileSize * 0.2) - margin*2);
	}

}
