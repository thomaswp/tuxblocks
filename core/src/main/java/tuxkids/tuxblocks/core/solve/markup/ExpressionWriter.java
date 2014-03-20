package tuxkids.tuxblocks.core.solve.markup;

import playn.core.Canvas;
import playn.core.Graphics;
import playn.core.PlayN;
import playn.core.TextFormat;
import playn.core.TextLayout;
import pythagoras.f.Vector;
import tripleplay.util.Colors;

/**
 * Draws a mathematical expression to a {@link Canvas}.
 * These are created using {@link Renderer#getExpressionWriter(TextFormat)}.
 * While {@link Renderer}s define the expressions relative positions,
 * the {@link ExpressionWriter}s also include font and color information.
 */
public abstract class ExpressionWriter {
	
	protected final static Config DEFAULT_CONFIG = new Config();
	
	protected Config config = DEFAULT_CONFIG;
	protected final Vector size;
	protected final TextFormat textFormat;
	
	protected abstract Vector formatExpression(TextFormat textFormat);
	public abstract void drawExpression(Canvas canvas);
	
	public Vector blankCenter() {
		return null;
	}
	
	/** The color to use when drawing a blank in the expression */
	protected int blankColor() {
		return config.blankColor;
	}
	
	/** The approximate width of a "space" character */
	protected float spacing() {
		return textFormat.font.size() / 4;
	}
	
	/** Sets the Canvas' color to the appropriate color given a term's highlight */
	protected void setColor(Canvas canvas, boolean highlight) {
		int color = highlight ? config.highlightColor : config.defaultColor;
		canvas.setFillColor(color);
		canvas.setStrokeColor(color);
	}
	
	/** Returns the size (in pixels) of this expression when drawn to Canvas */
	public Vector getSize() {
		return size;
	}
	
	/** Returns the width (in pixels) of this expression when drawn to Canvas */
	public float width() {
		return size.x;
	}
	
	/** Returns the height (in pixels) of this expression when drawn to Canvas */
	public float height() {
		return size.y;
	}
	
	protected ExpressionWriter(TextFormat textFormat) {
		this.textFormat = textFormat;
		this.size = formatExpression(textFormat);
	}
	
	/** Draws this expression to the given Canvas, using the provided Config */
	public void drawExpression(Canvas canvas, Config config) {
		this.config = config;
		drawExpression(canvas);
	}
	
	/** Shortcut to {@link Graphics#layoutText(String, TextFormat)} */
	protected TextLayout layout(String text, TextFormat textFormat) {
		return PlayN.graphics().layoutText(text, textFormat);
	}
	
	/**
	 * Represents a color configuration for an {@link ExpressionWriter}.
	 */
	public static class Config {
		public final int defaultColor, highlightColor, blankColor;
		
		public Config() {
			this(Colors.BLACK, Colors.RED, Colors.BLUE);
		}
		
		/**
		 * Creates a Config with the following
		 * @param defaultColor The color for general text
		 * @param highlightColor The color highlighted text
		 * @param blankcolor The color for the blank space in an expression
		 */
		public Config(int defaultColor, int highlightColor, int blankcolor) {
			this.defaultColor = defaultColor;
			this.highlightColor = highlightColor;
			this.blankColor = blankcolor;
		}
	}
	
	public static ExpressionWriter NOOP = new ExpressionWriter(null) {
		@Override
		protected Vector formatExpression(TextFormat textFormat) {
			return new Vector();
		}

		@Override
		public void drawExpression(Canvas canvas) { }
	};
}
