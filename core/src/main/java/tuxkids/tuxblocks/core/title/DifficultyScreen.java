package tuxkids.tuxblocks.core.title;

import java.util.ArrayList;
import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.ImageLayer;
import playn.core.Pointer.Event;
import playn.core.TextFormat;
import playn.core.util.Clock;
import tripleplay.game.ScreenStack;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.DefenseScreen;
import tuxkids.tuxblocks.core.screen.BaseScreen;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator;
import tuxkids.tuxblocks.core.solve.markup.ExpressionWriter;
import tuxkids.tuxblocks.core.tutorial.Tutorial;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.tutorial.TutorialGameState;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.utils.PlayNObject;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.Button.OnReleasedListener;
import tuxkids.tuxblocks.core.widget.DiscreteSlideLayer;
import tuxkids.tuxblocks.core.widget.DiscreteSlideLayer.StopChangedListener;
import tuxkids.tuxblocks.core.widget.GameBackgroundSprite;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

/**
 * Screen for selecting the difficulty of a new game.
 * Sets the properties of a new {@link Difficulty}.
 */
public class DifficultyScreen extends BaseScreen {
	
	
	protected final List<DiscreteSlideLayer> slideLayers = new ArrayList<DiscreteSlideLayer>();
	protected final DiscreteSlideLayer mathSlider, gameSlider, timeSlider;
	protected final TextFormat promptFormat, descriptionFormat;
	protected final float spacing;
	protected final HeaderLayer header;
	
	@Override
	protected String getScreenName() {
		return "difficulty";
	}
	
