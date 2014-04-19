package tuxkids.tuxblocks.core.tutor;

import java.util.ArrayList;
import java.util.List;

import tuxkids.tuxblocks.core.Constant;
import tuxkids.tuxblocks.core.lang.Lang;
import tuxkids.tuxblocks.core.lang.Strings_Hint;
import tuxkids.tuxblocks.core.solve.action.DragAction;
import tuxkids.tuxblocks.core.solve.action.FinishSimplifyAction;
import tuxkids.tuxblocks.core.solve.action.ReciprocalAction;
import tuxkids.tuxblocks.core.solve.action.SolveAction;
import tuxkids.tuxblocks.core.solve.blocks.BaseBlock;
import tuxkids.tuxblocks.core.solve.blocks.Block;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationBlockIndex;
import tuxkids.tuxblocks.core.solve.blocks.HorizontalModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.ModifierBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.tutor.IdealEquationSolver.Step;
import tuxkids.tuxblocks.core.utils.Debug;
import tuxkids.tuxblocks.core.utils.Formatter;

public class Tutor implements Strings_Hint {

	public static final int MAX_HINT_ITERATIONS = 100;
	
	public enum HintLevel {
		Vague, Specific, BottomOut
	}
	
	private Equation lastHintEquation;
	private HintLevel lastHintLevel = HintLevel.Vague;

	public static class Hint {
		public final String text;
		public final SolveAction action;
		public final HintLevel level;
		public final List<EquationBlockIndex> highlights = new ArrayList<EquationBlockIndex>();
		
		public final static String DOMAIN = "hint";

		public Hint(String key) {
			this(null, null, key);
		}
		
		public Hint(SolveAction action, HintLevel level, String key, Object... args) {
			this.action = action;
			this.level = level;
			this.text = Formatter.format(Lang.getString(DOMAIN, key), args);
		}
		
		public Hint addHighlights(EquationBlockIndex... indices) {
			for (EquationBlockIndex index : indices) {
				highlights.add(index);
			}
			return this;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	public Hint getHint(Equation equation) {
		List<Step> solution = IdealEquationSolver.aStar(equation, 100);
		if (solution == null) return new Hint(key_stumped);
		if (solution.size() < 2) return new Hint(key_solved);
		
		if (lastHintEquation != null && 
				equation.getPlainText().equals(lastHintEquation.getPlainText())) {
			if (lastHintLevel != HintLevel.BottomOut) {
				lastHintLevel = HintLevel.values()[lastHintLevel.ordinal() + 1];
			}
		} else {
			lastHintLevel = HintLevel.Vague;
			lastHintEquation = equation.copy();
		}
		
		return getHint(equation, solution.get(1).actions.get(0), lastHintLevel);
	}

	private Hint getHint(Equation equation, SolveAction action, HintLevel level) {
		if (action instanceof DragAction) {
			return getDragHint(equation, (DragAction) action, level);
		} else if (action instanceof FinishSimplifyAction) {
			return getSimplifyHint(equation, (FinishSimplifyAction) action, level);
		} else if (action instanceof ReciprocalAction) {
			return getReciprocalHint(equation, (ReciprocalAction) action, level);
		}
		Debug.write("Unkown action: " + action);
		return null;
	}
	
	private Hint getDragHint(Equation equation, DragAction action, HintLevel level) {
		Block dragging = equation.getBlock(action.fromIndex);
		BaseBlock dragTo = equation.getBaseBlock(action.toIndex);
		
		if (level == HintLevel.Vague) {
			String text;
			if (dragging instanceof VariableBlock) {
				if (dragTo instanceof VariableBlock) {
					text = key_dragVagueVariables;
				} else {
					text = key_dragVagueOneVariable;
				}
			} else if (dragging instanceof HorizontalModifierBlock) {
				text = key_dragVagueHorizontal;
			} else {
				text = key_dragVagueVertical;
			}
			return new Hint(action, level, text);
		}
		
		if (level == HintLevel.Specific) {
			return new Hint(action, level, key_dragSpecific, dragging.toString())
			.addHighlights(action.fromIndex);
		}
		
		return new Hint(action, level, key_dragBottomOut, dragging.toString(), dragTo.toString())
		.addHighlights(action.fromIndex, EquationBlockIndex.fromBaseBlockIndex(action.toIndex));
	}
	
	private Hint getSimplifyHint(Equation equation, FinishSimplifyAction action, HintLevel level) {
		if (level == HintLevel.Vague) {
			return new Hint(action, level, key_simplifyVague);
		}
		
		Block base = equation.getBlock(action.baseIndex);
		ModifierBlock pair = (ModifierBlock) equation.getBlock(action.pairIndex);
		
		boolean cancel = false;
		if (base instanceof ModifierBlock) {
			if (((ModifierBlock) base).value() == pair.value()) cancel = true;
		}
		
		if (level == HintLevel.Specific) {
			if (!cancel) {
				return new Hint(action, level, key_simplifySpecificSimplify, base.toString())
				.addHighlights(action.baseIndex);
			} else {
				return new Hint(action, level, key_simplifySpecificCancel);
			}
		}
		
		if (!cancel) {
			return new Hint(action, level, key_simplifyBottomOutSimplify, base.toString(), pair.toString())
			.addHighlights(action.baseIndex, action.pairIndex);
		} else {
			return new Hint(action, level, key_simplifyBottomOutCancel, base.toString(), pair.toString())
			.addHighlights(action.baseIndex, action.pairIndex);
		}
	}
	
	private Hint getReciprocalHint(Equation equation, ReciprocalAction action, HintLevel level) {
		if (level == HintLevel.Vague) {
			return new Hint(action, level, key_reciprocateVague);
		}
		
		Block toCancel = equation.getBlock(action.index);
		if (level == HintLevel.Specific) {
			return new Hint(action, level, key_reciprocateSpecific, toCancel.toString())
			.addHighlights(action.index);
		}
		
		return new Hint(action, level, key_reciprocateBottomOut, 
				Lang.getDeviceString("tutorial", Constant.TUTORIAL_TEXT_CLICK), toCancel)
		.addHighlights(action.index);
	}
}
