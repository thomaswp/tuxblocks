package tuxkids.tuxblocks.core.solve.expression;

import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.Equation.Builder;
import tuxkids.tuxblocks.core.utils.Formatter;
import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class Equation extends PlayNObject implements Hashable {

	private Expression leftHandSide, rightHandSide;
	private Integer answer, difficulty;
	
	public Expression leftHandSide() {
		return leftHandSide;
	}

	public Expression rightHandSide() {
		return rightHandSide;
	}

	public int answer() {
		return answer;
	}
	
	public int difficulty() {
		return difficulty;
	}

	public Equation(Expression leftHandSide, Expression rightHandSide, int answer, int difficulty) {
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
		this.answer = answer;
		this.difficulty = difficulty;
	}
	
	public String toMathString() {
		return Formatter.format("%s = %s", leftHandSide, rightHandSide);
	}

	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(leftHandSide);
		hashCode.addField(rightHandSide);
	}
	
	public tuxkids.tuxblocks.core.solve.blocks.Equation toBlocks() {
		return new Builder()
		.addLeft(leftHandSide.toBaseBlock())
		.addRight(rightHandSide.toBaseBlock())
		.createEquation();
	}
}
