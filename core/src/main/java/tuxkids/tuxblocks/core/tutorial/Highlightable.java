package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;

/**
 * An interface for on-screen objects which can be highlighted
 * during the tutorial. Must provide a {@link Highlighter}.
 */
public interface Highlightable {
	Highlighter highlighter();
	
	/** A class for causing an on-screen {@link Highlightable} to highlight. */
	public static abstract class Highlighter {
		protected boolean highlighted;
		protected final List<Tag> tags = new ArrayList<Tutorial.Tag>();
		
		private ColorState oldState;
		
		protected abstract ColorState colorState();
		/** Highlight the associated {@link Highlightable} with the given colors. */
		protected abstract void setTint(int baseColor, int tintColor, float perc);
		
		public boolean highlighted() {
			return highlighted;
		}

		public void setHighlighted(boolean highlight) {
			if (highlight == highlighted) return;
			this.highlighted = highlight;
			if (highlight) {
				oldState = colorState();
			} else {
				oldState.reset();
			}
		}

		public void addTag(Tag tag) {
			if (!tags.contains(tag)) tags.add(tag);
		}

		/** Returns true if this Highlighter has any of the given tags */
		public boolean hasTags(List<Tag> tags) {
			for (Tag tag : this.tags) {
				if (tags.contains(tag)) return true;
			}
			return false;
		}
	}
	
	/**
	 * Represents a color state for a {@link Highlightable} object.
	 * When {@link ColorState#reset()} is called, the objects should be
	 * back to it original state, before {@link Highlighter#setTint(int, int, float)}
	 * was called.
	 */
	public interface ColorState {
		/** See {@link ColorState} */
		void reset();
	}
}
