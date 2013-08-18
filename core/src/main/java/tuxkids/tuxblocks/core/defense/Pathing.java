package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import pythagoras.f.FloatMath;
import pythagoras.i.Point;

public class Pathing {

	private final static int[] offRows = new int[] {0, 1, 0, -1};
	private final static int[] offCols = new int[] {1, 0, -1, 0};
//	private final static int[] offRows = new int[] {0, 1, 0, -1, 0, 2, 0, -2};
//	private final static int[] offCols = new int[] {1, 0, -1, 0, 2, 0, -2, 0};
	
	
	public static List<Point> getPath(Grid grid, Point from, Point to) {
		return getPath(grid, from, to, false);
	}
	
	public static List<Point> getPath(Grid grid, Point from, Point to, boolean canFly) {
		List<Point> closedSet = new ArrayList<Point>(),
				openSet = new ArrayList<Point>();
		HashMap<Point, Point> cameFrom = new HashMap<Point, Point>();
		final HashMap<Point, Float> gScore = new HashMap<Point, Float>(),
				fScore = new HashMap<Point, Float>();

		Comparator<Point> openSetComparator = new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return (int)Math.signum(fScore.get(o1) - fScore.get(o2));
			}
		};

		openSet.add(from);

		gScore.put(from, 0f);
		fScore.put(from, FloatMath.sqrt(from.distanceSq(to)));

		while (!openSet.isEmpty()) {
			Collections.sort(openSet, openSetComparator);
			Point current = openSet.remove(0);
			if (current.equals(to)) {
				return reconstructPath(cameFrom, to);
			}

			closedSet.add(current);
			for (int i = 0; i < offRows.length; i++) {
				int row = current.x + offRows[i];
				int col = current.y + offCols[i];
				if (row < 0 || row >= grid.rows()) continue;
				if (col < 0 || col >= grid.cols()) continue;
				if (!canFly && !grid.getPassability()[row][col]) continue;

				Point neighbor = new Point(row, col); 

				float tentativeGScore = gScore.get(current) + 1;
				if (closedSet.contains(neighbor) && tentativeGScore >= gScore.get(neighbor)) {
					continue;
				}

				if (!openSet.contains(neighbor) || tentativeGScore < gScore.get(neighbor)) {
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, tentativeGScore);
					fScore.put(neighbor, tentativeGScore + FloatMath.sqrt(neighbor.distanceSq(to)));
					if (!openSet.contains(neighbor)) {
						openSet.add(neighbor);
					}
				}
			}
		}

		return null;
	}

	private static List<Point> reconstructPath(HashMap<Point, Point> cameFrom, Point currentNode) {
		if (cameFrom.containsKey(currentNode)) {
			List<Point> p = reconstructPath(cameFrom, cameFrom.get(currentNode));
			p.add(currentNode);
			return p;
		}
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(currentNode);
		return path;
	}
}
