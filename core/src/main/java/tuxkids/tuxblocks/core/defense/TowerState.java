package tuxkids.tuxblocks.core.defense;

import java.util.HashMap;

import pythagoras.i.Point;
import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.utils.Persistable;

public class TowerState extends PlayNObject implements Persistable {
	
	HashMap<Point, Tower> towerMap = new HashMap<Point, Tower>();

	public TowerState(Grid grid) {
		if (grid != null) {
			for (Tower tower : grid.towers) {
				towerMap.put(tower.coordinates, tower);
			}
		}
	}
	
	public void set(Grid grid) {
		for (Point point : towerMap.keySet()) {
			grid.placeTower(towerMap.get(point), point);
		}
	}
	
	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		if (data.readMode()) {
			towerMap.clear();
			int n = data.persist(0);
			for (int i = 0; i < n; i++) {
				Point p = new Point(data.persist(0), data.persist(0));
				TowerType type = TowerType.values()[data.persist(0)];
				Tower tower = type.newInstance();
				tower.setUpgradeLevel(data.persist(0));
				towerMap.put(p, tower);
			}
		} else {
			data.persist(towerMap.size());
			for (Point point : towerMap.keySet()) {
				data.persist(point.x);
				data.persist(point.y);
				Tower tower = towerMap.get(point);
				data.persist(tower.type().ordinal());
				data.persist(tower.upgradeLevel());
			}
		}
	}
}
