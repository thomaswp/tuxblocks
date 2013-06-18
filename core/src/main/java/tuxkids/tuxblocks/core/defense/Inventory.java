package tuxkids.tuxblocks.core.defense;

import java.net.PasswordAuthentication;
import java.util.Dictionary;

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
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.tower.BigShooter;
import tuxkids.tuxblocks.core.defense.tower.HorizontalWall;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.VerticalWall;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Inventory extends PlayNObject {
	
	private final static int ITEM_SPRITE_MARGIN = 5;
	
	private Grid grid;
	private GroupLayer groupLayer;
	private int width, height;
	private int counts[];
	private ImageLayer countSprites[];
	private Button itemButtons[];
	private TextFormat textFormat;
	private Button buttonPlus;
	private DefenseScreen screen;
	
	private final static Tower[] towers = new Tower[] {
		new PeaShooter(),
		new BigShooter(),
		new VerticalWall(),
		new HorizontalWall(),
	};
	
	public GroupLayer layer() {
		return groupLayer;
	}
	
	private int getItemSpriteSize() {
		return (int) (width * 0.45f);
	}
	
	private int getItemCaptionHeight() {
		return (int)(textFormat.font.size() + ITEM_SPRITE_MARGIN);
	}
	
	private float getItemSpriteX(int index) {
		int j = index % 2;
		float spriteWidth = getItemSpriteSize() + ITEM_SPRITE_MARGIN * 2;
		return width / 2 + (j - 0.5f) * spriteWidth;
	}
	
	private float getItemSpriteY(int index) {
		int i = index / 2;
		int rows = (towers.length + 1) / 2;
		float spriteHeight = getItemSpriteSize() + ITEM_SPRITE_MARGIN * 2 + getItemCaptionHeight();
		return height / 2 + (i - (rows - 1) * 0.5f) * spriteHeight;
	}
	
	public Inventory(DefenseScreen screen, Grid grid, int width, int height) {
		groupLayer = graphics().createGroupLayer();
		this.screen = screen;
		this.grid = grid;
		this.width = width;
		this.height = height;
		counts = new int[towers.length];
		
		textFormat = new TextFormat().withFont(
				graphics().createFont("Arial", Style.BOLD, getItemSpriteSize() / 7));
		//createBackgroundSprite();
		createSelectionSprites();
		createCountSprites();
		createPlusButton();
		
		addItem(0, 3);
		addItem(2, 1);
		addItem(3, 1);
	}
	
	private void createPlusButton() {
		Image image = assets().getImage("images/plus.png");
		int size = this.width / 4;
		buttonPlus = new Button(image, size, size, true);
		buttonPlus.setPosition(size * 0.6f, size * 0.6f);
		buttonPlus.setTint(Colors.blend(Colors.RED, Colors.BLACK, 0.9f), 
		Colors.blend(Colors.RED, Colors.BLACK, 0.7f));
		groupLayer.add(buttonPlus.layer());
		
		buttonPlus.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				screen.pushSelectScreen();
			}
		});
	}
	
	private void addItem(int index, int count) {
		counts[index] += count;
		refreshCountSprite(index);
	}
	
	private void createCountSprites() {
		countSprites = new ImageLayer[counts.length];
		for (int i = 0; i < countSprites.length; i++) {
			countSprites[i] = graphics().createImageLayer();
			countSprites[i].setTranslation(getItemSpriteX(i) - getItemSpriteSize() / 2 + ITEM_SPRITE_MARGIN, 
					getItemSpriteY(i) - getItemSpriteSize() / 2 - getItemCaptionHeight() / 2 + ITEM_SPRITE_MARGIN);
			groupLayer.add(countSprites[i]);
			refreshCountSprite(i);
		}
	}
	
	private void refreshCountSprite(int index) {
		String text = "x" + counts[index];
		TextLayout layout = graphics().layoutText(text, textFormat);
		CanvasImage image = graphics().createImage(layout.width(), layout.height());
		image.canvas().setFillColor(Colors.BLACK);
		image.canvas().fillText(layout, 0, 0);
		countSprites[index].setImage(image);
		itemButtons[index].setEnabled(counts[index] > 0);
	}
	
	private void createSelectionSprites() {
		int spriteSize = getItemSpriteSize();
		int textHeight = getItemCaptionHeight();
		int rad = (int)(spriteSize * 0.05f);
		float padding = spriteSize * 0.1f;
		float cellSize = (spriteSize - padding * 2) / 3;
		float strokeWidth = 5;
		
		itemButtons = new Button[towers.length];
		for (int index = 0; index < itemButtons.length; index++) {
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
					image.height() - textHeight + ITEM_SPRITE_MARGIN / 2);
			
			final Button button = new Button(image, image.width(), image.height(), false);
			float x = getItemSpriteX(index);
			float y = getItemSpriteY(index);
			button.setPosition(x, y);
			button.setTint(Colors.WHITE, Color.rgb(230, 230, 230));
			
			final int fi = index;
			button.layer().addListener(new Listener() {
				@Override
				public void onPointerStart(Event event) {
					if (!button.enabled()) return;
					grid.startPlacement(tower.copy());
				}
				
				@Override
				public void onPointerEnd(Event event) {
					if (!button.enabled()) return;
					if (grid.endPlacement(event.x(), event.y())) {
						counts[fi]--;
						refreshCountSprite(fi);
					}
				}
				
				@Override
				public void onPointerDrag(Event event) {
					if (!button.enabled()) return;
					grid.updatePlacement(event.x(), event.y());
					
				}
				
				@Override
				public void onPointerCancel(Event event) { }
			});
			
			groupLayer.add(button.layer());
			itemButtons[index] = button;
		}
	}
	
	private void createBackgroundSprite() {
		Image image = CanvasUtils.createRect(width, height, Colors.LIGHT_GRAY, 1, Colors.BLACK);
		ImageLayer layer = graphics().createImageLayer(image);
		groupLayer.add(layer);
	}

}
