package tuxkids.tuxblocks.core.solve.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EquationGenerator {

	private final static int MAX_ANSWER = 50;
	private final static int MAX_ADD_SUB = 20;
	private final static int MAX_TIMES = 10;
	private final static int MIN_TIMES = 2;
	private final static int MAX_RHS = 500;
	
	private final static Random random = new Random();
	
	private enum Operation {
		Plus, Minus, Times, Over
	}
	
	public static Equation generate(int steps) {
		int answer = random.nextInt(MAX_ANSWER * 2 + 1) - MAX_ANSWER;
		int rhs = answer;
		Expression lhs = new Variable("x");
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
			if (maxTimes == 1) operations.remove(Operation.Times);
			
			if (operations.size() > 1 && lastOperationInv != null)
				operations.remove(lastOperationInv);
			
			Operation operation = operations.get(random.nextInt(operations.size()));
			lastOperation = operation;
			lastTimes = null;
			
			if (operation == Operation.Plus) {
				lastOperationInv = Operation.Minus;
				int value = random.nextInt(MAX_ADD_SUB - 1) + 1;
				lhs = lhs.plus(value);
				rhs += value;
			} else if (operation == Operation.Minus) {
				lastOperationInv = Operation.Plus;
				int value = random.nextInt(MAX_ADD_SUB - 1) + 1;
				lhs = lhs.minus(value);
				rhs -= value;
			} else if (operation == Operation.Times) {
				lastOperationInv = Operation.Over;
				int value = random.nextInt(maxTimes - MIN_TIMES) + MIN_TIMES;
				lhs = lhs.times(value);
				rhs *= value;
				lastTimes = value;
			} else {
				lastOperationInv = Operation.Times;
				int value = factors.get(random.nextInt(factors.size()));
				lhs = lhs.over(value);
				rhs /= value;
			}
		}
		return new Equation(lhs, new Number(rhs), answer);
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
