package tuxkids.tuxblocks.core.defense;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState.InventoryChangedListener;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;

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
		int wSize = (int) ((width - ITEM_SPRITE_MARGIN * 2) / COLS * 0.9f);
		int hSize = (int) ((height - ITEM_SPRITE_MARGIN * 2) / rows() * 0.9f) - getItemCaptionHeight();
		return Math.min(wSize, hSize);
	}
	
	private int getItemCaptionHeight() {
		return textFormat == null ? 0 : (int)(textFormat.font.size() + ITEM_SPRITE_MARGIN);
	}
	
	private float getItemSpriteX(int index) {
		int j = index % COLS;
		float spriteWidth = width / COLS;
		return width / 2 + (j - (COLS - 1) * 0.5f) * spriteWidth;
	}
	
	private int rows() {
		return (Tower.towerCount() - 1) / COLS + 1;
	}
	
	private float getItemSpriteY(int index) {
		int i = index / COLS;
		int rows = rows();
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
		createSelectionSprites();
		createCountSprites();
		
		screen.state().setInventoryChangedListener(new InventoryChangedListener() {
			@Override
			public void onInventoryChanged(int index, int count) {
				refreshCountSprite(index);
			}
		});
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
		CanvasImage image = CanvasUtils.createText(text, textFormat, Colors.BLACK);
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
		
		itemButtons = new Button[Tower.towerCount()];
		for (int index = 0; index < itemButtons.length; index++) {
			final TowerType towerType = Tower.getTypeByIndex(index);
			
			TextLayout layout = graphics().layoutText(towerType.instance().name(), textFormat);
			
			float indentX = Math.max(0, layout.width() - spriteSize) / 2;
			
			CanvasImage image = graphics().createImage(Math.max(spriteSize, layout.width()), spriteSize + textHeight);
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().fillRoundRect(indentX, 0, spriteSize, spriteSize, rad);
			image.canvas().setStrokeColor(Colors.DARK_GRAY);
			image.canvas().setStrokeWidth(strokeWidth);
			image.canvas().strokeRoundRect(indentX + strokeWidth / 2 - 1, strokeWidth / 2 - 1, 
					spriteSize - strokeWidth + 2, 
					spriteSize  - strokeWidth + 2, rad);
			
			Image towerImage = towerType.instance().createImage(cellSize, grid.towerColor());
			image.canvas().drawImage(towerImage, indentX + (spriteSize - towerImage.width()) / 2, 
					(spriteSize - towerImage.height()) / 2);
			
			image.canvas().setFillColor(Colors.WHITE);
			image.canvas().fillText(layout, (image.width() - layout.width()) / 2, 
					image.height() - textHeight + ITEM_SPRITE_MARGIN / 2);
			
			final Button button = new Button(image, false);
			screen.registerHighlightable(button, Tag.Defense_Towers);
			if (towerType == TowerType.PeaShooter) {
				screen.registerHighlightable(button, Tag.Defense_PeaShooter);
			}
			
			float x = getItemSpriteX(index);
			float y = getItemSpriteY(index);
			button.setPosition(x, y);
			button.setTint(Colors.WHITE, Color.rgb(230, 230, 230));
			button.setNoSound();
			
			final int fi = index;
			button.imageLayer().addListener(new Listener() {
				@Override
				public void onPointerStart(Event event) {
					if (!button.enabled()) return;
					grid.startPlacement(towerType.newInstance());
					Audio.se().play(Constant.SE_TICK);
				}
				
				@Override
				public void onPointerEnd(Event event) {
					if (!button.enabled()) return;
					if (grid.endPlacement(event.x(), event.y())) {
						towerCounts()[fi]--;
						refreshCountSprite(fi);
						Audio.se().play(Constant.SE_DROP);
					}
				}
				
				@Override
				public void onPointerDrag(Event event) {
					if (!button.enabled()) return;
					grid.updatePlacement(event.x(), event.y());
					
				}
				
				@Override
				public void onPointerCancel(Event event) { 
					grid.cancelPlacement();
				}
			});
			
			groupLayer.add(button.layerAddable());
			itemButtons[index] = button;
		}
	}
}
