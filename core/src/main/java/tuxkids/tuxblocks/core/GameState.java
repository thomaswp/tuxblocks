package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.Equation.Builder;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;

public class GameState {
	
	public enum Stat {
		Plus("+"), 
		Minus("-"), 
		Times(Constant.TIMES_SYMBOL), 
		Over(Constant.DIVIDE_SYMBOL);

		private final String symbol;
		Stat(String symbol) { this.symbol = symbol; }
		public String symbol() {
			return symbol;
		}
	}

	private static final int POINTS_PER_UPGRADE = 500;
	private static final int SOLVE_EXP_BASE = 10;
	private static final int SOLVE_EXP_PER_LVL = 5;
	private static final int EXP_TO_POINTS_FACTOR = 1;
	
	private int[] towerCounts;
	private List<Problem> problems;
	private GameBackgroundSprite background;
	private InventoryChangedListener inventoryChangedListener;
	private ProblemAddedListener problemAddedListener;
	private int maxSteps = 1;
	private int minSteps = 4;
	private int[] statLevels = new int[Stat.values().length];
	private int[] statExps = new int[Stat.values().length];
	private int lives = 20;
	private int score = 0;
	private int upgrades = 20;
	private int earnedUpgrades = 0;
	private int timeBetweenRounds = 31;
	private Level level;
	
	public Level level() {
		return level;
	}
	
	public int upgrades() {
		return upgrades;
	}
	
	public int score() {
		return score;
	}
	
	public int getStatLevel(Stat stat) {
		return statLevels[stat.ordinal()];
	}
	
	public float getStatPerc(Stat stat) {
		return (float)statExps[stat.ordinal()] / getNextLevelExp(statLevels[stat.ordinal()]);
	}


	public void addExpForLevel(Stat stat, int level) {
		addExp(stat, SOLVE_EXP_BASE + level * SOLVE_EXP_PER_LVL);
	}
	
	public void addExp(Stat stat, int exp) {
		int index = stat.ordinal();
		statExps[index] += exp;
		int nextLevelExp = getNextLevelExp(statLevels[index]);
		if (statExps[index] >= nextLevelExp) {
			statExps[index] -= nextLevelExp;
			statLevels[index]++;
		}
		addPoints(exp * EXP_TO_POINTS_FACTOR);
	}

	private int getNextLevelExp(int level) {
		return 50;
	}

	public int lives() {
		return lives;
	}
	
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
	
	public float themeHue() {
		return background().primaryHue();
	}
	
	public int secondaryColor() {
		return background().secondaryColor();
	}
	
	public int ternaryColor() {
		return background().ternaryColor();
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
		level = Level.generate(timeBetweenRounds);
		addItem(TowerType.PeaShooter, 4);
		addItem(TowerType.BigShooter, 4);
		addItem(TowerType.Zapper, 4);
		addItem(TowerType.Freezer, 4);
		for (int i = 0; i < 8; i++) {
			addProblemWithReward(new Reward(TowerType.PeaShooter, 2));
		}
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
		//Equation eq = eqs[index++ % eqs.length]; 
		Equation eq = EquationGenerator.generateFormA3();
//		Equation eq = EquationGenerator.generate((int)(Math.random() * (maxSteps - minSteps)) + minSteps).toBlocks();
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

	public void loseLife() {
		lives--;
	}

	public void addPoints(int points) {
		score += points;
		int earned = score / POINTS_PER_UPGRADE;
		if (earned != earnedUpgrades) {
			upgrades += earned - earnedUpgrades;
			earnedUpgrades = earned;
		}
	}

	public void useUpgrades(int cost) {
		upgrades -= cost;
	}
}
