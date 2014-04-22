package tuxkids.tuxblocks.core.story;

import java.util.HashMap;
import java.util.Map;

import pythagoras.i.Point;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.defense.select.StarredProblem;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.tutorial.AbstractStarredTutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial0;
import tuxkids.tuxblocks.core.tutorial.Tutorial1;
import tuxkids.tuxblocks.core.tutorial.Tutorial2ExplainingStarred;
import tuxkids.tuxblocks.core.tutorial.TutorialInstance;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class StoryGameState extends GameState implements StoryGameStateKeys{

	private int tutorialIndex = 0;

	private boolean hasSetupTowersForLesson = false;

	private Map<String, Boolean> booleansMap = new HashMap<String, Boolean>();

	private final Equation[] cannedEquations = new Equation[] {
			new Equation.Builder().addLeft(new VariableBlock("x"))
					.addRight(new NumberBlock(4).times(3)).createEquation().name("3x4"),
			new Equation.Builder().addLeft(new VariableBlock("x"))
					.addRight(new NumberBlock(6).minus(5)).createEquation().name("6-5")
	};
	private int cannedEquationIndex = 0;

	public StoryGameState() {
		super(new Difficulty(0, 2, Difficulty.ROUND_TIME_INFINITE));
//		problems().add(new Problem(studentModel.getNextGeneralEquation(), createReward(1)));
//		problems().add(new Problem(studentModel.getNextGeneralEquation(), createReward(1)));
	}

	@Override
	protected void setUpProblems() {
		//The student model gets these.
	}

	@Override
	protected void setUpTowers() {
		addItem(TowerType.PeaShooter, 2);
	}

	public static Constructor constructor() {
		return new Constructor() {
			@Override
			public Persistable construct() {
				return new StoryGameState();
			}
		};
	}

	@Override
	public void persist(Data data) throws ParseDataException, NumberFormatException {
		super.persist(data);

		tutorialIndex = data.persist(tutorialIndex);
		hasSetupTowersForLesson = data.persist(hasSetupTowersForLesson);
		data.persist(studentModel);
	}

	@Override
	public void registerGrid(Grid grid) {
		super.registerGrid(grid);
		if (!hasSetupTowersForLesson) {
			setUpTowersForLesson(grid);
			hasSetupTowersForLesson = true;
		}
	}

	private void setUpTowersForLesson(Grid grid) {
		switch (tutorialIndex) {
		case 0:
			grid.placeTower(new PeaShooter(), new Point(8, 1));
			grid.placeTower(new PeaShooter(), new Point(7, 2));
			grid.placeTower(new PeaShooter(), new Point(6, 2));
			grid.placeTower(new PeaShooter(), new Point(7, 14));
			break;

		default:
			break;
		}
	}

	public TutorialInstance makeTutorialInstance() {

		switch (tutorialIndex) {
		case 0:
			return new Tutorial0();
		case 1:
			return new Tutorial1();
		default:
//			Debug.write("No lesson prepared for "+tutorialIndex);
			return null;
		}
	}

	public void finishedLesson() {
		tutorialIndex++;
		TutorialInstance nextTutorial = makeTutorialInstance();
		if (nextTutorial != null) {
			Tutorial.loadTutorial(nextTutorial, this);
		} else {
			Tutorial.unloadTutorial();
		}
	}

	@Override
	protected Problem createProblem(int difficulty, float percFinished, Reward reward) {
		if (cannedEquationIndex < cannedEquations.length) {
			return new Problem(cannedEquations[cannedEquationIndex++], reward);
		}
		if (/*studentModel.isReadyForNextStarred() && */level.roundNumber() > 1) {
			if (/*tutorialIndex > 1 && */!getBoolean(HESP)) {
				Tutorial.loadTutorial(new Tutorial2ExplainingStarred(), this);
			}
			//StarredTutorial tutorial = studentModel.getNextTutorial();
			return new StarredProblem(cannedEquations[0], reward, new StarredTutorial1());
			//return new StarredProblem(tutorial.createEquation(), reward, tutorial);
		} else {
			Equation equation = studentModel.getNextGeneralEquation();
			return new Problem(equation, reward);
		}
	}

	public void setBoolean(String key, boolean value) {
		booleansMap.put(key, value);
//		Debug.write(key + " = "+ value);
	}

	public boolean getBoolean(String key) {
		Boolean retVal = booleansMap.get(key);
		if (retVal == null) return false;
		return retVal.booleanValue();
	}


}
