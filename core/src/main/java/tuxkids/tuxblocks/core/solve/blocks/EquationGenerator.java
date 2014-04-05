package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tuxkids.tuxblocks.core.solve.blocks.Equation.Builder;
import tuxkids.tuxblocks.core.utils.PlayNObject;

/**
 * Static class for procedurally generating {@link Equation}s. 
 */
public class EquationGenerator extends PlayNObject {

	// allows us to create store our rules for generation
	// as inner/anonymous classes
	public static interface EGenerator {
		public Equation generate();
	}

	// Generates equations with a since x and a given number
	// of modifier blocks (the operations parameter).
	private static class StandardGenerator implements EGenerator {

		private final int operations;

		public StandardGenerator(int operations) {
			this.operations = operations;
		}

		@Override
		public Equation generate() {
			return generateStandard(operations);
		}
	}

	// Generates equations which are combinations of two other
	// Standard equations with the same answer.
	private static class CompositeGenerator implements EGenerator {
		public final int minSteps, maxSteps, expressions;

		public CompositeGenerator(int minSteps, int maxSteps, int expressions) {
			this.minSteps = maxSteps;
			this.maxSteps = maxSteps;
			this.expressions = expressions;
		}

		@Override
		public Equation generate() {
			return generateComposite(minSteps, maxSteps, expressions);
		}
	}

	// Generators for the various advanced prestructured forms
	
	private static EGenerator gA1 = new EGenerator() {
		@Override
		public Equation generate() {
			return generateFormA1();
		}
	};

	private static EGenerator gA2 = new EGenerator() {
		@Override
		public Equation generate() {
			return generateFormA2();
		}
	};

	private static EGenerator gA3 = new EGenerator() {
		@Override
		public Equation generate() {
			return generateFormA3();
		}
	};

	private static EGenerator gB1 = new EGenerator() {
		@Override
		public Equation generate() {
			return generateFormB1();
		}
	};

	private static EGenerator gB2 = new EGenerator() {
		@Override
		public Equation generate() {
			return generateFormB2();
		}
	};
	
	// Lists of generators for every difficulty level
	public static EGenerator[][] generators = new EGenerator[][] {
		// Level 1
		new EGenerator[] {
				new StandardGenerator(1),
		},

		// Level 2
		new EGenerator[] {
				new StandardGenerator(2),
				new StandardGenerator(2),
				new CompositeGenerator(1, 1, 2),
				gA1,
		},

		// Level 3
		new EGenerator[] {
				new StandardGenerator(3),
				new StandardGenerator(3),
				new CompositeGenerator(1, 2, 2),
				gA2, gA3,
		},

		// Level 4
		new EGenerator[] {
				new StandardGenerator(4),
				new StandardGenerator(4),
				new CompositeGenerator(2, 3, 2),
				gA2, gA3, gB1,
		},

		// Level 5
		new EGenerator[] {
				new StandardGenerator(5),
				new CompositeGenerator(3, 3, 2),
				new CompositeGenerator(1, 2, 3),
				gB1, gB2,
		},
	};
	
	// smallest factor to add to an equation
	private final static int MIN_FACTOR = 2;

	private static Random rand = new Random();

	// These are our generation parameters
	// TODO: I really should find a better way to pass them around
	// but since everything's synchronous, it should work fine
	private static int difficulty; // the difficulty of the problem to generate [1-5]
	private static float percFinished; // the percentage through the game [0-1]

	// These are upper bounds on various parts of the equation.
	// Our bounds relax as the game goes on, creating harder problems
	// with bigger numbers
	
	private static int maxFactor() {
		return (int) (10 + 5 * percFinished + difficulty);
	}

	private static int maxAdden() {
		return (int) (20 + 20 * percFinished + difficulty * 2);
	}

	// How large any given term in the equation can get at any
	// point. Just because you start with small terms doesn't
	// mean they can't combine to get much bigger. This is not
	// an easy value to control for that reason.
	private static int maxTerm() {
		return (int) (200 + 200 * percFinished + difficulty * 100);
	}

