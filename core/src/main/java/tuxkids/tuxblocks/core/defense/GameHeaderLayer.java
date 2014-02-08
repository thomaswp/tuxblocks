package tuxkids.tuxblocks.core.defense;

import java.util.ArrayList;
import java.util.List;

import playn.core.Color;
import playn.core.Font;
import playn.core.Font.Style;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.TextFormat;
import playn.core.util.Clock;
import pythagoras.f.FloatMath;
import tripleplay.util.Colors;
import tuxkids.tuxblocks.core.Audio;
import tuxkids.tuxblocks.core.Lang;
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.Stat;
import tuxkids.tuxblocks.core.defense.round.Round;
import tuxkids.tuxblocks.core.layers.ImageLayerTintable;
import tuxkids.tuxblocks.core.layers.LayerLike;
import tuxkids.tuxblocks.core.layers.LayerWrapper;
import tuxkids.tuxblocks.core.screen.GameScreen;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.tutorial.Highlightable;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;
import tuxkids.tuxblocks.core.utils.CanvasUtils;
import tuxkids.tuxblocks.core.widget.Button;
import tuxkids.tuxblocks.core.widget.HeaderLayer;

/**
 * Special {@link HeaderLayer} displayed during the game. Contains a number
 * of widgets that can be optionally displayed, depending on the screen. These
 * include:
 * <ul>
 * <li> {@link Bar}s, created with the {@link GameHeaderLayer#createBars()} method
 * <li> {@link Heart}, created with the {@link GameHeaderLayer#createHeart()} method
 * <li> {@link Score}, created with the {@link GameHeaderLayer#createScore()} method
 * <li> {@link Timer}, created with the {@link GameHeaderLayer#createTimer()} method
 * <li> {@link Upgrade}, created with the {@link GameHeaderLayer#createUpgrades()} method
 * </ul>
 */
public abstract class GameHeaderLayer extends HeaderLayer {

	private final static int BEAT_TIME = 300; // ms that a pulse lasts when a value changes
	private final static float ITEM_ALPHA = (1 + Button.DEFAULT_UNPRESSED_ALPHA) / 2;
	private final static int ITEM_TEXT_COLOR = Colors.BLACK;
	
	private final TextFormat barNumberFormat, barTextFormat, scoreTextFormat;
	private final int itemSize;
	private final GameState state;
	private final List<Widget> widgets = new ArrayList<GameHeaderLayer.Widget>();
	private final float barCenter, itemCenter;
	private final GameScreen parent;

	private Bar[] bars;
	private Heart heart;
	private Score score;
	private Timer timer;
	private Upgrade upgrade;
	
	private interface Widget {
		void update(int delta);
		void paint(Clock clock);
	}
	
	/** Use this method to create your desired widgets from the list in {@link GameHeaderLayer}. */
	protected abstract void createWidgets();
	
	public GameHeaderLayer(GameScreen parent, float width) {
		super(width, parent.state().themeColor());
		state = parent.state();
		this.parent = parent;
		
		barNumberFormat = new TextFormat().withFont(
				graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, height * 0.18f));
		barTextFormat = new TextFormat().withFont(
				graphics().createFont(Lang.font(), Style.PLAIN, barNumberFormat.font.size()));
		scoreTextFormat = new TextFormat().withFont(
				graphics().createFont(Constant.NUMBER_FONT, Style.PLAIN, height * 0.4f));
		itemSize = (int) (2 * height / 3);
		
		barCenter = width * 0.26f;
		itemCenter = width * 0.74f;
		
