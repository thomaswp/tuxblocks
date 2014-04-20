package tuxkids.tuxblocks.core;

import java.util.ArrayList;
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
import tuxkids.tuxblocks.core.student.BasicStudentModel;
import tuxkids.tuxblocks.core.student.StudentModel;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.persist.Persistable;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;

/**
 * Represents the state of a game in progress. The important
 * fields of this class are persisted so that games can be
 * saved and loaded.
 */
public class GameState implements Persistable {
	
	/** The 4 arithmetic functions */
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

	// student model for keeping track of what the player knows
	protected final StudentModel studentModel = new BasicStudentModel();

	// base exp for solving an arithmetic problem
	private static final int SOLVE_EXP_BASE = 10;
	// exp gained per difficulty level of an arithmetic problem
	private static final int SOLVE_EXP_PER_LVL = 5;
	// points added to score for each arithmetic exp gained
	private static final int EXP_TO_POINTS_FACTOR = 1;
	// max algebra problems shown at a time
	private static final int MAX_PROBLEMS = 6;
	
	// number of each type of tower in the player's inventory
	private final int[] towerCounts;
	// problems available for solving
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
	// number of upgrades earned so far, regardless of how many were spent
	protected int earnedUpgrades = 0;
	protected Level level;
	protected Grid grid;
	// object representing all towers on the grid (for persisting)
	protected TowerState loadedTowerState;

	public StudentModel studentModel() {
		return studentModel;
	}
	
	public GameState(Difficulty difficulty) {
		this.difficulty = difficulty;
		towerCounts = new int[Tower.towerCount()];
		problems = new ArrayList<Problem>();
		level = Level.generate(difficulty.roundTime);
		// start with 3 PeaShooters and 2 problems to solve
		setUpTowers();
		setUpProblems();
	}

	protected void setUpTowers() {
		addItem(TowerType.PeaShooter, 3);
	}

	protected void setUpProblems() {
		for (int i = 0; i < 2; i++) {
			addProblemWithReward(0);
		}
	}

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
	
	public int getStatLevel(Stat stat) {
		return statLevels[stat.ordinal()];
	}
	
	/** How close (in %) is this stat to levelling up */
	public float getStatPerc(Stat stat) {
		return (float)statExps[stat.ordinal()] / getNextLevelExp(statLevels[stat.ordinal()]);
	}

	/** Add arithmetic exp for solving a problem */
	public void addExpForSolving(Stat stat, int level) {
		addExp(stat, SOLVE_EXP_BASE + level * SOLVE_EXP_PER_LVL);
	}
	
	/** Add arithmetic exp for the given Stat */
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

	// exp needed for the next stat level-up
	private int getNextLevelExp(int level) {
		return 50 + 20 * level;
	}

	/** Removes the given problem and add the appropriate Tower reward */
	public void solveProblem(Problem problem) {
		problems.remove(problem);
		addReward(problem.reward());
	}

	/** Adds the given Reward to the player's inventory */
	public void addReward(Reward reward) {
		if (reward.tower() != null) {
			towerCounts()[reward.tower().ordinal()] += reward.count();
			onInventoryChanged(reward.tower().ordinal());
		}
	}
	
	// Creates an Equation based on a math difficulty and percent trough the
	// current game (problems get harder as the game goes on)
	protected Problem createProblem(int difficulty, float percFinished, Reward reward) {
		Equation equation = EquationGenerator.generate(difficulty, percFinished);
		return new Problem(equation, reward);
	}
	
	public void addProblemWithReward(float percFinished) {

		// The max number of "points" used to allocate a reward for
		// solving a problem. See it's use below.
		final int MAX_REWARD_POINTS = 6;
		
		
		// give a chance to select a higher or lower difficulty problem
		// the higher the level, the higher the chance of a more difficult problem
		float barLower = 0.5f - percFinished / 2;
		float barHigher = 0.5f;
		int difficulty = this.difficulty.mathDifficulty;
		float r = (float) Math.random();
		if (r < barLower) {
			if (difficulty > 0) {
				// decrease the difficulty and the reward
				difficulty--;
				percFinished = Math.max(0, percFinished - 1f / MAX_REWARD_POINTS);
			}
		} else if (r < barHigher) {
			if (difficulty < Difficulty.MAX_MATH_DIFFICULTY - 1) {
				// increase the difficulty and the reward
				difficulty++;
				percFinished = Math.min(1, percFinished + 1f / MAX_REWARD_POINTS);
			}
		}
		
		percFinished = Math.max(Math.min(percFinished, 1), 0);
		int points = (int)(percFinished * (MAX_REWARD_POINTS - 0.5f) + 0.5f) + 1;
		
		Problem problem = createProblem(difficulty, percFinished, createReward(points));
		while (problems.size() >= MAX_PROBLEMS) {
			removeProblem();
		}
		problems.add(problem);
		if (problemsChangedListener != null) problemsChangedListener.onProblemAdded(problem);
	}
	
	// creates a reward of value <= the supplied number of points
	private Reward createReward(int points) {
		ArrayList<Reward> possibleRewards = new ArrayList<Reward>();
		for (TowerType type : TowerType.values()) {
			// each tower's cost determines how many points it "costs"
			if (type.instance().cost() <= points) {
				possibleRewards.add(new Reward(type, points / type.instance().cost()));
			}
		}
		Reward reward = possibleRewards.get(
				(int) (Math.random() * possibleRewards.size()));
		return reward;
	}
	
	// removes a problem to make room for a new one
	private void removeProblem() {
		for (int round = 0; round < 2; round++) {
			for (int i = 0; i < problems.size(); i++) {
				Problem problem = problems.get(i);
				// On the first round, we skip problems
				// the have been attempted since the last time
				// This prevents the game from removing a 1/2 solved problem
				if (problem.modified()) {
					// But then we reset them so they can be removed
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

	/** Adds a tower of the given type and count to the player's inventory */
	public void addItem(TowerType type, int count) {
		int index = type.ordinal();
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
	
	// number of points required to achieve another upgrade
	private int nextUpgrade() {
		int base = pointsPerUpgradeBase();
		// roughly, the sum of the points required for each previous upgrade 
		return base * (earnedUpgrades + 1) * (earnedUpgrades + 2) / 2;
	}

	// base cost of the first upgrade
	private int pointsPerUpgradeBase() {
		return (int)(150 * difficulty.getWalkerHpMultiplier()) * 5;
	}

	public void useUpgrades(int number) {
		upgrades -= number;
	}

	/** See {@link Constructor} */
	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new GameState(new Difficulty());
			}
		};
	}

	/** Register the game's Grid with this game state, to be saved or loaded */
	public void registerGrid(Grid grid) {
		this.grid = grid;
		if (loadedTowerState != null) {
			loadedTowerState.set(grid);
			loadedTowerState = null;
		}
	}
	
	//Used for both saving and loading
	@Override
	public void persist(Data data) throws ParseDataException,
			NumberFormatException {
		// final members don't get reassigned, but they're modified
		// in the persist method if this is a "load" call
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
	
	/** Add the appropriate number of points for finishing a round */
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