	// Max value for X
	private static int maxAnswer() {
		return (int) (30 + 15 * percFinished + 5 * difficulty);
	}

	// creates a random factor for multiplication/division
	private static int factor() {
		return factor(maxFactor());
	}
	
	// optionally with a max value
	private static int factor(int cap) {
		return rand(MIN_FACTOR, Math.min(cap, maxFactor()));
	}

	// or only a positive value
	private static int factorSigned() {
		return rand(MIN_FACTOR, maxFactor()) * randSign();
	}

	// or with a value that its guaranteed not to be
	private static int factorNot(int not) {
		int f = factor();
		if (f == not) return factorNot(not);
		return f;
	}

	// same deal with the adden, for addition or subtraction
	private static int adden() {
		return adden(maxAdden());
	}

	private static int adden(int cap) {
		return randNonZero(Math.min(maxAdden(), cap));
	}

	/** 
	 * Procedurally generates an equation for the given difficulty and percent through
	 * the game. The returned equation may be of higher or lower difficulty than
	 * the request, but it should average out to the requested difficulty.
	 */
	public static Equation generate(int difficulty, float percFinished) {
		EquationGenerator.percFinished = percFinished;
		EquationGenerator.difficulty = difficulty;
		
		EGenerator[] gens = generators[difficulty];
		return gens[rand.nextInt(gens.length)].generate();
	}
	
	/** 
	 * Procedurally generates a sample equation for the given difficulty.
	 * Unlike the {@link EquationGenerator#generate(int, int)} method,
	 * this method guaranteed the returned Equation will be of the requested
	 * difficulty.
	 */
	public static Equation generateSample(int difficulty) {
		EquationGenerator.difficulty = difficulty;
		EGenerator[] gens = generators[difficulty];
		return gens[rand.nextInt(gens.length)].generate();
		
	}

