package tuxkids.tuxblocks.core.tutorial;

import tripleplay.util.Colors;


public class Tutorial1 extends Tutorial {

	public Tutorial1(int themeColor, int secondaryColor, String path) {
		super(themeColor, secondaryColor, path);
	}

	@Override
	protected void addActions() {
		addAction(null);
		addAction(Trigger.TextBoxHidden);
//		.addIndicatorR("buttons", Colors.WHITE, 0.5f, 0.6f, 0.25f, -1);
		addAction(Trigger.Difficulty_Shown);
//		.addIndicatorR("back", themeColor, 0.08f, 0.03f, 0.2f, -1, Align.TopLeft)
//		.addIndicatorR("start", Colors.BLACK, 0.89f, 0.04f, 0.16f, -1, Align.TopRight);
		addAction(Trigger.Defense_Shown);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Defense_TowerDropped);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Defense_GridZoom);
		addAction(Trigger.Defense_TowerSelected);
		addAction(Trigger.Defense_TowerUpgraded);
		addAction(Trigger.TextBoxHidden);
		addAction(Trigger.Defense_GridZoom);
		addAction(Trigger.Defense_StartRound);
		addAction(Trigger.Defense_RoundOver);
		addAction(Trigger.Select_Shown);
	}
}
