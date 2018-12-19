package game.models.tiles;

import game.models.Board;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class MiddleTile extends Tile {

	public MiddleTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0xed008c");
		multiplier = "3W";
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(paint);
		double tileSize = (double)Board.getTileSize();
		int x = this.x + Board.getX();
		int y = this.y + Board.getY();
		gc.fillRoundRect((double)x + margin, (double)y + margin, tileSize - margin*2, tileSize - margin*2, arcSize, arcSize);
		gc.drawImage(new Image("images/logo.png"), (double)x + margin, (double)y + margin, tileSize - margin*2, tileSize - margin*2);
	}

}
