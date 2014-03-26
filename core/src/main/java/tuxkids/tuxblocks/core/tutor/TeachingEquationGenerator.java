package tuxkids.tuxblocks.core.tutor;

import java.util.Random;

import static java.lang.Math.*;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;


public class TeachingEquationGenerator {
	
	private static int LOW_NUMBER_CEILING = 25;
	
	private final static Random random = new Random();
	
//L0: x=6±5, x=4*3
//L1: x±3=5, x=3*(4-2)
//L2: x-4 = 8+4, 3x = 12, x±23 = 45, x/4= 5
//L3: 3x = 8+4, x±23 = 9*5, x+20-9 = 45-21	
//L4: 3x+x = 12, 9x = 108
//L5: 5x - x = 5*4

//L6: 3x = x - 4
//L7: 2(x+1) = -6, -6x-24 = 9x + 6, -10(x+10) = -360
//L8: x/4 - 3x = 22, 6(x-3) + -12(x-9) = 30
//L9: x/5 + 9(x-1) = -101, 4(x/2+1) = 8
//L10: x/-4 + x/-3 = -49, 1/8(x-6) = -48, (x+2)/2 = 16
//L11: -10((x+8)/12 - 7) = 40, 5x + x/6 + 4x/3 = 117
//L12: (-4x + 15)/13 + (1/2)(x/10 + 16) = 4

	
	
	private static int nextLowInt() {
		return nextNonZeroInt(LOW_NUMBER_CEILING);
	}

	private static int nextNonZeroInt(int bounds) {
		return nextNonZeroInt(-bounds, bounds);
	}
	
	private static int nextNonZeroInt(int min, int max) {
		int i = random.nextInt(max-min) - min;
		while(i == 0) i = random.nextInt(max-min) - min;
		return i;
	}

	//L0: x=6±5, x=4*3
	//L1: x±3=5, x=3*(4-2)
	//L2: x-4 = 8+4, 3x = 12, x±23 = 45, x/4= 5
	//L3: 3x = 8+4, x±23 = 9*5, x+20-9 = 45-21	
	//L4: 3x+x = 12, 9x = 108
	//L5: 5x - x = 5*4
	
	//L6: 3x = x - 4
	//L7: 2(x+1) = -6, -6x-24 = 9x + 6, -10(x+10) = -360
	//L8: x/4 - 3x = 22, 6(x-3) + -12(x-9) = 30
	//L9: x/5 + 9(x-1) = -101, 4(x/2+1) = 8
	//L10: x/-4 + x/-3 = -49, 1/8(x-6) = -48, (x+2)/2 = 16
	//L11: -10((x+8)/12 - 7) = 40, 5x + x/6 + 4x/3 = 117
	//L12: (-4x + 15)/13 + (1/2)(x/10 + 16) = 4
	
		
		
		public static Equation generateL0_0(){
			
			int first = random.nextInt(LOW_NUMBER_CEILING-1)+1;
			int second = nextNonZeroInt(LOW_NUMBER_CEILING-first);
					
			return new Equation.Builder()
			.addLeft(new VariableBlock("x"))
			.addRight(new NumberBlock(first).add(second))
			.exchangeSides(random.nextBoolean())
			.createEquation();
		}


}
