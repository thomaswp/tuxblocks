package tuxkids.tuxblocks.core.tutorial;

import playn.core.PlayN;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;

/** Tutorial for Play mode */
public class TutorialPlay extends LinearTutorial {

	public TutorialPlay(int themeColor, int secondaryColor) {
		super(themeColor, secondaryColor);
	}

	@Override
	protected void addActions() {
		addAction(Trigger.Difficulty_Shown)
		.addHighlight(Tag.Difficulty_Start);
		addAction(Trigger.Defense_ScreenShown)
		.addHighlight(Tag.Defense_Grid);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Defense_Towers);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Defense_PeaShooter);
		addAction(Trigger.Defense_TowerDropped)
		.addHighlight(Tag.Menu_Countdown)
		.addHighlight(Tag.Menu_Lives);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Menu_Upgrades);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Defense_Grid);
		addAction(Trigger.Defense_GridZoom);
		addAction(Trigger.Defense_TowerSelected)
		.addHighlight(Tag.Defense_UpgradeTower);
		addAction(Trigger.Defense_TowerUpgraded)
		.addHighlight(Tag.Defense_DeleteTower);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Defense_GridZoom)
		.addHighlight(Tag.Defense_StartRound);
		addAction(Trigger.Defense_RoundOver)
		.addHighlight(Tag.Defense_EquationSelectScreen);
		
		addAction(Trigger.Select_ScreenShown)
		.addHighlight(Tag.Select_FirstEquation);
		addAction(Trigger.Solve_ScreenShown);
		addAction(Trigger.Solve_BlockReleased)
		.setSkip(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.NumberSelect_Shown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		if (!PlayN.keyboard().hasHardwareKeyboard()) {
			actions.remove(actions.size() - 1);
		}
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.NumberSelect_NumberSelected)
		.addHighlight(Tag.NumberSelect_Ok);
		addAction(Trigger.Solve_SimplifiedSuccess)
		.dontRepeat();
		addAction(Trigger.Solve_Solved)
		.addHighlight(Tag.Solve_Ok);

		addAction(Trigger.Select_ScreenShown)
		.addHighlight(Tag.Select_SecondEquation);
		addAction(Trigger.Solve_ScreenShown);
		addAction(Trigger.Solve_BlockReleasedOnBlank);
		addAction(Trigger.Solve_BlockWithModifiersReleasedOnBlank);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Solve_BaseBlockReleasedOnOther);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Solve_VerticalModifierDoubleClicked);
		addAction(Trigger.Solve_Simplified);
		addAction(Trigger.NumberSelect_Shown);
		addAction(Trigger.Solve_SimplifiedSuccess);
		addAction(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.Solve_VariablesStartedCombine)
		.addHighlight(Tag.Solve_Reset);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.NumberSelect_Shown)
		.addHighlight(Tag.NumberSelect_Scratch);
		addAction(Trigger.NumberSelect_Scratch)
		.addHighlight(Tag.NumberSelect_Clear);
		addAction(Trigger.Select_ScreenShown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Select_Return);
		addAction(Trigger.Defense_ScreenShown)
		.addHighlight(Tag.Defense_PeaShooter);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		
	}
}
