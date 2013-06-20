package tuxkids.tuxblocks.core.defense;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.util.Clock;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Walker;

public class GridSprite extends PlayNObject {
	
//	private final static boolean SHOW_GRID = false;
//	
//	private Grid grid;
//	private GroupLayer layer;
//	private int cellSize;
//
//	private ImageLayer gridSprite;
//	private float targetAlpha;
//	private ImageLayer toPlacePreview;
//	private Tower toPlace;
//	
//	public Layer layer() {
//		return layer;
//	}
//	
//	public int width() {
//		return grid.width();
//	}
//	
//	public int height() {
//		return grid.height();
//	}
//
//	public GridSprite(Grid grid) {
//		this.grid = grid;
//		layer = graphics().createGroupLayer();
//		cellSize = grid.getCellSize();
//		createGridSprite();
//	}
//
//	public void fadeIn(float targetAlpha) {
//		this.targetAlpha = targetAlpha;
//		layer.setAlpha(0);
//	}
//	
//	public void update(int delta) {
//		if (layer.alpha() < targetAlpha * 0.99f) {
//			layer.setAlpha(lerp(layer.alpha(), targetAlpha, 1 - (float)Math.pow(0.99, delta)));
//		} else {
//			layer.setAlpha(targetAlpha);
//		}
//		updateToPlace();
//	}
//	
//	public void paint(Clock clock) {
//		
//	}
//	
//
//	private void createGridSprite() {
//		if (gridSprite != null) {
//			layer.remove(gridSprite);
//		}
//		
//		//List<Point> path = Pathing.getPath(this, new Point(0, 0), new Point(rows - 1, cols - 1));
//		
//		int rows = grid.rows();
//		int cols = grid.cols();
//		
//		CanvasImage image = graphics().createImage(width(), height());
//		Canvas canvas = image.canvas();
//		canvas.setFillColor(Colors.WHITE);
//		canvas.fillRect(0, 0, width(), height());
//		canvas.setStrokeColor(Colors.BLACK);
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < cols; j++) {
//				int x = j * cellSize;
//				int y = i * cellSize;
//				canvas.setFillColor(Colors.WHITE);
////				if (path != null && path.contains(new Point(i,j))) {
////					canvas.setFillColor(Colors.BLUE);
////				} else 
//				if (!grid.getPassability()[i][j]) {
//					canvas.setFillColor(Colors.GRAY);
//				}
//				canvas.fillRect(x, y, cellSize, cellSize);
//				if (SHOW_GRID) canvas.strokeRect(x, y, cellSize, cellSize);
//			}
//		}
//		gridSprite = graphics().createImageLayer(image);
//		//gridSprite.setAlpha(0.2f);
//		layer.add(gridSprite);
//		gridSprite.setDepth(-1);
//	}
//
//	public Point getCell(float x, float y) {
//		int r = Math.min(Math.max((int)y / cellSize, 0), grid.rows() - 1);
//		int c = Math.min(Math.max((int)x / cellSize, 0), grid.cols() - 1);
//		return new Point(r, c);
//	}
//	
//	public Point getCell(float x, float y, float width, float height) {
//		return getCell(x - width / 2 + cellSize / 2, y - height / 2 + cellSize / 2);
//	}
//	
//	private void addWalker(Walker walker) {
//		layer.add(walker.getSprite());
//		grid.addWalker(walker);
//	}
//	
//	public void startPlacement(Tower toPlace) {
//		this.toPlace = toPlace;
//		toPlace.preview(grid);
//		toPlace.layer().setVisible(false);
//		layer.add(toPlace.layer());
//		grid.startPlacement();
//		
//		toPlacePreview = graphics().createImageLayer(toPlace.createRadiusImage());
//		centerImageLayer(toPlacePreview);
//		layer.add(toPlacePreview);
//		updateToPlace();
//	}
//	
//	private float getPlaceX(float globalX) {
//		float placeX = globalX - getGlobalTx(layer);
//		if (PlayN.platform().touch().hasTouch()) placeX -= width() / 20;
//		return  placeX;
//	}
//	
//	private float getPlaceY(float globalY) {
//		float placeY = globalY - getGlobalTy(layer);
//		if (PlayN.platform().touch().hasTouch()) placeY -= width() / 20;
//		return  placeY;
//	}
//
//	public void updatePlacement(float globalX, float globalY) {
//		float placeX = getPlaceX(globalX), placeY = getPlaceY(globalY);
//		if (toPlace != null) {
//			Point cell = getCell(placeX, placeY, toPlace.width(), toPlace.height());
//			toPlace.setCoordinates(cell);
//			toPlace.layer().setVisible(!grid.isOutOfBounds(placeX, placeY));
//			updateToPlace();
//		}
//	}
//
//	public boolean endPlacement(float globalX, float globalY) {
//		boolean canPlace = grid.canPlace(toPlace);
//		if (canPlace) {
//			toPlace.place(grid, toPlace.coordinates());
//			grid.addTower(toPlace);
//		} else if (toPlace != null) {
//			toPlace.layer().destroy();
//		}
//		toPlace = null;
//		toPlacePreview.destroy();
//		toPlacePreview = null;
//		return canPlace;
//	}
//	
//	private void updateToPlace() {
//		if (toPlace == null) return;
//		toPlace.layer().setAlpha(grid.canPlace(toPlace) ? 1 : 0.5f);
//		toPlacePreview.setTranslation(toPlace.position().x, toPlace.position().y);
//		toPlacePreview.setVisible(toPlace.layer().visible() && toPlace.layer().alpha() == 1);
//	}
}
