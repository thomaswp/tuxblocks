package tuxkids.tuxblocks.core.defense.round;

import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * A reward for finishing a {@link Round} which consists of a
 * {@link TowerType} and a count.
 */
public class Reward implements Persistable {
	private TowerType tower;
	private int count;
	
	public TowerType tower() { return tower; }
	public int count() { return count; }
	
	public Reward(TowerType tower, int count) {
		this.tower = tower;
		this.count = count;
	}
	
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new Reward(TowerType.PeaShooter, 0);
			}
			
		};
	}

	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		tower = TowerType.values()[data.persist(tower.ordinal())];
		count = data.persist(count);
	}
}
