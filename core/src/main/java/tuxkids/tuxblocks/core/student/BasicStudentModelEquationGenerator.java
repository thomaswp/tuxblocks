package tuxkids.tuxblocks.core.student;

import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator;
import tuxkids.tuxblocks.core.solve.blocks.EquationGenerator.EGenerator;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;

public class BasicStudentModelEquationGenerator extends EquationGenerator {
	
	
	protected static int randomPlusOrMinus(Random rand, int bounds) {
		return rand.nextInt(bounds * 2 + 1) - bounds;
	}
	
	protected static int randomNonZero(Random rand, int bounds) {
		return rand.nextInt(bounds - 1)+1;
	}
	
	public static EGenerator firstLevelAS() {
		return new EGenerator() {
			Random random = new Random();
			@Override
			public Equation generate() {
				
				if (random.nextBoolean()) {
					return generateAddition();
				}
				return generateSubtraction();
				
			}
			private Equation generateSubtraction() {
				int firstNum = randomNonZero(random, 50);
				int secondNum = randomNonZero(random, firstNum);
				
				return new Equation.Builder().
						addLeft(new VariableBlock("x")).
						addRight(new NumberBlock(firstNum).minus(secondNum)).
						createEquation();
			}
			
			private Equation generateAddition() {
				int answer = random.nextInt(50);
				int addend = random.nextInt(51-answer);
				return new Equation.Builder().
				addLeft(new VariableBlock("x")).
				addRight(new NumberBlock(answer - addend).plus(addend)).
				createEquation();
			}
		};
	}

	/**
	 * Positive factors, x is already isolated, small numbers
	 * @return
	 */
	public static EGenerator firstLevelMD() {
		return new EGenerator() {
			Random random = new Random();
			@Override
			public Equation generate() {
				
				if (random.nextBoolean()) {
					return generateMultiplication();
				}
				return generateDivision();
				
			}
			private Equation generateDivision() {
				int answer = random.nextInt(9);
				int divisor = random.nextInt(9);
				
				return new Equation.Builder().
						addLeft(new VariableBlock("x")).
						addRight(new NumberBlock(answer*divisor).over(divisor)).
						createEquation();
			}
			private Equation generateMultiplication() {
				int firstFactor = random.nextInt(9);
				int secondFactor = random.nextInt(9);
				return new Equation.Builder().
				addLeft(new VariableBlock("x")).
				addRight(new NumberBlock(firstFactor).times(secondFactor)).
				createEquation();
			}
		};
	}

	public static EGenerator singleDragAS() {
		// TODO Auto-generated method stub
		return null;
	}

	public static EGenerator firstLevelMDAS() {
		// TODO Auto-generated method stub
		return null;
	}

}
