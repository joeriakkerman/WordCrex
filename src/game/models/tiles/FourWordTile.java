package game.models.tiles;

import javafx.scene.paint.Paint;

public class FourWordTile extends Tile {

	public FourWordTile(int x, int y) {
		super(x, y);
		paint = Paint.valueOf("0xf16625");
		multiplier = "4W";
	}

}
