package tuxkids.tuxblocks.core.defense.select;

import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.solve.expression.Equation;

public class Problem {
	private Equation equation;
	private Tower reward;
	private int rewardCount;
	
	public Equation equation() {
		return equation;
	}
	
	public Tower reward() {
		return reward;
	}
	
	public int rewardCount() {
		return rewardCount;
	}
	
	public void setEquation(Equation equation) {
		this.equation = equation;
	}
	
	public Problem(Equation equation, Tower reward, int rewardCount) {
		this.equation = equation;
		this.reward = reward;
		this.rewardCount = rewardCount;
	}
}
