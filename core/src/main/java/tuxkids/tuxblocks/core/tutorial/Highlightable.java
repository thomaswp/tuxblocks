package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.tutorial.Tutorial.Tag;

public interface Highlightable {
	Highlighter highlighter();
	
	public static abstract class Highlighter {
		protected boolean highlighted;
		protected final List<Tag> tags = new ArrayList<Tutorial.Tag>();
		
		private ColorState oldState;
		
		protected abstract ColorState colorState();
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

		public boolean hasTags(List<Tag> tags) {
			for (Tag tag : this.tags) {
				if (tags.contains(tag)) return true;
			}
			return false;
		}
	}
	
	public interface ColorState {
		void reset();
	}
}
