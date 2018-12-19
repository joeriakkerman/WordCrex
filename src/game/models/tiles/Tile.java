package game.models.tiles;

import game.models.Board;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Tile {
	
	protected int x, y;
	protected int arcSize, margin;
	protected String multiplier;
	protected Paint paint;
	
	public Tile(int x, int y){
		this.x = x;
		this.y = y;
		margin = 1;
		arcSize = 5;
		paint = Paint.valueOf("0x221f58");
		multiplier = "";
	}
	
	public void render(GraphicsContext gc){
		gc.setFill(paint);
		double tileSize = (double)Board.getTileSize();
		int x = this.x + Board.getX();
		int y = this.y + Board.getY();
		gc.fillRoundRect((double)x + margin, (double)y + margin, tileSize - margin*2, tileSize - margin*2, arcSize, arcSize);
		gc.setFill(Paint.valueOf("0xffffff"));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Font.font("Open Sans", FontWeight.BOLD, 18));
		gc.fillText(multiplier, (double)(x + 0.5 * tileSize + margin), (double)(y + 0.5 * tileSize + margin), tileSize - margin*2);
	}
	
	public String getMultiplier() {
		return multiplier;
	}
	
	public int getTileX() {
		return x / Board.getTileSize();
	}
	
	public int getTileY() {
		return y / Board.getTileSize();
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public boolean contains(int x, int y){
		double tileSize = (double)Board.getTileSize();
		if(y >= this.y && y < this.y + tileSize) {
			if(x >= this.x && x < this.x + tileSize) {
				return true;
			}
		}
		return false;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

}
