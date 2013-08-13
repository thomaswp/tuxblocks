package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.List;

import tripleplay.ui.Background;
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
	
	private final int[] towerCounts;
	private final List<Problem> problems;
	private final GameBackgroundSprite background;
	private final int[] statLevels = new int[Stat.values().length];
	private final int[] statExps = new int[Stat.values().length];
	private final Difficulty difficulty;
	
	private InventoryChangedListener inventoryChangedListener;
	private ProblemAddedListener problemAddedListener;
	
	protected int lives = 20;
	protected int score = 0;
	protected int upgrades = 2;
	protected int earnedUpgrades = 0;
	protected Level level;
	
	public Difficulty difficulty() {
		return difficulty;
	}
	
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
	
	public GameState(GameBackgroundSprite background, Difficulty difficulty) {
		this.background = background;
		this.difficulty = difficulty;
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		level = Level.generate(difficulty.roundTime);
		addItem(TowerType.PeaShooter, 2);
		for (int i = 0; i < 2; i++) {
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
	
	protected Equation createEquation() { 
		return EquationGenerator.generate(difficulty);
	}
	
	public void addProblemWithReward(Reward reward) {
//		Equation eq = EquationGenerator.generateComposite(2, 3, 2);
		Equation eq = createEquation();
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
