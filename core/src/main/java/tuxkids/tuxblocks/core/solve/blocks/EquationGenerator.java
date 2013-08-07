package tuxkids.tuxblocks.core.solve.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import tuxkids.tuxblocks.core.PlayNObject;

public class EquationGenerator extends PlayNObject {
	
	private static Random rand = new Random();
	
	public static Equation generate(int vars, int steps) {
		Expression lhs = new Expression(), rhs = new Expression();
		int answer = rand.nextInt(21) - 10;
		int factor = rand.nextInt(15) + vars;
		int[] varFactors = new int[vars];
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		for (int i = 1; i < factor; i++) possibleValues.add(i);
		for (int i = 1; i < vars; i++) {			
			varFactors[i] = possibleValues.remove(rand.nextInt(possibleValues.size()));
		}
		Arrays.sort(varFactors);
		for (int i = 0; i < vars - 1; i++) varFactors[i] = varFactors[i+1] - varFactors[i];
		varFactors[vars - 1] = factor - varFactors[vars - 1];

		int n = answer * factor;
		
		VariableBlock[] vBlocks = new VariableBlock[vars];
		for (int i = 0; i < vars; i++) {
			VariableBlock vb = new VariableBlock("x");
			if (varFactors[i] != 1) {
				vb.times(varFactors[i]);
			}
			vBlocks[i] = vb;
		}
		
		int f = factor;//varFactors[rand.nextInt(varFactors.length)];
		for (VariableBlock v : vBlocks) v.over(f);
		n /= f;
		
		for (int i = 0; i < steps; i++) {
			
		}

		for (int i = 0; i < vars; i++) {
			VariableBlock vb = vBlocks[i];
			if (true || rand.nextBoolean()) {
				lhs.add(vb);
			} else {
				vb.times(-1);
				rhs.add(vb);
			}
			vb.simplifyModifiers();
		}
		if (false && rand.nextBoolean()) {
			lhs.add(new NumberBlock(-n));
		} else {
			rhs.add(new NumberBlock(n));
		}
		
		return new Equation(lhs, rhs);
	}
	
	private static void times(Expression lhs, NumberBlock rhs) {
		int factor = randNonZero(9);
		for (BaseBlock b : lhs) {
			b.times(factor);
			b.simplifyModifiers();
		}
		rhs.value *= factor;
	}
	
	private static void plus(Expression lhs, NumberBlock rhs) {
		int adden = randNonZero(20);
		for (BaseBlock b : lhs) {
			b.add(adden);
			b.simplifyModifiers();
		}
		rhs.value += adden;
	}
	
	private static void over(Expression lhs, NumberBlock rhs) {
		List<Integer> factors = getFactors(rhs.value);
		int factor = factors.get(rand.nextInt(factors.size()));
		for (BaseBlock b : lhs) {
			b.over(factor);
			b.simplifyModifiers();
		}
		rhs.value /= factor;
	}
	
	private static void split(Expression lhs, NumberBlock rhs) {
		int factor = randNonZero(9);
		for (BaseBlock b : lhs) {
			b.times(factor);
			b.simplifyModifiers();
		}
		rhs.value *= factor;
	}
	
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
	
	private static int rand(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}
	
	private static int randNonZero(int maxMag) {
		return rand(1, maxMag) * randSign();
	}
	
	private static int randSign() {
		return rand(0, 1) * 2 - 1;
	}
	
	private static class Expression extends ArrayList<BaseBlock> {
		private static final long serialVersionUID = 1L;
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
