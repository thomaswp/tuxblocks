package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.HashMap;
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
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Peon;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.MultiList;

public class Grid extends PlayNObject implements Listener {
	private int cellSize;
	private int rows, cols;
	private GroupLayer groupLayer;
	private ImageLayer gridSprite;
	private boolean[][] passability;
	private List<Walker> walkers = new ArrayList<Walker>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private List<Tower> towers = new ArrayList<Tower>();
	@SuppressWarnings("unchecked")
	private MultiList<GridObject> gridObjects = new MultiList<GridObject>(walkers, projectiles, towers);
	private Point walkerStart, walkerDestination;
	private Tower toPlace;
	private List<Point> currentPath;
	
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
	
	public List<Point> currentPath() {
		return currentPath;
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
		walkerStart = new Point();
		walkerDestination = new Point(rows - 1, cols - 1);
		refreshPath();
		createGridSprite();
	}
	
	long timer;
	public void update(int delta) {
		timer += delta;
		if (timer > 5000) {
			timer -= 5000;
			addWalker(new Peon().place(this, walkerStart, walkerDestination));
		}
		
		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			if (gridObject.update(delta)) {
				gridObjects.remove(gridObject);
				i--; nObjects--;
			}
		}
		
		updateToPlace();
	}
	
	public void paint(Clock clock) {
		int nObjects = gridObjects.size();
		for (int i = 0; i < nObjects; i++) {
			GridObject gridObject = gridObjects.get(i);
			gridObject.paint(clock);
		}
	}
	
	private void refreshPath() {
		currentPath = Pathing.getPath(this, walkerStart, walkerDestination);
	}
	
	public void addWalker(Walker walker) {
		walkers.add(walker);
		groupLayer.add(walker.getSprite());
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
//				if (!passability[i][j]) {
//					canvas.setFillColor(Colors.BLACK);
//				}
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
		int r = Math.min(Math.max((int)y / cellSize, 0), rows - 1);
		int c = Math.min(Math.max((int)x / cellSize, 0), cols - 1);
		return new Point(r, c);
	}
	
	public Point getCell(float x, float y, float width, float height) {
		return getCell(x - width / 2 + getCellSize() / 2, y - height / 2 + getCellSize() / 2);
	}
	
	private int truncate(float x) {
		return (int)(x / cellSize) * cellSize;
	}
	
	@Override
	public void onPointerStart(Event event) {
		//Debug.write(System.currentTimeMillis());
		toPlace = new PeaShooter().preview(this, getCell(event.localX(), event.localY()));
		toPlace.setCoordinates(getCell(event.localX(), event.localY(), toPlace.width(), toPlace.height()));
		updateToPlace();
		groupLayer.add(toPlace.layer());
		validPlacementMap.clear();
	}

	@Override
	public void onPointerEnd(Event event) {
		if (canPlace()) {
			toPlace.place(this, toPlace.coordinates());
			towers.add(toPlace);
			refreshPath();
		} else if (toPlace != null) {
			toPlace.layer().destroy();
		}
		toPlace = null;
	}

	@Override
	public void onPointerDrag(Event event) {
		if (toPlace != null) {
			toPlace.setCoordinates(getCell(event.localX(), event.localY(), toPlace.width(), toPlace.height()));
			updateToPlace();
		}
	}
	
	private void updateToPlace() {
		if (toPlace == null) return;
		toPlace.layer().setAlpha(canPlace() ? 1 : 0.5f);
	}

	private boolean canPlace() {
		if (toPlace == null) return false;

		Point p = toPlace.coordinates();
		int rows = toPlace.rows(), cols = toPlace.cols();
		
		if (p.x < 0 || p.x + rows > this.rows || p.y < 0 || p.y + cols > this.cols){
			return false;
		}
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				for (Walker walker : walkers) {
					Point walkerPos = walker.coordinates();
					if (walkerPos.x == p.x + i && walkerPos.y == p.y + j) {
						return false;
					}
				}
			}
		}
		
		if (validPlacementMap.containsKey(p)) return validPlacementMap.get(p);
		boolean canPlace = canPlaceStatic(p);
		validPlacementMap.put(p.clone(), canPlace);
		
		return canPlace;
	}
	
	private HashMap<Point, Boolean> validPlacementMap = new HashMap<Point, Boolean>();
	private boolean canPlaceStatic(Point p) {
		
		int rows = toPlace.rows(), cols = toPlace.cols();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!passability[p.x+i][p.y+j]) return false;
			}
		}
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = false;
			}
		}
		List<Point> path = Pathing.getPath(this, walkerStart, walkerDestination);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				passability[p.x+i][p.y+j] = true;
			}
		}
		return path != null;
//		return true;
	}

	@Override
	public void onPointerCancel(Event event) {
		
	}

	public void fireProjectile(Tower tower) {
		if (walkers.size() == 0) return;
		Projectile p = tower.createProjectile();
		p.place(this, walkers.get(0), tower.position().clone());
		groupLayer.add(p.layer());
		projectiles.add(p);
	}
}
