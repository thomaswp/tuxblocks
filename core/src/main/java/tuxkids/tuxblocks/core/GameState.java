package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BaseBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Equation;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.Equation.Builder;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.MinusBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.NumberBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.OverBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.TimesBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.VariableBlockSprite;
import tuxkids.tuxblocks.core.solve.blocks.n.sprite.BlockController.Side;
import tuxkids.tuxblocks.core.solve.expression.EquationGenerator;
import tuxkids.tuxblocks.core.utils.Debug;

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
	
	public void addProblemWithReward(Reward reward) {
		Builder builder = new Builder();
		
		BaseBlockSprite sprite1 = new VariableBlockSprite("x")
		.addModifier(new TimesBlockSprite(3));
		builder.addLeft(sprite1);
		
		BaseBlockSprite sprite2 = new VariableBlockSprite("x")
		.addModifier(new MinusBlockSprite(7))
		.addModifier(new OverBlockSprite(4));
		builder.addLeft(sprite2);
		
		builder.addRight(new NumberBlockSprite(8));
		
		Equation eq = builder.createEquation();//EquationGenerator.generate((int)(Math.random() * (maxSteps - minSteps)) + minSteps);
		Problem problem = new Problem(eq, reward);
		problems.add(problem);
		if (problemAddedListener != null) problemAddedListener.onProblemAdded(problem);
	}

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
