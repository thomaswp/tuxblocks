package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.solve.blocks.Equation.Builder;

public class GameState {
	private int[] towerCounts;
	private List<Problem> problems;
	private GameBackgroundSprite background;
	private InventoryChangedListener inventoryChangedListener;
	private ProblemAddedListener problemAddedListener;
	private int maxSteps = 1;
	private int minSteps = 2;
	
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
	
	public void setInventoryChangedListener(InventoryChangedListener inventoryChangedListener) {
		this.inventoryChangedListener = inventoryChangedListener;
	}
	
	public void setProblemAddedListener(ProblemAddedListener problemAddedListener) {
		this.problemAddedListener = problemAddedListener;
	}
	
	public GameState() {
		background = new GameBackgroundSprite();
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		addItem(TowerType.PeaShooter, 2);
		addProblemWithReward(new Reward(TowerType.PeaShooter, 2));
		addProblemWithReward(new Reward(TowerType.PeaShooter, 2));
		addProblemWithReward(new Reward(TowerType.PeaShooter, 2));
		addProblemWithReward(new Reward(TowerType.PeaShooter, 2));
	}

	public void solveProblem(Problem problem) {
		problems.remove(problem);
		addReward(problem.reward());
	}

	public void addReward(Reward reward) {
		if (reward.tower != null) {
			towerCounts()[reward.tower.index()] += reward.count;
			onInventoryChanged(reward.tower.index());
		}
	}
	
	int index;
	public void addProblemWithReward(Reward reward) {		
		Equation eq = eqs[index++ % eqs.length]; //EquationGenerator.generate((int)(Math.random() * (maxSteps - minSteps)) + minSteps);
		Problem problem = new Problem(eq, reward);
		problems.add(problem);
		if (problemAddedListener != null) problemAddedListener.onProblemAdded(problem);
	}
	
	private Equation[] eqs = new Equation[] {
			
			new Builder()
			.addLeft(new VariableBlock("x").plus(2).times(3))
			.addRight(new NumberBlock(9))
			.createEquation(),
			
			new Builder()
			.addLeft(new VariableBlock("x").plus(2).times(3).minus(3).over(5))
			.addLeft(new BlockHolder())
			.addRight(new NumberBlock(3))
			.createEquation(),
			
			new Builder()
			.addLeft(new VariableBlock("x").times(3))
			.addLeft(new VariableBlock("x").minus(7).over(4))
			.addRight(new NumberBlock(8))
			.createEquation(),
			
			new Builder()
			.addLeft(new VariableBlock("x").plus(12).over(3))
			.addLeft(new VariableBlock("x").times(25).over(6))
			.addRight(new NumberBlock(8))
			.addRight(new VariableBlock("x").over(2))
			.createEquation(),
					
	};

	public void addItem(TowerType type, int count) {
		int index = type.index();
		towerCounts[index] += count;
		onInventoryChanged(index);
	}
	
	private void onInventoryChanged(int index) {
		if (inventoryChangedListener != null) {
			inventoryChangedListener.onInventoryChanged(index, towerCounts[index]);
		}
	}
	
	public interface InventoryChangedListener {
		void onInventoryChanged(int index, int count);
	}
	
	public interface ProblemAddedListener {
		void onProblemAdded(Problem problem);
	}
}
