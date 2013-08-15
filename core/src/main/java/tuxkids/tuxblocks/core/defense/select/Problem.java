package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.utils.Persistable;

public class Problem implements Persistable {
	private Reward reward;
	private Equation equation;
	
	public Equation equation() {
		return equation;
	}
	
	public Reward reward() {
		return reward;
	}
	
	public void setEquation(Equation equation) {
		this.equation = equation;
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
	}
}
