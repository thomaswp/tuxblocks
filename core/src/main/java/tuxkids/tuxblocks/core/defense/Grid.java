package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import playn.core.util.Clock;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.utils.Debug;

public class Grid extends PlayNObject implements Listener {
	private int cellSize;
	private int rows, cols;
	private GroupLayer groupLayer;
	private ImageLayer gridSprite;
	private boolean[][] passability;
	private List<Walker> walkers = new ArrayList<Walker>();
	private Point destination;
	
	public int width() {
		return cols * cellSize;
	}
	
	public int height() {
		return rows * cellSize;
	}
	
	public int rows() {
		return rows;
	}
	
	public int cols() {
		return cols;
	}
	
	public GroupLayer getLayer() {
		return groupLayer;
	}
	
	public boolean[][] getPassability() {
		return passability;
	}

	public float getCellSize() {
		return cellSize;
	}
	
	public Grid(int rows, int cols, int maxWidth, int maxHeight) {
		this.rows = rows; this.cols = cols;
		passability = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[i][j] = true;
			}
		}
		int maxRowSize = maxHeight / rows, maxColSize = maxWidth / cols;
		cellSize = Math.min(maxRowSize, maxColSize);
		groupLayer = graphics().createGroupLayer();
		destination = new Point(rows - 1, cols - 1);
		createGridSprite();
		createPlace();
	}
	
	long timer;
	public void update(int delta) {
		timer += delta;
		if (timer > 5000) {
			timer -= 5000;
			addWalker(new Walker(this, new Point(0, 0), destination, 500));
		}

		for (Walker walker : walkers) {
			walker.update(delta);
		}
	}
	
	public void paint(Clock clock) {
		for (Walker walker : walkers) {
			walker.paint(clock);
		}
	}
	
	public void addWalker(Walker walker) {
		walkers.add(walker);
		groupLayer.add(walker.getSprite());
	}

	private ImageLayer place;
	private Point placePos = new Point();
	private void createPlace() {
		CanvasImage image = graphics().createImage(cellSize * 2, cellSize * 2);
		image.canvas().setFillColor(Colors.WHITE);
		image.canvas().setStrokeColor(Colors.BLACK);
		image.canvas().fillRect(0, 0, image.width(), image.height());
		image.canvas().strokeRect(0, 0, image.width(), image.height());
		groupLayer.add(place = graphics().createImageLayer(image));
		place.setVisible(false);
		place.setDepth(1);
	}
	
	private void createGridSprite() {
		if (gridSprite != null) {
			groupLayer.remove(gridSprite);
		}
		
		//List<Point> path = Pathing.getPath(this, new Point(0, 0), new Point(rows - 1, cols - 1));
		
		CanvasImage image = graphics().createImage(width(), height());
		Canvas canvas = image.canvas();
		canvas.setFillColor(Colors.WHITE);
		canvas.fillRect(0, 0, width(), height());
		canvas.setStrokeColor(Colors.BLACK);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int x = j * cellSize;
				int y = i * cellSize;
				canvas.setFillColor(Colors.WHITE);
//				if (path != null && path.contains(new Point(i,j))) {
//					canvas.setFillColor(Colors.BLUE);
//				} else 
				if (!passability[i][j]) {
					canvas.setFillColor(Colors.BLACK);
				}
				canvas.fillRect(x, y, cellSize, cellSize);
				canvas.strokeRect(x, y, cellSize, cellSize);
			}
		}
		gridSprite = graphics().createImageLayer(image);
		//gridSprite.setAlpha(0.2f);
		groupLayer.add(gridSprite);
		gridSprite.addListener(this);
		gridSprite.setDepth(-1);
	}

	public Point getCell(float x, float y) {
		return new Point((int)y / cellSize, (int)x / cellSize);
	}
	
	@Override
	public void onPointerStart(Event event) {
		place.setVisible(true);
	}

	@Override
	public void onPointerEnd(Event event) {
		if (place.tint() == Colors.GREEN) {
			Point p = getCell(event.x(), event.y());
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					passability[i+p.x][j+p.y]= false; 
				}
			}
//			for (Walker walker : walkers) {
//				walker.refreshPath();
//			}
			createGridSprite();
		}
		place.setVisible(false);
	}

	@Override
	public void onPointerDrag(Event event) {
		Point p = getCell(event.x(), event.y());
		if (!placePos.equals(p)) {
			placePos.setLocation(p);
			place.setTint(canPlace(p) ? Colors.GREEN : Colors.RED);
			place.setTranslation(placePos.y * cellSize, placePos.x * cellSize);
			Debug.write(placePos);
		}
	}
	
	private boolean canPlace(Point p) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (!passability[p.x+i][p.y+j]) return false;
				for (Walker walker : walkers) {
					if (walker.getLocation().distanceSq(p.x+i, p.y+j) == 0) {
						return false;
					}
				}
			}
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				passability[p.x+i][p.y+j] = false;
			}
		}
		List<Point> path = Pathing.getPath(this, new Point(0, 0), new Point(rows - 1, cols - 1));
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				passability[p.x+i][p.y+j] = true;
			}
		}
		return path != null;
	}

	@Override
	public void onPointerCancel(Event event) {
		// TODO Auto-generated method stub
		
	}
}
