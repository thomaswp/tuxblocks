package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import tuxkids.tuxblocks.core.PlayNObject;
import tuxkids.tuxblocks.core.solve.blocks.Equation.Builder;

public class EquationGenerator extends PlayNObject {
	
	private static Random rand = new Random();
	
	private static int factor() {
		return rand(1, 10);
	}
	
	private static int factor(int not) {
		int f = factor();
		if (f == not) f++;
		return f;
	}
	
	private static int adden() {
		return randNonZero(20);
	}
	
	private static int adden(int not) {
		int a = adden();
		if (a == not) a++;
		return a;
	}
	
	/** ax + b = cx + d */
	public static Equation generateFormA1() {
		int a = factor();
		int c = factor(a);
		int d = adden();
		int bot = a - c;
		int b = d % bot;
		b += randNonZero(4) * bot;
		if (b == d) b += bot;
		
		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a).add(b))
		.addRight(new VariableBlock("x").times(c).add(d))
		.addRight(new BlockHolder())
		.createEquation();
	}
	
	/** ax + (x + b) / c = d */
	public static Equation generateFormA2() {

		int a = factor();
		int c = factor();
		int d = adden();
		int top = c * d;
		int bot = a * c + 1;
		int b = top - (Math.round((float)top / bot) * bot);
		
		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a))
		.addLeft(new VariableBlock("x").add(b).over(c))
		.addRight(new NumberBlock(d))
		.createEquation();
	}
	
	/** x / a + c * (x + b) = d */
	public static Equation generateFormA3() {
		int a = factor();
		int c = factor();
		int b = adden();
		int d = randNonZero(2) * (c * a + 1) + c * b;
		
		return new Equation.Builder()
		.addLeft(new VariableBlock("x").over(a))
		.addLeft(new VariableBlock("x").add(b).times(c))
		.addRight(new NumberBlock(d))
		.addRight(new BlockHolder())
		.createEquation();
	}
	
	/** ax + x / b + cx / d = e */
	public static Equation generateFormB1() {
		int a = factor();
		int b = factor();
		int c = factor();
		int d = factor();
		int bot = a * b * d + d + b * c;
		int e = randNonZero(3) * bot;
		
		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a))
		.addLeft(new VariableBlock("x").over(b))
		.addLeft(new VariableBlock("x").times(c).over(d))
		.addRight(new NumberBlock(e))
		.createEquation();
	}
	
	/** ax + (x + b) / c + dx / e = f */
	public static Equation generateFormB2() {
		int a = factor();
		int c = factor();
		int d = factor();
		int e = factor();
		int f = adden();
		
		int bot = a * c * e + e + c * d;
		int b = c * f - randNonZero(2) * bot;
		
		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a))
		.addLeft(new VariableBlock("x").add(b).over(c))
		.addLeft(new VariableBlock("x").times(d).over(e))
		.addRight(new NumberBlock(f))
		.createEquation();
	}
	
	private final static int MAX_ANSWER = 50;
	private final static int MAX_ADD_SUB = 20;
	private final static int MAX_TIMES = 10;
	private final static int MIN_TIMES = 2;
	private final static int MAX_RHS = 500;
	
	private enum Operation {
		Plus, Minus, Times, Over
	}
	
	public static Equation generateComposite(int minSteps, int maxSteps, int expressions) {
		Builder builder = new Builder();
		int rhs = 0;
		int answer = generateAnswer() / 2;
		for (int i = 0; i < expressions; i++) {
			int steps = rand(minSteps, maxSteps);
			Equation eq = generate(answer, steps);
			builder.addLeft(eq.leftSide().get(0));
			rhs += ((NumberBlock) eq.rightSide().get(0)).value();
		}
		builder.addRight(new NumberBlock(rhs));
		builder.addRight(new BlockHolder());
		return builder.createEquation();
	}
	
	private static int generateAnswer() {
		return rand.nextInt(MAX_ANSWER * 2 + 1) - MAX_ANSWER;
	}
	
	public static Equation generate(int steps) {
		return generate(generateAnswer(), steps);
	}
	
	private static Equation generate(int answer, int steps) {
		int rhs = answer;
		BaseBlock lhs = new VariableBlock("x");
		Operation lastOperation = null;
		Operation lastOperationInv = null;
		Integer lastTimes = null;
		for (int i = 0; i < steps; i++) {
			List<Integer> factors = getFactors(rhs);
			if (lastTimes != null) factors.remove(lastTimes);
			
			List<Operation> operations = new ArrayList<Operation>();
			for (Operation operation : Operation.values()) operations.add(operation);
			if (factors.isEmpty()) operations.remove(Operation.Over);
			if (lastOperation != null) operations.remove(lastOperation);
			
			int maxTimes = MAX_TIMES;
			if (rhs != 0) maxTimes = Math.min(maxTimes, Math.abs(MAX_RHS / rhs));
			if (maxTimes <= MIN_TIMES) operations.remove(Operation.Times);
			
			if (operations.size() > 1 && lastOperationInv != null)
				operations.remove(lastOperationInv);
			
			Operation operation = operations.get(rand.nextInt(operations.size()));
			lastOperation = operation;
			lastTimes = null;
			
			int value;
			if (operation == Operation.Plus) {
				lastOperationInv = Operation.Minus;
				value = rand.nextInt(MAX_ADD_SUB - 1) + 1;
				lhs = lhs.add(value);
				rhs += value;
			} else if (operation == Operation.Minus) {
				lastOperationInv = Operation.Plus;
				value = rand.nextInt(MAX_ADD_SUB - 1) + 1;
				lhs = lhs.add(-value);
				rhs -= value;
			} else if (operation == Operation.Times) {
				lastOperationInv = Operation.Over;
				value = rand.nextInt(maxTimes - MIN_TIMES) + MIN_TIMES;
				lhs = lhs.times(value);
				rhs *= value;
				lastTimes = value;
			} else {
				lastOperationInv = Operation.Times;
				value = factors.get(rand.nextInt(factors.size()));
				lhs = lhs.over(value);
				rhs /= value;
			}
		}
		return new Builder().addLeft(lhs).addRight(new NumberBlock(rhs)).createEquation();
	}
	
	
	private static int rand(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}
	
	private static int randNonZero(int maxMag) {
		return rand(1, maxMag) * randSign();
	}
	
	private static int randSign() {
		return rand(0, 1) * 2 - 1;
	}
	
	public static List<Integer> getFactors(int n) {
		List<Integer> factors = new ArrayList<Integer>();
		n = Math.abs(n);
		if (n < 3) return factors;
		double max = Math.sqrt(n);
		int pIndex = 0;
		int prime = getPrime(pIndex++);
		while (prime <= max) {
			if (n % prime == 0) {
				if (n / prime == 1) break;
				factors.add(prime);
				if (!factors.contains(n / prime)) factors.add(n / prime);
				List<Integer> subprimes = getFactors(n / prime);
				for (Integer subprime : subprimes) {
					if (!factors.contains(subprime)) factors.add(subprime);
					if (!factors.contains(subprime * prime)) factors.add(subprime * prime);
				}
				break;
			} else {
				prime = getPrime(pIndex++);
			}
		}
		return factors;
	}

	
	private final static List<Integer> primes = new ArrayList<Integer>();
	static { primes.add(2); }
	private static int getPrime(int index) {
		while (index >= primes.size()) {
			int possible = primes.get(primes.size() - 1) + 1;
			while (!isPrimeSoFar(possible)) possible++;
			primes.add(possible);
		}

		return primes.get(index);
	}
	
	private static boolean isPrimeSoFar(int n) {
		double max = Math.sqrt(n);
		for (int prime : primes) {
			if (prime > max) break;
			if (n % prime == 0) return false;
		}
		return true;
	}
}