	public DifficultyScreen(ScreenStack screens, final GameBackgroundSprite background) {
		super(screens, background);
		
		// manually add in a header bar, since this isn't a GameScreen
		header = new HeaderLayer(width(), background.primaryColor());
		layer.add(header.layerAddable());
		
		
		// text prompt 
		ImageLayer titleLayer = graphics().createImageLayer();
		titleLayer.setImage(CanvasUtils.createTextCached(getString("choose-difficulty"), 
				Cache.createFormat(header.height() * 0.5f), Colors.BLACK));
		titleLayer.setDepth(1);
		titleLayer.setTranslation(width() / 2, header.height() / 2);
		PlayNObject.centerImageLayer(titleLayer);
		layer.add(titleLayer);
		
		// spacing between slider bars
		spacing = height() / 4.5f;
		float offY = header.height() * 0.95f;
		
		// format for slider prompts
		promptFormat = Cache.createFormat(height() / 22);
		// and slider descriptions
		descriptionFormat = Cache.createFormat(height() / 35);
		
		// math difficulty slider
		String[] mathDifficulties = new String[Difficulty.MAX_MATH_DIFFICULTY + 1];
		for (int i = 0; i < mathDifficulties.length; i++) mathDifficulties[i] = "" + (i+1);
		mathSlider = createSlideLyer(getString("math-difficulty"), null, 0.5f * spacing + offY, mathDifficulties);

		final ImageLayer descriptionLayer = graphics().createImageLayer();
		descriptionLayer.setImage(CanvasUtils.createTextCached(getString("sample-problem"), descriptionFormat, Colors.WHITE));
		layer.add(descriptionLayer);
		
		// create the preview equation layer
		final ImageLayer equationLayer = graphics().createImageLayer();
		equationLayer.setTranslation(width() * 0.6f + descriptionLayer.width() / 2, 0.75f * spacing + mathSlider.ty());
		equationLayer.setDepth(-1);
		descriptionLayer.setTy(equationLayer.ty() - descriptionLayer.height() / 2);
		layer.add(equationLayer);
		
		mathSlider.setStopChangedListener(new StopChangedListener() {
			@Override
			public void onStopChanged(int stop) {
				Equation eq = EquationGenerator.generateSample(stop);
				// make the equation text 
				int size = Math.round(Math.min(height() / 16 / eq.renderer().lines(), height() / 18));
				ExpressionWriter writer = eq.renderer().getExpressionWriter(Cache.createNumberFormat(size));
				float pad = height() / 40, rad = pad / 2;
				CanvasImage image = CanvasUtils.createRoundRect(writer.width() + pad * 2, writer.height() + pad * 2, rad, 
						Color.withAlpha(Colors.WHITE, 200), pad / 2, Colors.GRAY);
				image.canvas().translate(pad, pad);
				writer.drawExpression(image.canvas());
				equationLayer.setImage(image);
				PlayNObject.centerImageLayer(equationLayer);
				
				descriptionLayer.setTx(equationLayer.tx() - equationLayer.width() / 2 - descriptionLayer.width() * 1.1f);
			}
		});

		// game difficulty slider
		String[] gameDifficulties = new String[Difficulty.MAX_GAME_DIFFICULTY + 1];
		for (int i = 0; i < gameDifficulties.length; i++) gameDifficulties[i] = "" + (i+1);
		gameSlider = createSlideLyer(getString("game-difficulty"), getString("game-difficulty-desc"),
				2f * spacing + offY, gameDifficulties);
		
		// time difficulty slider
		String[] timeStops = new String[Difficulty.TIMES.length];
		timeStops[0] = Constant.INFINITY_SYMBOL;
		for (int i = 1; i < timeStops.length; i++) timeStops[i] = Difficulty.TIMES[i] + "s";
		timeSlider = createSlideLyer(getString("solve-time"), getString("solve-time-desc"),
				3f * spacing + offY, timeStops);
		
		// start sliders in the middle
		mathSlider.setStop(2, true);
		gameSlider.setStop(2, true);
		timeSlider.setStop(2, true);
		
		// add header buttons
		Button buttonOk = header.addRightButton(Constant.BUTTON_OK);
		registerHighlightable(buttonOk, Tag.Difficulty_Start);
		buttonOk.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					startGame();
				}
			}
		});
		
		Button buttonBack = header.addLeftButton(Constant.BUTTON_BACK);
		buttonBack.setNoSound();
		buttonBack.setOnReleasedListener(new OnReleasedListener() {
			@Override
			public void onRelease(Event event, boolean inButton) {
				if (inButton) {
					popThis();
				}
			}
		});
	}

	@Override
	protected void popThis() {
		Audio.se().play(Constant.SE_BACK);
		super.popThis();
	}
	
	@Override
	protected Trigger wasShownTrigger() {
		return Trigger.Difficulty_Shown;
	}
	
	private void startGame() {
		GameState state;
		if (Tutorial.running()) {
			// start a special GameState for the tutorial
			state = new TutorialGameState();
		} else {
			Difficulty difficulty = new Difficulty(mathSlider.stop(), gameSlider.stop(), Difficulty.TIMES[timeSlider.stop()]);
			state = new GameState(difficulty);
		}
		state.setBackground(background);
		
		DefenseScreen ds = new DefenseScreen(screens, state);
		pushScreen(ds, screens.slide().down());
		// remove this screen from the stack - going back should lead to the TitleScreen
		screens.remove(this);
		Audio.bg().play(Constant.BG_GAME1);
	}
	
	// convenience method for creating/sizing/positioning sliders with a prompt text
	private DiscreteSlideLayer createSlideLyer(String prompt, String description, float height, String... stops) {
		ImageLayer promptLayer = graphics().createImageLayer();
		promptLayer.setImage(CanvasUtils.createTextCached(prompt, promptFormat, Colors.WHITE));
		promptLayer.setTranslation(width() * 0.025f, height - promptLayer.height() / 2);
		
		float maxPromptWidth = width() * 0.2f;
		if (promptLayer.width() > maxPromptWidth) {
			promptLayer.setScale(maxPromptWidth / promptLayer.width());
		}
		
		layer.add(promptLayer);
		
		if (description != null) {
			ImageLayer descriptionLayer = graphics().createImageLayer();
			descriptionLayer.setImage(CanvasUtils.createTextCached(description, descriptionFormat, Colors.WHITE));
			descriptionLayer.setTranslation(width() * 0.6f, height + spacing / 2);
			PlayNObject.centerImageLayer(descriptionLayer);
			layer.add(descriptionLayer);
		}
		
		DiscreteSlideLayer slider = new DiscreteSlideLayer(width() * 0.7f, 
				height() / 6, background.primaryColor(), stops);
		slider.centerLayer();
		slider.setTranslation(width() * 0.6f, height);
		
		layer.add(slider.layerAddable());
		slideLayers.add(slider);
		return slider;
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		header.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		for (DiscreteSlideLayer slide : slideLayers) {
			slide.paint(clock);
		}
	}

}
