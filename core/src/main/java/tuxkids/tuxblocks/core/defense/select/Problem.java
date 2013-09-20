package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

/**
 * Represents a problem that can be solved by the player to
 * get more {@link Tower}s. Problems are displayed on the 
 * {@link SelectScreen} and consist of an {@link Equation} to
 * solve and a {@link Reward} for solving it.
 */
public class Problem implements Persistable {
	private Reward reward;
	private Equation equation;
	private boolean modified;
	
	public Equation equation() {
		return equation;
	}
	
	public Reward reward() {
		return reward;
	}
	
	/** 
	 * Returns true if this problem's {@link Equation} has been modified 
	 * since creation. This is used to determine which problems should be
	 * removed to make room for new ones.
	 */
	public boolean modified() {
		return modified;
	}
	
	public void setEquation(Equation equation) {
		this.equation = equation;
		modified = true;
	}

	/** Resets this Problem so that is is no longer considered modified */
	public void resetModified() {
		modified = false;
	}
	
	public Problem(Equation equation, TowerType reward, int rewardCount) {
		this(equation, new Reward(reward, rewardCount));
	}

	public Problem(Equation equation, Reward reward) {
		this.equation = equation;
		this.reward = reward;
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new Problem(null, null);
			}
		};
	}

	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		reward = data.persist(reward);
		equation = data.persist(equation);
		modified = data.persist(modified);
	}
}
