package tuxkids.tuxblocks.core.defense;

import java.net.PasswordAuthentication;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Button;
import tuxkids.tuxblocks.core.Button.OnDragListener;
import tuxkids.tuxblocks.core.Button.OnPressedListener;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.tower.BigShooter;
import tuxkids.tuxblocks.core.defense.tower.HorizontalWall;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.VerticalWall;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Inventory extends PlayNObject {
	
	private Grid grid;
	private GroupLayer groupLayer;
	private int width, height;
	
	private final static Tower[] towers = new Tower[] {
		new PeaShooter(),
		new BigShooter(),
		new VerticalWall(),
		new HorizontalWall(),
	};
	
	public GroupLayer layer() {
		return groupLayer;
	}
	
	public Inventory(Grid grid, int width, int height) {
		groupLayer = graphics().createGroupLayer();
		this.grid = grid;
		this.width = width;
		this.height = height;
		createBackgroundSprite();
		createSelectionSprites();
	}
	
	private void createSelectionSprites() {
		int spriteSize = (int)(width * 0.45f);
		TextFormat textFormat = new TextFormat().withFont(
				graphics().createFont("Arial", Style.BOLD, spriteSize / 8));
		int rad = (int)(spriteSize * 0.05f);
		float padding = spriteSize * 0.1f;
		int textHeight = (int)(textFormat.font.size() + padding);
		int margin = 5;
		int spriteSizeWidthMargin = spriteSize + margin * 2;
		int spriteSizeWidthMarginAndText = spriteSizeWidthMargin + textHeight;
		int rows = (towers.length + 1) / 2;
		float cellSize = (spriteSize - padding * 2) / 3;
		float strokeWidth = 5;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < 2; j++) {
				int index = i * 2 + j;
				if (index >= towers.length) break;
				final Tower tower = towers[index];
				
				CanvasImage image = graphics().createImage(spriteSize, spriteSize + textHeight);
				image.canvas().setFillColor(Colors.WHITE);
				image.canvas().fillRoundRect(0, 0, spriteSize, spriteSize, rad);
				image.canvas().setStrokeColor(Colors.BLACK);
				image.canvas().setStrokeWidth(strokeWidth);
				image.canvas().strokeRoundRect(strokeWidth / 2 - 1, strokeWidth / 2 - 1, 
						spriteSize - strokeWidth + 2, 
						spriteSize  - strokeWidth + 2, rad);
				
				Image towerImage = tower.createImage(cellSize * tower.cols(), cellSize * tower.rows(), Colors.RED);
				image.canvas().drawImage(towerImage, (spriteSize - towerImage.width()) / 2, 
						(spriteSize - towerImage.height()) / 2);
				
				TextLayout layout = graphics().layoutText(tower.name(), textFormat);
				image.canvas().setFillColor(Colors.BLACK);
				image.canvas().fillText(layout, (image.width() - layout.width()) / 2, 
						image.height() - cellSize + padding);
				
				Button button = new Button(image, image.width(), image.height(), false);
				float x = width / 2 + (j - 0.5f) * spriteSizeWidthMargin;
				float y = height / 2 + (i - (rows - 1) * 0.5f) * spriteSizeWidthMarginAndText;
				button.setPosition(x, y);
				button.setTint(Colors.WHITE, Color.rgb(230, 230, 230));
				
				button.layer().addListener(new Listener() {
					@Override
					public void onPointerStart(Event event) {
						grid.startPlacement(tower.copy());
					}
					
					@Override
					public void onPointerEnd(Event event) {
						grid.endPlacement(event.x(), event.y());
						
					}
					
					@Override
					public void onPointerDrag(Event event) {
						grid.updatePlacement(event.x(), event.y());
						
					}
					
					@Override
					public void onPointerCancel(Event event) { }
				});
				
				groupLayer.add(button.layer());
			}
		}
	}
	
	private void createBackgroundSprite() {
		Image image = CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.BLACK);
		ImageLayer layer = graphics().createImageLayer(image);
		groupLayer.add(layer);
	}

}
