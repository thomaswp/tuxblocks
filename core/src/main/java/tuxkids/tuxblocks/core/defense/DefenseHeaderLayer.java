package tuxkids.tuxblocks.core.defense;

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
import tuxkids.tuxblocks.core.Cache;
import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.GameState.Stat;
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

public class DefenseHeaderLayer extends HeaderLayer {

	private final static int BEAT_TIME = 300;
	private final static float ITEM_ALPHA = (1 + Button.UNPRESSED_ALPHA) / 2;
	private final static int ITEM_TEXT_COLOR = Colors.BLACK;
	
	private final TextFormat barTextFormat;
	private final TextFormat scoreTextFormat;
	private final int itemSize;
	private final GameState state;

	private Bar[] bars;
	private Heart heart;
	private Score score;
	private Timer timer;
	private Upgrade upgrade;
	
	public DefenseHeaderLayer(GameScreen parent, float width) {
		this(parent, width, true);
	}
	
	public DefenseHeaderLayer(GameScreen parent, float width, boolean showScore) {
		super(width, parent.state().themeColor());
		state = parent.state();
		
		barTextFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, height * 0.18f));
		scoreTextFormat = new TextFormat().withFont(
				graphics().createFont(Constant.FONT_NAME, Style.PLAIN, height * 0.4f));
		itemSize = (int) (2 * height / 3);
		
		float barCenter = width * 0.26f, itemCenter = width * 0.74f;

		createBars(barCenter);
		createHeart(itemCenter);
		createTimer(itemCenter);
		createUpgrade(itemCenter);
		createScore();
		if (!showScore) score.setVisible(false);
		
		parent.register(heart, Tag.Menu_Lives);
		parent.register(timer, Tag.Menu_Countdown);
		parent.register(upgrade, Tag.Menu_Upgrades);
	}
	
	private void createScore() {
		score = new Score(height);
		score.setTranslation(width / 2, 0);
		layer.add(score.layerAddable());
	}
	
	private void createUpgrade(float itemCenter) {
		upgrade = new Upgrade(itemSize, itemSize);
		upgrade.setTranslation(itemCenter - itemSize * 4 / 3, 
				height / 2);
		layer.add(upgrade.layerAddable());
	}
	
	private void createTimer(float itemCenter) {
		timer = new Timer(itemSize, itemSize);
		timer.setTranslation(itemCenter + itemSize * 4 / 3, 
				height / 2);
		layer.add(timer.layerAddable());
	}
	
	private void createHeart(float itemCenter) {
		heart = new Heart(itemSize, itemSize);
		heart.setTranslation(itemCenter, 
				height / 2);
		layer.add(heart.layerAddable());
	}
	
	private void createBars(float barCenter) {
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
//			int row = index / 2;
//			int col = index % 2;
//			bar.setTranslation(barIndent + barWidth * col, height / 5 * (row + 0.5f) * 2);
			layer.add(bar.layerAddable());
			
			bars[index++] = bar;
		}
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		for (Bar bar : bars) {
			bar.update(delta);
		}
		heart.update(delta);
		score.update(delta);
		timer.update(delta);
		upgrade.update(delta);
	}
	
	@Override
	public void paint(Clock clock) {
		super.paint(clock);
		for (Bar bar : bars) {
			bar.paint(clock);
		}
		heart.paint(clock);
		score.paint(clock);
		timer.paint(clock);
		upgrade.paint(clock);
	}
	
	private void updateBeat(int ms, Layer layer) {
		if (ms > 0) {
			float perc = (float)ms / BEAT_TIME;
			float scale = 1 + 0.3f * FloatMath.sin(perc * FloatMath.PI);
			layer.setScale(scale);
		} else {
			layer.setScale(1f);
		}
	}
	
	private void updateAlpha(int ms, LayerLike layer) {
		if (ms > 0) {
			float perc = (float)ms / BEAT_TIME;
			layer.setAlpha(lerp(ITEM_ALPHA, 1, perc));
		} else {
			layer.setAlpha(ITEM_ALPHA);
		}
	}
	
	private abstract class LayerWrapperHighlightable extends LayerWrapper implements  Highlightable {

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
		
		public LayerWrapperHighlightable(Layer layer) {
			super(layer);
		}

		@Override
		public Highlighter highlighter() {
			return highlighter;
		}
	}
	
	private class Upgrade extends LayerWrapperHighlightable {

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
		
		private void update(int delta) {
			int m = state.upgrades();
			if (m != upgrades) {
				upgrades = m;
				String text = "" + upgrades;
				numberLayer.setImage(CanvasUtils.createString(
						barTextFormat, text, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		private void paint(Clock clock) {
			updateBeat(beatMS, layer);
			updateAlpha(beatMS, plusLayer);
			if (beatMS > 0) beatMS -= clock.dt();
//			if (state.upgrades() == 0) {
//				plusLayer.setAlpha(plusLayer.alpha() * 0.5f);
//			}
		}

		@Override
		protected ImageLayerTintable highlightLayer() {
			return plusLayer;
		}
	}
	
	private class Timer extends LayerWrapperHighlightable {

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
		
		private void update(int delta) {
			int l;
			boolean beat = true;
			if (state.level().duringRound()) {
				l = DURING_ROUND;
			} else {
				l = state.level().timeUntilNextRound();
				if (l > 0) l /= 1000;
				beat = l <= 5;
			}
			if (l != time) {
				time = l;
				String text;
				if (time == Difficulty.ROUND_TIME_INFINITE) {
					text = Constant.INFINITY_SYMBOL;
				} else if (time == DURING_ROUND) {
					text = "Round " + state.level().roundNumber();
				} else {
					text = "" + time;
				}
				TextFormat tf = barTextFormat;
				if (time == Difficulty.ROUND_TIME_INFINITE) {
					Font font = Cache.getFont(tf.font.name(), tf.font.style(), tf.font.size() * 2f);
					tf = tf.withFont(font);
				}
				numberLayer.setImage(CanvasUtils.createString(
						tf, text, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				if (beat) {
					beatMS = BEAT_TIME;
					if (time != DURING_ROUND && time != Difficulty.ROUND_TIME_INFINITE) {
						Audio.se().play(Constant.SE_PITCH);
					}
				}
			}
		}
		
		private void paint(Clock clock) {
			updateBeat(beatMS, layer);
			if (beatMS > 0) {
				beatMS -= clock.dt();
				updateAlpha(beatMS, hourglassLayer);
			} else {
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
	
	private class Score extends LayerWrapper {

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
				scoreLayer.setImage(CanvasUtils.createString(
						scoreTextFormat, "" + score, Colors.BLACK));
				centerImageLayer(scoreLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		public void paint(Clock clock) {
			updateBeat(beatMS, scoreLayer);
			if (beatMS > 0) beatMS -= clock.dt();
		}
	}

	private class Heart extends LayerWrapperHighlightable {
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
		
		private void update(int delta) {
			int l = state.lives();
			if (l != lives) {
				lives = l;
				numberLayer.setImage(CanvasUtils.createString(
						barTextFormat, "" + lives, ITEM_TEXT_COLOR));
				centerImageLayer(numberLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		private void paint(Clock clock) {
			updateBeat(beatMS, layer);
			updateAlpha(beatMS, heartLayer);
			if (beatMS > 0) beatMS -= clock.dt();
		}

		@Override
		protected ImageLayerTintable highlightLayer() {
			return heartLayer;
		}
	}
	
	private class Bar extends LayerWrapper {

		private float TEXT_SPACE = barTextFormat.font.size();
		
		private GroupLayer layer;
		private float width, height;
		private Stat stat;
		private ImageLayer symbolLayer, levelLayer, barBG, barFill;
		private int level = -1;
		private int color;
		private int strokWidth;
		private float barPerc;
		private int beatMS;
		
		public Bar(Stat stat, float width, float height) {
			super(graphics().createGroupLayer());
			layer = (GroupLayer) layerAddable();
			this.width = width;
			this.height = height;
			this.stat = stat;
			this.color = state.themeColor();
			
			symbolLayer = graphics().createImageLayer(CanvasUtils.createString(
					barTextFormat, stat.symbol(), Colors.BLACK));
			symbolLayer.setTranslation((TEXT_SPACE - symbolLayer.width()) / 2, (height - symbolLayer.height()) / 2);
			layer.add(symbolLayer);
			
			levelLayer = graphics().createImageLayer();
			layer.add(levelLayer);
			
			strokWidth = 3;
			barBG = graphics().createImageLayer(CanvasUtils.createRoundRectCached(width - TEXT_SPACE * 2, 
					height, height * 0.2f, Color.argb(0, 0, 0, 0), strokWidth, Colors.BLACK));
			barBG.setTranslation(TEXT_SPACE, (height - barBG.height()) / 2);
			layer.add(barBG);
			
			barFill = graphics().createImageLayer(CanvasUtils.createRect(5, height - strokWidth * 2, color));
			barFill.setTranslation(barBG.tx() + strokWidth, barBG.ty() + strokWidth);
			barFill.setDepth(-1);
			barFill.setAlpha(ITEM_ALPHA);
			layer.add(barFill);
			
			update(0);
		}
		
		private void update(int delta) {
			int l = state.getStatLevel(stat);
			if (level != l) {
				level = l;
				levelLayer.setImage(CanvasUtils.createString(
						barTextFormat, "" + level, Colors.BLACK));
				levelLayer.setTranslation(width - TEXT_SPACE / 2, height / 2);
				centerImageLayer(levelLayer);
				beatMS = BEAT_TIME;
			}
		}
		
		public void paint(Clock clock) {
			updateBeat(beatMS, levelLayer);
			if (beatMS > 0) beatMS -= clock.dt();

			float target = state.getStatPerc(stat);
			if (target < barPerc) target++; //causes wrap around
			barPerc = lerpTime(barPerc, target, 0.995f, clock.dt(), 0.01f);
			if (barPerc > 0.99f) barPerc--; //causes wrap around

			barFill.setWidth(barPerc * (barBG.width() - strokWidth * 2));
		}
		
	}
}
