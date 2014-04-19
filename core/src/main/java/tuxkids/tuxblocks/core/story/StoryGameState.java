package tuxkids.tuxblocks.core.story;

import java.util.HashMap;
import java.util.Map;

import pythagoras.i.Point;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.Grid;
import tuxkids.tuxblocks.core.defense.tower.PeaShooter;
import tuxkids.tuxblocks.core.defense.tower.TowerType;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial0;
import tuxkids.tuxblocks.core.tutorial.Tutorial1;
import tuxkids.tuxblocks.core.tutorial.TutorialInstance;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.persist.Persistable;

public class StoryGameState extends GameState implements StoryGameStateKeys{

	private int lesson = 0;
	
	private boolean hasSetupTowersForLesson = false;
	
	private Map<String, Boolean> booleansMap = new HashMap<String, Boolean>();
	
	public StoryGameState() {
		super(new Difficulty(0, 2, Difficulty.ROUND_TIME_INFINITE));

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
		
		lesson = data.persist(lesson);
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
		switch (lesson) {
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

	public TutorialInstance getCurrentTutorialInstance() {
		
		switch (lesson) {
		case 0:
			return new Tutorial0(this);
		case 1:
			return new Tutorial1(this);
		default:
			Debug.write("No lesson prepared for "+lesson);
			return null;
		}
	}

	public void finishedLesson() {
		lesson++;
		TutorialInstance nextTutorial = getCurrentTutorialInstance();
		if (nextTutorial != null) Tutorial.loadTutorial(nextTutorial);
	}
	
	@Override
	protected Equation createEquation(int difficulty, float percFinished) {
		if (studentModel.isReadyForNextStarred())
			return studentModel.getNextStarredEquation();
		return studentModel.getNextGeneralEquation();
	}

	public void setBoolean(String key, boolean value) {
		booleansMap.put(key, value);
		Debug.write(key + " = "+ value);
	}

	public boolean getBoolean(String key) {
		Boolean retVal = booleansMap.get(key);
		if (retVal == null) return false;
		return retVal.booleanValue();
	}

	public boolean isThereAStarredProblemOut() {
		return false;
	}
	


}
