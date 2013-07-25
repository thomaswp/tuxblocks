package tuxkids.tuxblocks.core.solve.blocks.n.markup;

import playn.core.Canvas;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tripleplay.util.Colors;

public abstract class ExpressionWriter {
	
	protected final static Config DEFAULT_CONFIG = new Config();
	
	protected Config config = DEFAULT_CONFIG;
	protected Vector size;
	protected TextFormat textFormat;
	
	protected abstract Vector formatExpression(TextFormat textFormat);
	public abstract void drawExpression(Canvas canvas);
	
	public Vector blankCenter() {
		return null;
	}
	
	protected int blankColor() {
		return config.blankColor;
	}
	
	protected float spacing() {
		return textFormat.font.size() / 4;
	}
	
	protected void setColor(Canvas canvas, boolean highlight) {
		int color = highlight ? config.highlightColor : config.defaultColor;
		canvas.setFillColor(color);
		canvas.setStrokeColor(color);
	}
	
	public Vector getSize() {
		return size;
	}
	
	public float width() {
		return size.x;
	}
	
	public float height() {
		return size.y;
	}
	
	public ExpressionWriter(TextFormat textFormat) {
		this.textFormat = textFormat;
		this.size = formatExpression(textFormat);
	}
	
	public void drawExpression(Canvas canvas, Config config) {
		this.config = config;
		drawExpression(canvas);
	}
	
	protected TextLayout layout(String text, TextFormat textFormat) {
		return PlayN.graphics().layoutText(text, textFormat);
	}
	
	public static class Config {
		public int defaultColor, highlightColor, blankColor;
		
		public Config() {
			this(Colors.WHITE, Colors.RED, Colors.BLUE);
		}
		
		public Config(int defaultColor, int highlightColor, int blankcolor) {
			this.defaultColor = defaultColor;
			this.highlightColor = highlightColor;
			this.blankColor = blankcolor;
		}
	}
}
