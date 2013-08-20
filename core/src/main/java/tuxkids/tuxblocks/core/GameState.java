package tuxkids.tuxblocks.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.TowerState;
import tuxkids.tuxblocks.core.defense.round.Level;
import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.tower.Tower;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.Persistable;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;

public class GameState implements Persistable {
	
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

	private static final int SOLVE_EXP_BASE = 10;
	private static final int SOLVE_EXP_PER_LVL = 5;
	private static final int EXP_TO_POINTS_FACTOR = 1;
	private static final int MAX_PROBLEMS = 6;
	
	private final int[] towerCounts;
	private final List<Problem> problems;
	private final int[] statLevels = new int[Stat.values().length];
	private final int[] statExps = new int[Stat.values().length];
	private final Difficulty difficulty;

	private GameBackgroundSprite background;
	
	private InventoryChangedListener inventoryChangedListener;
	private ProblemsChangedListener problemsChangedListener;
	
	protected int lives = 20;
	protected int score = 0;
	protected int upgrades = 0;
	protected int earnedUpgrades = 0;
	protected Level level;
	protected Grid grid;
	protected TowerState loadedTowerState;
	
	public Grid grid() {
		return grid;
	}
	
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
		int nextLevelExp;
		boolean play = false;
		while (statExps[index] >= (nextLevelExp = 
				getNextLevelExp(statLevels[index]))) {
			statExps[index] -= nextLevelExp;
			statLevels[index]++;
			play = true;
		}
		if (play) Audio.se().play(Constant.SE_SUCCESS_SPECIAL);
		addPoints(exp * EXP_TO_POINTS_FACTOR);
	}

	private int getNextLevelExp(int level) {
		return 50 + 20 * level;
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
	
	public void setProblemAddedListener(ProblemsChangedListener problemAddedListener) {
		this.problemsChangedListener = problemAddedListener;
	}
	
	public void setBackground(GameBackgroundSprite background) {
		this.background = background;
	}
	
	public GameState(Difficulty difficulty) {
		this.difficulty = difficulty;
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		level = Level.generate(difficulty.roundTime);
		addItem(TowerType.PeaShooter, 3);
//		addItem(TowerType.BigShooter, 2);
//		addItem(TowerType.Zapper, 2);
//		addItem(TowerType.Freezer, 2);
		for (int i = 0; i < 2; i++) {
			addProblemWithReward(0);
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
	
	protected final int MAX_REWARD_POINTS = 6;
	
	protected Equation createEquation(int difficulty, float percFinished) {
		return EquationGenerator.generate(difficulty, percFinished);
	}
	
	public void addProblemWithReward(float percFinished) {

		// give a chance to select a higher or lower difficulty problem
		// the higher the level, the higher the chance of a more difficult problem
		float barLower = 0.5f - percFinished / 2;
		float barHigher = 0.5f;
		int difficulty = this.difficulty.mathDifficulty;
		float r = (float) Math.random();
		if (r < barLower) {
			if (difficulty > 0) {
				difficulty--;
				percFinished = Math.max(0, percFinished - 1f / MAX_REWARD_POINTS);
			}
		} else if (r < barHigher) {
			if (difficulty < Difficulty.MAX_MATH_DIFFICULTY - 1) {
				difficulty++;
				percFinished = Math.min(1, percFinished + 1f / MAX_REWARD_POINTS);
			}
		}
		
		Equation eq = createEquation(difficulty, percFinished);
		Problem problem = new Problem(eq, d(percFinished));
		while (problems.size() >= MAX_PROBLEMS) {
			removeProblem();
		}
		problems.add(problem);
		if (problemsChangedListener != null) problemsChangedListener.onProblemAdded(problem);
	}
	
	private Reward d(float percFinished) {
		int points = (int)(percFinished * (MAX_REWARD_POINTS - 0.5f) + 0.5f) + 1;
		ArrayList<Reward> possibleRewards = new ArrayList<Reward>();
		for (TowerType type : TowerType.values()) {
			if (type.instance().cost() <= points) {
				possibleRewards.add(new Reward(type, points / type.instance().cost()));
			}
		}
		Reward reward = possibleRewards.get(
				(int) (Math.random() * possibleRewards.size()));
		return reward;
	}
	
	private void removeProblem() {
		for (int round = 0; round < 2; round++) {
			for (int i = 0; i < problems.size(); i++) {
				Problem problem = problems.get(i);
				if (problem.modified()) {
					problem.resetModified();
				} else {
					Problem removed = problems.remove(i);
					if (problemsChangedListener != null) {
						problemsChangedListener.onProblemRemoved(removed);
					}
					return;
				}
			}
		}
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
	
	public interface ProblemsChangedListener {
		void onProblemAdded(Problem problem);
		void onProblemRemoved(Problem problem);
	}

	public void loseLife() {
		lives--;
		Audio.se().play(Constant.SE_BEAT);
	}

	public void addPoints(int points) {
		score += points;
		if (score > nextUpgrade()) {
			upgrades++;
			earnedUpgrades++;
			Audio.se().play(Constant.SE_SUCCESS_SPECIAL);
		}
	}
	
	private int nextUpgrade() {
		int base = pointsPerUpgradeBase();
		return base * (earnedUpgrades + 1) * (earnedUpgrades + 2) / 2;
	}

	private int pointsPerUpgradeBase() {
		return (int)(150 * difficulty.getWalkerHpMultiplier()) * 5;
	}

	public void useUpgrades(int cost) {
		upgrades -= cost;
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new GameState(new Difficulty());
			}
		};
	}

	public void registerGrid(Grid grid) {
		this.grid = grid;
		if (loadedTowerState != null) {
			loadedTowerState.set(grid);
			loadedTowerState = null;
		}
	}
	
	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		data.persistArray(towerCounts);
		data.persistArray(statLevels);
		data.persistArray(statExps);
		data.persistList(problems);
		data.persist(difficulty);
		
		lives = data.persist(lives);
		score = data.persist(score);
		upgrades = data.persist(upgrades);
		earnedUpgrades = data.persist(earnedUpgrades);
		level = data.persist(level);
		loadedTowerState = data.persist(new TowerState(grid));
	}

	public boolean canSave() {
		return !level.duringRound();
	}
	
	public void finishRound() {
		if (difficulty.roundTime > 0) {
			addPoints(((Difficulty.TIMES[1] - difficulty.roundTime) / 2 + 10) * 5);
		}
	}

	public void update(int delta) {
		if (level != null) {
			level.update(delta);
		}
	}
}
