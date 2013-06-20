package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.util.Clock;
import pythagoras.f.Vector;
import pythagoras.i.Point;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.projectile.Projectile;
import tuxkids.tuxblocks.core.defense.round.Round;
import tuxkids.tuxblocks.core.defense.round.Wave;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.walker.Peon;
import tuxkids.tuxblocks.core.defense.walker.Walker;
import tuxkids.tuxblocks.core.utils.MultiList;

public class Grid extends PlayNObject {
	
	private final static boolean SHOW_GRID = false;
	
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
	private ImageLayer toPlacePreview;
	private List<Point> currentPath;
	private Round round;
	private float targetAlpha;
	private int towerColor;
	
	public int towerColor() {
		return towerColor;
	}
	
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
	
	public void setTowerColor(int themeColor) {
		this.towerColor = themeColor;
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
		walkerStart = new Point(rows / 2, 0);
		walkerDestination = new Point(rows / 2, cols - 1);
		for (int i = 0; i < rows; i++) {
			if (i != rows / 2) {
				passability[i][0] = false;
				passability[i][cols - 1] = false;
			}
		}
		for (int i = 0; i < cols; i++) {
			passability[0][i] = false;
			passability[rows - 1][i] = false;
		}
		refreshPath();
		createGridSprite();
		
		round = new Round() {
			@Override
			protected void populateRound() {
				addWave(new Wave(new Peon(), 500, 3), 3000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
				addWave(new Wave(new Peon(), 500, 3), 6000);
			}
		};
	}

	public void fadeIn(float targetAlpha) {
		this.targetAlpha = targetAlpha;
		groupLayer.setAlpha(0);
	}
	
	public void update(int delta) {
		if (groupLayer.alpha() < targetAlpha * 0.99f) {
			groupLayer.setAlpha(lerp(groupLayer.alpha(), targetAlpha, 1 - (float)Math.pow(0.99, delta)));
		} else {
			groupLayer.setAlpha(targetAlpha);
		}
		
		Walker walker = round.update(delta);
		if (walker != null) {
			addWalker(walker.place(this, walkerStart, walkerDestination));
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
				if (!passability[i][j]) {
					canvas.setFillColor(Colors.GRAY);
				}
				canvas.fillRect(x, y, cellSize, cellSize);
				if (SHOW_GRID) canvas.strokeRect(x, y, cellSize, cellSize);
			}
		}
		gridSprite = graphics().createImageLayer(image);
		//gridSprite.setAlpha(0.2f);
		groupLayer.add(gridSprite);
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
	
	public void startPlacement(Tower toPlace) {
		this.toPlace = toPlace;
		toPlace.preview(this);
		toPlace.layer().setVisible(false);
		groupLayer.add(toPlace.layer());
		validPlacementMap.clear();
		
		toPlacePreview = graphics().createImageLayer(toPlace.createRadiusImage());
		centerImageLayer(toPlacePreview);
		groupLayer.add(toPlacePreview);
		updateToPlace();
	}
	
	private float getPlaceX(float globalX) {
		float placeX = globalX - getGlobalTx(groupLayer);
		if (PlayN.platform().touch().hasTouch()) placeX -= width() / 20;
		return  placeX;
	}
	
	private float getPlaceY(float globalY) {
		float placeY = globalY - getGlobalTy(groupLayer);
		if (PlayN.platform().touch().hasTouch()) placeY -= width() / 20;
		return  placeY;
	}

	public void updatePlacement(float globalX, float globalY) {
		float placeX = getPlaceX(globalX), placeY = getPlaceY(globalY);
		if (toPlace != null) {
			Point cell = getCell(placeX, placeY, toPlace.width(), toPlace.height());
			toPlace.setCoordinates(cell);
			toPlace.layer().setVisible(!isOutOfBounds(placeX, placeY));
			updateToPlace();
		}
	}

	public boolean endPlacement(float globalX, float globalY) {
		boolean canPlace = canPlace();
		if (canPlace) {
			toPlace.place(this, toPlace.coordinates());
			towers.add(toPlace);
			refreshPath();
		} else if (toPlace != null) {
			toPlace.layer().destroy();
		}
		toPlace = null;
		toPlacePreview.destroy();
		toPlacePreview = null;
		return canPlace;
	}
	
	private void updateToPlace() {
		if (toPlace == null) return;
		toPlace.layer().setAlpha(canPlace() ? 1 : 0.5f);
		toPlacePreview.setTranslation(toPlace.position().x, toPlace.position().y);
		toPlacePreview.setVisible(toPlace.layer().visible() && toPlace.layer().alpha() == 1);
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
		
		if (p.equals(walkerStart)) return false;
		
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

	public boolean fireProjectile(Tower tower) {
		if (walkers.size() == 0) return false;
		Walker target = null;
		float targetDis = Float.MAX_VALUE;
		for (Walker walker : walkers) {
			if (!walker.isAlive()) continue;
			float dis = walker.position().distance(tower.position());
			if (dis < tower.range() * cellSize && dis < targetDis) {
				target = walker;
				targetDis = dis;
			}
		}
		if (target == null) return false;
		Projectile p = tower.createProjectile();
		p.place(this, target, tower);
		groupLayer.add(p.layer());
		projectiles.add(p);
		return true;
	}
	
	public Walker getHitWalker(Vector position) {
		for (Walker walker : walkers) {
			if (!walker.isAlive()) continue;
			float dx = walker.position().x - position.x;
			float dy = walker.position().y - position.y;
			if (Math.abs(dx) < walker.width() / 2 && Math.abs(dy) < walker.height() / 2) {
				return walker;
			}
		}
		return null;
	}

	public boolean isOutOfBounds(Vector position) {
		return isOutOfBounds(position.x, position.y);
	}
	
	public boolean isOutOfBounds(float x, float y) {
		return x < 0 || y < 0 || x >= width() || y >= height();
	}
}
