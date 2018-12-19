package game.models.tiles;

import javafx.scene.paint.Paint;

public class TwoLetterTile extends Tile {

	public TwoLetterTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0x25a8e0");
		multiplier = "2L";
	}

}
