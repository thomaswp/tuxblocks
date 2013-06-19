package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.Inventory;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.solve.expression.Equation;
import tuxkids.tuxblocks.core.solve.expression.EquationGenerator;

public class GameState {
	private int[] towerCounts;
	private List<Problem> problems;
	
	public int[] towerCounts() {
		return towerCounts;
	}
	
	public List<Problem> problems() {
		return problems;
	}
	
	public GameState() {
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		int maxSteps = 1;
		int minSteps = 1;
		for (int i = 0; i < 10; i++) {
			Equation eq = EquationGenerator.generate((int)(Math.random() * (maxSteps - minSteps)) + minSteps);
			Tower reward = Tower.randomTower();
			int count = Math.round(eq.difficulty() / 10f / reward.cost());
			problems.add(new Problem(eq, reward, count));
		}
	}

	public void solveProblem(Problem problem) {
		problems.remove(problem);
		towerCounts[problem.reward().id()] += problem.rewardCount();
	}
}