	/** ax + b = cx + d */
	public static Equation generateFormA1() {
		int a = factor();
		int c = factorNot(a);
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
		int d = adden();
		int c = factor(maxTerm() / Math.abs(d)); // limit c*d
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
		int a = factor(maxTerm() / 4);
		int c = factor(maxTerm() / a / 2);
		int b = adden(maxTerm() / a / c); // limit a*c*b
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
		int b = factor(maxTerm() / 4);
		int c = factor();
		int d = factor(maxTerm() / b / 2);
		int bot = a * b * d + d + b * c;
		int maxE = maxTerm() / a / d; // limit b*d*e
		int e = bot;
		int times = randNonZero(3);
		while (Math.abs(e) < maxE && times != 0) {
			e += bot * (int) Math.signum(e);
			times -= 1 * (int) Math.signum(e);
		}

		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a))
		.addLeft(new VariableBlock("x").over(b))
		.addLeft(new VariableBlock("x").times(c).over(d))
		.addRight(new NumberBlock(e))
		.createEquation();
	}

	/** ax + (x + b) / c + dx / e = f */
	public static Equation generateFormB2() {
		int a = factor(maxTerm() / 4);
		int c = factor(maxTerm() / a / 2);
		int d = factor();
		int e = factor(maxTerm() / a / c); // limit c*e*a
		int f = adden(maxTerm() / c / e); // limit c*e*f

		int bot = a * c * e + e + c * d;
		int b = c * f - randNonZero(2) * bot;

		return new Equation.Builder()
		.addLeft(new VariableBlock("x").times(a))
		.addLeft(new VariableBlock("x").add(b).over(c))
		.addLeft(new VariableBlock("x").times(d).over(e))
		.addRight(new NumberBlock(f))
		.createEquation();
	}


	/**
	 * Generates an equation with the given number of variable expressions, each with
	 * between minSteps and maxSteps number of modifiers. 
	 */
	public static Equation generateComposite(int minSteps, int maxSteps, int expressions) {
		// TODO: find a method for bounding term size while working these equations
		// They have a tendency of producing giant terms >1000 that aren't fun to work with
		
		Builder builder = new Builder();
		int rhs = 0;
		int answer = generateAnswer() / 2;
		for (int i = 0; i < expressions; i++) {
			int steps = rand(minSteps, maxSteps);
			Equation eq = generateStandard(answer, steps);
			
			// compare the current number of x's to see if it's negated by the new equation, avoiding floating point error
			if (Math.round((numXs(eq.leftSide()) + numXs(builder.leftSide())) * 1000) == 0) { 
				// this means the two equations will cancel out, yielding no or all solutions, so try again
				i--;
				continue;
			}
			
			builder.addLeft(eq.leftSide().iterator().next());
			rhs += ((NumberBlock) eq.rightSide().iterator().next()).value();
		}
		builder.addRight(new NumberBlock(rhs));
		if (expressions < 3) {
			builder.addRight(new BlockHolder());
		}
		return builder.createEquation();
	}
	
	// returns the total "number of x's" in this equation,
	// in other words the sum of all factors of x
	private static double numXs(Iterable<BaseBlock> expressions) {
		double xs = 0;
		for (BaseBlock expression : expressions) {
			int factor = 1, divisor = 1;
			for (Block block : expression.getAllBlocks()) {
				if (block instanceof TimesBlock) {
					factor *= ((TimesBlock) block).value;
				} else if (block instanceof OverBlock) {
					divisor *= ((OverBlock) block).value;
				}
			}
			xs += (double) factor / divisor;
		}
		return xs;
	}

	private static int generateAnswer() {
		return rand.nextInt(maxAnswer() * 2 + 1) - maxAnswer();
	}

	private enum Operation {
		Plus, Minus, Times, Over
	}

	public static Equation generateStandard(int steps) {
		return generateStandard(generateAnswer(), steps);
	}

	// Generates a standard equation, with one X expression and the given number of steps
	// needed to solve it. Most of the logic here is choosing nice orderings for
	// the operations, so we don't get boring stuff like x+2+3-4.
	private static Equation generateStandard(int answer, int steps) {
		int rhs = answer;
		BaseBlock lhs = new VariableBlock("x");
		Operation lastOperation = null;
		Operation lastOperationInv = null;
		Integer lastTimes = null;
		
		// start with x = rhs and iteratively do some operation to both sides
		
		for (int i = 0; i < steps; i++) {
			List<Integer> factors = getFactors(rhs); // get the factors of the right hand side
			if (lastTimes != null) factors.remove(lastTimes); // don't repeat a divide operation

			List<Operation> operations = new ArrayList<Operation>();
			for (Operation operation : Operation.values()) operations.add(operation);
			if (factors.isEmpty()) operations.remove(Operation.Over); // can't divide if rhs isn't divisible by anything
			if (lastOperation != null) operations.remove(lastOperation); // don't repeat an operator

			int maxTimes = maxFactor();
			if (rhs != 0) maxTimes = Math.min(maxTimes, Math.abs(maxTerm() / rhs));
			if (maxTimes <= MIN_FACTOR) operations.remove(Operation.Times); // remove time if we can't find a value

			if (operations.size() > 1 && lastOperationInv != null) // if we have the option, avoid doing a +/- or *// pair
				operations.remove(lastOperationInv);

			Operation operation = operations.get(rand.nextInt(operations.size()));
			lastOperation = operation;
			lastTimes = null;

			int value;
			if (operation == Operation.Plus) {
				lastOperationInv = Operation.Minus;
				value = adden();
				lhs = lhs.add(value);
				rhs += value;
			} else if (operation == Operation.Minus) {
				lastOperationInv = Operation.Plus;
				value = adden();
				lhs = lhs.add(-value);
				rhs -= value;
			} else if (operation == Operation.Times) {
				lastOperationInv = Operation.Over;
				value = factorSigned();
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

	// returns -1 or 1
	private static int randSign() {
		return rand(0, 1) * 2 - 1;
	}

	// gets the prime factors of a number
	// it's more complicated then that, but I honestly don't remember :)
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
