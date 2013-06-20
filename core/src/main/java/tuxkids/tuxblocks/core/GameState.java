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
	private GameBackgroundSprite background;
	
	public int[] towerCounts() {
		return towerCounts;
	}
	
	public List<Problem> problems() {
		return problems;
	}
	
	public GameBackgroundSprite background() {
		return background;
	}
	
	public int themeColor() {
		return background().primaryColor();
	}
	
	public void newThemeColor() {
		background.newThemeColor();
	}
	
	public GameState() {
		background = new GameBackgroundSprite();
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		int maxSteps = 2;
		int minSteps = 4;
		for (int i = 0; i < 8; i++) {
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
