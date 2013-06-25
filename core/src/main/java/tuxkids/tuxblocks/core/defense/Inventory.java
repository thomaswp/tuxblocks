package tuxkids.tuxblocks.core.defense;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

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
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.MenuSprite;
import tuxkids.tuxblocks.core.Button.OnDragListener;
import tuxkids.tuxblocks.core.Button.OnPressedListener;
import tuxkids.tuxblocks.core.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.tower.BigShooter;
import tuxkids.tuxblocks.core.defense.tower.HorizontalWall;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.VerticalWall;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.utils.CanvasUtils;

public class Inventory extends PlayNObject {
	
	private final static int ITEM_SPRITE_MARGIN = 5;
	private final static int COLS = 2;
	
	private Grid grid;
	private GroupLayer groupLayer;
	private int width, height;
	private ImageLayer countSprites[];
	private Button itemButtons[];
	private TextFormat textFormat;
	private DefenseScreen screen;
	
	public GroupLayer layer() {
		return groupLayer;
	}
	
	private int getItemSpriteSize() {
		return (int) ((width - ITEM_SPRITE_MARGIN * 2) / COLS * 0.9f);
	}
	
	private int getItemCaptionHeight() {
		return (int)(textFormat.font.size() + ITEM_SPRITE_MARGIN);
	}
	
	private float getItemSpriteX(int index) {
		int j = index % COLS;
		float spriteWidth = getItemSpriteSize() + ITEM_SPRITE_MARGIN * 2;
		return width / 2 + (j - (COLS - 1) * 0.5f) * spriteWidth;
	}
	
	private float getItemSpriteY(int index) {
		int i = index / COLS;
		int rows = (Tower.towers().length - 1) / COLS + 1;
		float spriteHeight = getItemSpriteSize() + ITEM_SPRITE_MARGIN * 2 + getItemCaptionHeight();
		return height / 2 + (i - (rows - 1) * 0.5f) * spriteHeight;
	}
	
	private int[] towerCounts() {
		return screen.state().towerCounts();
	}
	
	public Inventory(DefenseScreen screen, Grid grid, int width, int height) {
		groupLayer = graphics().createGroupLayer();
		this.screen = screen;
		this.grid = grid;
		this.width = width;
		this.height = height;
		
		textFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.BOLD, getItemSpriteSize() / 7));
//		createBackgroundSprite();
		createSelectionSprites();
		createCountSprites();
		
		for (int i = 0; i < 4; i++) {
			addItem(i, 10);	
		}
	}
	
	private void addItem(int index, int count) {
		towerCounts()[index] += count;
		refreshCountSprite(index);
	}
	
	private void createCountSprites() {
		countSprites = new ImageLayer[towerCounts().length];
		for (int i = 0; i < countSprites.length; i++) {
			countSprites[i] = graphics().createImageLayer();
			countSprites[i].setTranslation(getItemSpriteX(i) - getItemSpriteSize() / 2 + ITEM_SPRITE_MARGIN, 
					getItemSpriteY(i) - getItemSpriteSize() / 2 - getItemCaptionHeight() / 2 + ITEM_SPRITE_MARGIN);
			groupLayer.add(countSprites[i]);
			refreshCountSprite(i);
		}
	}
	
	public void refreshCountSprites() {
		for (int i = 0; i < countSprites.length; i++) {
			refreshCountSprite(i);
		}
	}
	
	private void refreshCountSprite(int index) {
		String text = "x" + towerCounts()[index];
		TextLayout layout = graphics().layoutText(text, textFormat);
		CanvasImage image = graphics().createImage(layout.width(), layout.height());
		image.canvas().setFillColor(Colors.BLACK);
		image.canvas().fillText(layout, 0, 0);
		countSprites[index].setImage(image);
		itemButtons[index].setEnabled(towerCounts()[index] > 0);
	}
	
	private void createSelectionSprites() {
		int spriteSize = getItemSpriteSize();
		int textHeight = getItemCaptionHeight();
		int rad = (int)(spriteSize * 0.05f);
		float padding = spriteSize * 0.1f;
		float cellSize = (spriteSize - padding * 2) / 3;
		float strokeWidth = 5;
		
		itemButtons = new Button[Tower.towers().length];
		for (int index = 0; index < itemButtons.length; index++) {
			final Tower tower = Tower.getTowerById(index);
			
			CanvasImage image = graphics().createImage(spriteSize, spriteSize + textHeight);
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().fillRoundRect(0, 0, spriteSize, spriteSize, rad);
			image.canvas().setStrokeColor(Colors.DARK_GRAY);
			image.canvas().setStrokeWidth(strokeWidth);
			image.canvas().strokeRoundRect(strokeWidth / 2 - 1, strokeWidth / 2 - 1, 
					spriteSize - strokeWidth + 2, 
					spriteSize  - strokeWidth + 2, rad);
			
			Image towerImage = tower.createImage(cellSize, grid.towerColor());
			image.canvas().drawImage(towerImage, (spriteSize - towerImage.width()) / 2, 
					(spriteSize - towerImage.height()) / 2);
			
			TextLayout layout = graphics().layoutText(tower.name(), textFormat);
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().fillText(layout, (image.width() - layout.width()) / 2, 
					image.height() - textHeight + ITEM_SPRITE_MARGIN / 2);
			
			final Button button = new Button(image, image.width(), image.height(), false);
			float x = getItemSpriteX(index);
			float y = getItemSpriteY(index);
			button.setPosition(x, y);
			button.setTint(Colors.WHITE, Color.rgb(230, 230, 230));
			
			final int fi = index;
			button.imageLayer().addListener(new Listener() {
				@Override
				public void onPointerStart(Event event) {
					if (!button.enabled()) return;
					grid.startPlacement(tower.copy());
				}
				
				@Override
				public void onPointerEnd(Event event) {
					if (!button.enabled()) return;
					if (grid.endPlacement(event.x(), event.y())) {
						towerCounts()[fi]--;
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
			
			groupLayer.add(button.addableLayer());
			itemButtons[index] = button;
		}
	}
	
	private void createBackgroundSprite() {
		Image image = CanvasUtils.createRect(width, height, Color.rgb(200, 125, 125), 1, Colors.DARK_GRAY);
		ImageLayer layer = graphics().createImageLayer(image);
		layer.setAlpha(MenuSprite.DEFAULT_ALPHA);
		groupLayer.add(layer);
	}

}
