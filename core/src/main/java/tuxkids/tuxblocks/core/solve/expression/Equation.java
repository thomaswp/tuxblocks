package tuxkids.tuxblocks.core.solve.expression;

import tuxkids.tuxblocks.core.utils.Formatter;

public class Equation {

	private Expression leftHandSide, rightHandSide;
	private int answer;
	
	public Expression getLeftHandSide() {
		return leftHandSide;
	}

	public Expression getRightHandSide() {
		return rightHandSide;
	}

	public int getAnswer() {
		return answer;
	}

	public Equation(Expression leftHandSide, Expression rightHandSide, int answer) {
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.answer = answer;
	}
	
	public String toMathString() {
		return Formatter.format("%s = %s", leftHandSide, rightHandSide);
	}
}
