package game.models.tiles;

import javafx.scene.paint.Paint;

public class FourLetterTile extends Tile {

	public FourLetterTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0x1f4397");
		multiplier = "4L";
	}

}