		createWidgets();
	}

	/**
	 * Creates all widgets listed in {@link GameHeaderLayer}.
	 */
	protected void createAll() {
		createBars();
		createHeart();
		createTimer();
		createUpgrades();
		createScore();
	}
	
	/** Creates a {@link Score} widget and adds it to this header */
	protected void createScore() {
		score = new Score(height);
		score.setTranslation(width / 2, 0);
		layer.add(score.layerAddable());
		widgets.add(score);
	}
	
	/** Creates an {@link Upgrade} widget and adds it to this header */
	protected void createUpgrades() {
		upgrade = new Upgrade(itemSize, itemSize);
		upgrade.setTranslation(itemCenter - itemSize * 4 / 3, 
				height / 2);
		layer.add(upgrade.layerAddable());
		parent.registerHighlightable(upgrade, Tag.Menu_Upgrades);
		widgets.add(upgrade);
	}
	
	/** Creates a {@link Timer} widget and adds it to this header */
	protected void createTimer() {
		timer = new Timer(itemSize, itemSize);
		timer.setTranslation(itemCenter + itemSize * 4 / 3, 
				height / 2);
		layer.add(timer.layerAddable());
		parent.registerHighlightable(timer, Tag.Menu_Countdown);
		widgets.add(timer);
	}
	
	/** Creates a {@link Heart} widget and adds it to this header */
	protected void createHeart() {
		heart = new Heart(itemSize, itemSize);
		heart.setTranslation(itemCenter, 
				height / 2);
		layer.add(heart.layerAddable());
		parent.registerHighlightable(heart, Tag.Menu_Lives);
		widgets.add(heart);
	}
	
	/** Creates 4 {@link Bar} widgets and adds them to this header */
	protected void createBars() {
		bars = new Bar[4];
		int index = 0;
		float barWidth = width / 4;
		float barHeight = height / 6;
		float barIndent = barCenter - barWidth / 2;
		for (Stat stat : Stat.values()) {
			Bar bar = new Bar(stat, barWidth, barHeight);
			int row = index;
			int col = 0;
			bar.setTranslation(barIndent + barWidth * col, height / 5 * (row + 0.5f));
			layer.add(bar.layerAddable());
			widgets.add(bar);
			
			bars[index++] = bar;
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		for (Widget widget : widgets) {
			if (widget != null) widget.update(delta);
		}
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		for (Widget widget : widgets) {
			if (widget != null) widget.paint(clock);
		}
	}
	
	// used to update the size of layers that "beat" when a value changes
	private void updateBeatSize(int ms, Layer layer) {
		if (ms > 0) {
			float perc = (float)ms / BEAT_TIME;
			float scale = 1 + 0.3f * FloatMath.sin(perc * FloatMath.PI);
			layer.setScale(scale);
		} else {
			layer.setScale(1f);
		}
	}
	
	// used to update the alpha of layers that "beat" when a value changes
	private void updateBeatAlpha(int ms, LayerLike layer) {
		if (ms > 0) {
			float perc = (float)ms / BEAT_TIME;
			layer.setAlpha(lerp(ITEM_ALPHA, 1, perc));
		} else {
			layer.setAlpha(ITEM_ALPHA);
		}
	}
	
	// allows the Widgets to be Highlightable in the Tutorial
	private abstract class HighlightableWidget extends LayerWrapper 
	implements  Highlightable, Widget {

		protected abstract ImageLayerTintable highlightLayer();
		
		protected final Highlighter highlighter = new Highlighter() {
			@Override
			protected void setTint(int baseColor, int tintColor, float perc) {
				highlightLayer().setTint(baseColor, tintColor, perc);
			}
			
			@Override
			protected ColorState colorState() {
				return new ColorState() {
					@Override
					public void reset() {
						highlightLayer().setTint(state.themeColor());
					}
				};
			}
		};
		
		public HighlightableWidget(Layer layer) {
			super(layer);
		}

		@Override
		public Highlighter highlighter() {
			return highlighter;
		}
	}
	
	/** Widget that displays the number of Upgrades the player has */
	private class Upgrade extends HighlightableWidget {

		private final GroupLayer layer;
		private final ImageLayerTintable plusLayer;
		private final ImageLayer numberLayer;
		
		private int upgrades = -1;
		private int beatMS;
		
		public Upgrade(int width, int height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			
			plusLayer = new ImageLayerTintable();
			plusLayer.setImage(assets().getImage(Constant.IMAGE_UPGRADE));
			plusLayer.setTint(state.themeColor());
			plusLayer.setSize(width, height);
			plusLayer.setAlpha(ITEM_ALPHA);
			centerImageLayer(plusLayer);
			layer.add(plusLayer.layerAddable());
			
			numberLayer = graphics().createImageLayer();
			numberLayer.setDepth(1);
			layer.add(numberLayer);
			
			update(0);
		}
		
		public void update(int delta) {
			int m = state.upgrades();
			if (m != upgrades) {
				upgrades = m;
				String text = "" + upgrades;
				numberLayer.setImage(CanvasUtils.createText(
						text, barNumberFormat, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				beatMS = BEAT_TIME; // start the "beat"
			}
		}
		
		public void paint(Clock clock) {
			updateBeatSize(beatMS, layer);
			updateBeatAlpha(beatMS, plusLayer);
			if (beatMS > 0) beatMS -= clock.dt();
		}

		@Override
		protected ImageLayerTintable highlightLayer() {
			return plusLayer;
		}
	}
	
	/** Widget that shows the amount of time until the next {@link Round} */
	private class Timer extends HighlightableWidget {

		private final static int DURING_ROUND = Difficulty.ROUND_TIME_INFINITE - 1;
		
		private final GroupLayer layer;
		private final ImageLayerTintable hourglassLayer;
		private final ImageLayer numberLayer;
		
		private int time = DURING_ROUND;
		private int beatMS;
		
		public Timer(int width, int height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			
			hourglassLayer = new ImageLayerTintable();
			hourglassLayer.setImage(assets().getImage(Constant.IMAGE_HOURGLASS));
			hourglassLayer.setTint(state.themeColor());
			hourglassLayer.setSize(width, height);
			centerImageLayer(hourglassLayer);
			layer.add(hourglassLayer.layerAddable());
			
			numberLayer = graphics().createImageLayer();
			numberLayer.setDepth(1);
			numberLayer.setTx(-2); //weird centering correction..
			layer.add(numberLayer);
			
			update(0);
		}
		
		public void update(int delta) {
			int nextRound; // time until next round (or a special state indicator if negative)
			boolean beat = true; // do we beat this frame?
			if (state.level().duringRound()) {
				nextRound = DURING_ROUND; // special value to indicate the middle of a round
			} else {
				// could return -1 to indicate infinite time
				nextRound = state.level().timeUntilNextRound();
				if (nextRound > 0) nextRound /= 1000; // convert ms -> seconds
				beat = nextRound <= 5; // beat for the last 5 seconds
			}
			if (nextRound != time) { // if our state changed
				time = nextRound;
				String text;
				TextFormat tf = barNumberFormat;
				if (time == Difficulty.ROUND_TIME_INFINITE) {
					text = Constant.INFINITY_SYMBOL; // infinite time
				} else if (time == DURING_ROUND) {
					text = Lang.getString("menu","round") +" "+state.level().roundNumber(); // during round
					tf = barTextFormat;
				} else {
					text = "" + time; // waiting for next round
				}
				
				if (time == Difficulty.ROUND_TIME_INFINITE) {
					// double the size for the infinity because its such a tiny character
					Font font = Cache.getFont(tf.font.name(), tf.font.style(), tf.font.size() * 2f);
					tf = tf.withFont(font);
				}
				
				numberLayer.setImage(CanvasUtils.createText(text, tf, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				
				if (beat) {
					beatMS = BEAT_TIME; // start a beat
					if (time != DURING_ROUND && time != Difficulty.ROUND_TIME_INFINITE) {
						Audio.se().play(Constant.SE_PITCH);
					}
				}
			}
		}
		
		public void paint(Clock clock) {
			updateBeatSize(beatMS, layer);
			if (beatMS > 0) {
				beatMS -= clock.dt();
				updateBeatAlpha(beatMS, hourglassLayer);
			} else {
				// if during a round, fade in/out the hourglass image
				if (state.level().duringRound()) {
					hourglassLayer.setAlpha(lerpTime(hourglassLayer.alpha(), 
							0, 0.995f, clock.dt(), 0.01f));
				} else {
					hourglassLayer.setAlpha(lerpTime(hourglassLayer.alpha(), 
							ITEM_ALPHA, 0.995f, clock.dt(), 0.01f));
				}
			}
		}

		@Override
		protected ImageLayerTintable highlightLayer() {
			return hourglassLayer;
		}
	}
	
	/**
	 * Widget for displaying the player's score
	 */
	private class Score extends LayerWrapper implements Widget {

		private final static int BEAT_TIME = 300;
		
		private final GroupLayer layer;
		private final ImageLayer scoreLayer;
		
		private int score = -1;
		private int beatMS;
		
		public Score(float height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			
			scoreLayer = graphics().createImageLayer();
			scoreLayer.setTy(height / 2);
			layer.add(scoreLayer);
		}
		
		public void update(int delta) {
			if (score != state.score()) {
				score = state.score();
				scoreLayer.setImage(CanvasUtils.createText(
						"" + score, scoreTextFormat, Colors.BLACK));
				centerImageLayer(scoreLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		public void paint(Clock clock) {
			updateBeatSize(beatMS, scoreLayer);
			if (beatMS > 0) beatMS -= clock.dt();
		}
	}

	/**
	 * Widget for displaying the player's current lives
	 */
	private class Heart extends HighlightableWidget {
		private final static int BEAT_TIME = 300;

		private final GroupLayer layer;
		private final ImageLayerTintable heartLayer;
		private final ImageLayer numberLayer;
		
		private int lives = -1;
		private int beatMS;
		
		public Heart(int width, int height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			
			heartLayer = new ImageLayerTintable();
			heartLayer.setImage(assets().getImage(Constant.IMAGE_HEART));
			heartLayer.setTint(state.themeColor());
			heartLayer.setSize(width, height);
			centerImageLayer(heartLayer);
			layer.add(heartLayer.layerAddable());
			
			numberLayer = graphics().createImageLayer();
			numberLayer.setDepth(1);
			layer.add(numberLayer);
			
			update(0);
		}
		
		public void update(int delta) {
			int l = state.lives();
			if (l != lives) {
				lives = l;
				numberLayer.setImage(CanvasUtils.createText(
						"" + lives, barNumberFormat, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		public void paint(Clock clock) {
			updateBeatSize(beatMS, layer);
			updateBeatAlpha(beatMS, heartLayer);
			if (beatMS > 0) beatMS -= clock.dt();
		}

		@Override
		protected ImageLayerTintable highlightLayer() {
			return heartLayer;
		}
	}
	
	/**
	 * Widget for displaying the player's experience for any
	 * of the given {@link Stat} values.
	 */
	private class Bar extends LayerWrapper implements Widget {

		private float TEXT_SPACE = barNumberFormat.font.size();
		
		private GroupLayer layer;
		private float width, height;
		private Stat stat;
		private ImageLayer symbolLayer, levelLayer, barBG, barFill;
		private int level = -1;
		private int color;
		private int strokeWidth;
		private float barPerc;
		private int beatMS;
		
		public Bar(Stat stat, float width, float height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			this.width = width;
			this.height = height;
			this.stat = stat;
			this.color = state.themeColor();
			
			// the stat's symbol
			symbolLayer = graphics().createImageLayer(CanvasUtils.createText(
					stat.symbol(), barNumberFormat, Colors.BLACK));
			symbolLayer.setTranslation((TEXT_SPACE - symbolLayer.width()) / 2, (height - symbolLayer.height()) / 2);
			layer.add(symbolLayer);
			
			levelLayer = graphics().createImageLayer();
			layer.add(levelLayer);
			
			// the "container" for the experience bar (this is static)
			strokeWidth = 3;
			barBG = graphics().createImageLayer(CanvasUtils.createRoundRectCached(width - TEXT_SPACE * 2, 
					height, height * 0.2f, Color.argb(0, 0, 0, 0), strokeWidth, Colors.BLACK));
			barBG.setTranslation(TEXT_SPACE, (height - barBG.height()) / 2);
			layer.add(barBG);
			
			// the "fill" of the experience bar (this changes sizes to indicate exp)
			barFill = graphics().createImageLayer(CanvasUtils.createRect(5, height - strokeWidth * 2, color));
			barFill.setTranslation(barBG.tx() + strokeWidth, barBG.ty() + strokeWidth);
			barFill.setDepth(-1);
			barFill.setAlpha(ITEM_ALPHA);
			layer.add(barFill);
			
			update(0);
		}
		
		public void update(int delta) {
			int l = state.getStatLevel(stat);
			if (level != l) {
				level = l;
				levelLayer.setImage(CanvasUtils.createText(
						"" + level, barNumberFormat, Colors.BLACK));
				levelLayer.setTranslation(width - TEXT_SPACE / 2, height / 2);
				centerImageLayer(levelLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		public void paint(Clock clock) {
			updateBeatSize(beatMS, levelLayer);
			if (beatMS > 0) beatMS -= clock.dt();

			float target = state.getStatPerc(stat);
			if (target < barPerc) target++; //causes wrap around
			barPerc = lerpTime(barPerc, target, 0.995f, clock.dt(), 0.01f);
			if (barPerc >= 1f) barPerc--; //causes wrap around

			barFill.setWidth(barPerc * (barBG.width() - strokeWidth * 2));
		}
		
	}
}
