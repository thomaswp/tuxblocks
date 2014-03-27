package tuxkids.tuxblocks.core.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import playn.core.Json;
import playn.core.PlayN;
import playn.core.util.Clock;
import tuxkids.tuxblocks.core.tutorial.Tutorial.Trigger;
import tuxkids.tuxblocks.core.utils.Debug;

abstract class FSMTutorial implements TutorialInstance {

	protected final State anyState = new State(null);
	protected final State endState = new State(null);

	private State startState = null;	
	private TutorialLayer layer;
	private State state = startState;
	private Json.Object messages;
	
	protected abstract void addStates();
	
	public FSMTutorial(int themeColor) {
		layer = new TutorialLayer(themeColor);
	}
	
	protected State addStartState(String id) {
		return (startState = addState(id));
	}
	
	protected State addState(String id) {
		String text = messages.getString(id);
		if (text == null) {
			Debug.write("WARNING: no such tutorial text with id: " + id);
			text = "";
		}
		text = Tutorial.prepareMessage(text);
		return new State(text);
	}
	
	@Override
	public void loadTextFile(String result) {
		messages = PlayN.json().parse(result);
		addStates();
		if (startState == null) {
			throw new RuntimeException("Must call addStartState()");
		}
		state = startState;
		if (state.message != null) layer.showMessage(state.message);
	}

	@Override
	public void paint(Clock clock) {
		layer.paint(clock);
	}

	@Override
	public void update(int delta) { }

	@Override
	public void trigger(Trigger event) {
		if (state == endState || state == null) return;
		
		State nextState = state.sawTrigger(event);
		
		if (nextState != null) {
			state = nextState;
			refreshHighlights();
			if (state.message != null) layer.showMessage(state.message);
		}
		if (nextState == endState) {
			destroy();
		}
	}

	@Override
	public void destroy() {
		layer.destroy();
	}

	@Override
	public void refreshHighlights() {
		Tutorial.refreshHighlights(new ArrayList<Tutorial.Tag>()); //TODO: correct list
	}

	@Override
	public void didLeaveScreen() {
		// screen left; message can possible be reshown
	}
	
	@Override
	public void wasRepeated() {
		// repeat button pressed, message reshown
	}
	
	protected class State {
		public final String message;
		public final List<Highlightable> highlightables = new ArrayList<Highlightable>();
		private final HashMap<Trigger, State> transitions = new HashMap<Trigger, State>();
		private State elseState;
		
		private State(String message) {
			this.message = message;
		}
		
		private State sawTrigger(Trigger event) {
			State nextState = transitions.get(event);
			if (nextState != null) {
				return nextState;
			} else if (nextState == null && elseState == null) {
				return anyState.transitions.get(event);
			}
			return elseState;
		}

		public State addHighlightable(Highlightable highlightable) {
			highlightables.add(highlightable);
			return this;
		}
		
		public State addTransition(State state, Trigger... triggers) {
			for(Trigger t: triggers) {
				transitions.put(t, state);
			}
			return this;
		}
		
		public State elseTransition(State state) {
			this.elseState = state;
			return this;
		}
	}

}
