package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Equation;

public class Problem {
	private Equation equation;
	private Reward reward;
	
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
}
