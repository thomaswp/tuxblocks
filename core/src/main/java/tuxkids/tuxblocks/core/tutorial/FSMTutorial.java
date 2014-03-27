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
	private static final Object NO_EXTRA_INFO = new Object();
	
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
		this.trigger(event, NO_EXTRA_INFO);
	}
	
	@Override
	public void trigger(Trigger event, Object extraInformation) {
		if (state == endState || state == null) return;
		
		State nextState = state.sawTrigger(event, extraInformation);
		
		if (nextState != null) {
			state = nextState;
			refreshHighlights();
			if (state.message != null) layer.showMessage(state.message);
			state = state.notifyMessageShown();
		}
		if (nextState == endState) {
			endOfTutorial();
		}
	}

	protected void endOfTutorial() {
		destroy();
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
	
	protected interface StateChooser {

		State chooseState(Object extraInformation);

		
	}
	
	private class DefaultStateChooser implements StateChooser {
		private State state;

		public DefaultStateChooser(State state) {
			this.state = state;
		}

		@Override
		public State chooseState(Object extraInformation) {
			return state;
		}
		
	}
	
	protected class State {
		public final String message;
		public final List<Highlightable> highlightables = new ArrayList<Highlightable>();
		private final HashMap<Trigger, StateChooser> transitions = new HashMap<Trigger, StateChooser>();
		private State elseState;
		private State epsilonState;
		
		//for subclassing.  Should call addState();
		protected State(String message) {
			this.message = message;
		}

		private State sawTrigger(Trigger trigger, Object extraInformation) {
				
			StateChooser nextState = transitions.get(trigger);
			if (nextState != null) {
				return nextState.chooseState(extraInformation);
			} else if (nextState == null && elseState == null && anyState.transitions.containsKey(trigger)) {
				return anyState.transitions.get(trigger).chooseState(extraInformation);
			}
			return elseState;
		}

		public State addHighlightable(Highlightable highlightable) {
			highlightables.add(highlightable);
			return this;
		}
		
		public State addTransition(State state, Trigger... triggers) {
			for(Trigger t: triggers) {
				StateChooser sc = transitions.get(t);
				if (sc == null) {
					transitions.put(t, new DefaultStateChooser(state));
				} else {
					Debug.write("Warning: Multiple states from this trigger.  Make a stateChooser instead.");
				}
			}
			return this;
		}
		
		public State addTransition(StateChooser stateChooser, Trigger trigger) {
			StateChooser sc = transitions.get(trigger);
			if (sc == null) {
				transitions.put(trigger, stateChooser);
			}
			else {
				Debug.write("You can't chain stateChoosers.  Make one combined stateChooser per action/trigger");
			}
	
			return this;
		}
		
		
		public State elseTransition(State state) {
			this.elseState = state;
			return this;
		}

		public void registerEpsilonTransition(State epsilonState) {
			if (this.epsilonState == null) {
				this.epsilonState = epsilonState;
			}
			else {
				Debug.write("You can only register one epsilon state");
			}
		}
		
		/**
		 * Allows this state to update after its message has been shown.
		 * 
		 * The default implementation is an epsilon transition (if one has 
		 * been registered), but subclasses could do things like trigger UI events
		 * @return
		 */
		public State notifyMessageShown() {
			if (this.epsilonState == null) {
				return this;
			}
			Debug.write("Epsilon transition to "+epsilonState);
			return epsilonState;
		}
	}

}
