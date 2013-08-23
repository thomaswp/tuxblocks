package tuxkids.tuxblocks.core.tutorial;

/** Tutorial for Play mode */
public class TutorialPlay extends Tutorial {

	public TutorialPlay(int themeColor, int secondaryColor) {
		super(themeColor, secondaryColor);
	}

	@Override
	protected void addActions() {
		addAction(Trigger.Difficulty_Shown)
		.addHighlight(Tag.Difficulty_Start);
		addAction(Trigger.Defense_Shown)
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
		.addHighlight(Tag.Defense_MoreTowers);
		
		addAction(Trigger.Select_Shown)
		.addHighlight(Tag.Select_FirstButton);
		addAction(Trigger.Solve_Shown);
		addAction(Trigger.Solve_BlockReleased)
		.setSkip(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Number_Shown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Number_NumberSelected)
		.addHighlight(Tag.Number_Ok);
		addAction(Trigger.Solve_SimplifiedSuccess)
		.dontRepeat();
		addAction(Trigger.Solve_Solved)
		.addHighlight(Tag.Solve_Ok);

		addAction(Trigger.Select_Shown)
		.addHighlight(Tag.Select_SecondButton);
		addAction(Trigger.Solve_Shown);
		addAction(Trigger.Solve_BlockReleasedOnBlank);
		addAction(Trigger.Solve_BlockWithModifiersReleasedOnBlank);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Solve_BaseBlockReleasedOnOther);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Solve_VerticalModifierDoubleClicked);
		addAction(Trigger.Solve_Simplified);
		addAction(Trigger.Number_Shown);
		addAction(Trigger.Solve_SimplifiedSuccess);
		addAction(Trigger.Solve_BlockReleasedOnOther);
		addAction(Trigger.Solve_VariablesStartedCombine)
		.addHighlight(Tag.Solve_Reset);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Number_Shown)
		.addHighlight(Tag.Number_Scratch);
		addAction(Trigger.Number_Scratch)
		.addHighlight(Tag.Number_Clear);
		addAction(Trigger.Select_Shown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden)
		.addHighlight(Tag.Select_Return);
		addAction(Trigger.Defense_Shown)
		.addHighlight(Tag.Defense_PeaShooter);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		
	}
}
