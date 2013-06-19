package tuxkids.tuxblocks.core.utils;

public class HashCode {
	private final static int prime = 31;
	private int result = 1;
	private Hashable hashable;
	private boolean inHash;
	private boolean inLeftEquals;
	private boolean inRightEquals;
	
	private Object lastObject;
	private int lastInt;
	private long lastLong;
	private short lastShort;
	private double lastDouble;
	private float lastFloat;
	private byte lastByte;
	private char lastChar;
	private boolean lastBoolean;
	
	private boolean equalSoFar;
	private int fieldIndex;
	private HashCode compareTo;	
	private int desiredFieldIndex;
	
	public HashCode(Hashable hashable) {
		this.hashable = hashable;
	}
	
	@Override
	public int hashCode() {
		inHash = true;
		result = 1;
		hashable.addFields(this);
		inHash = false;
		return result;
	}
	
	private void addHash(Object o) {
		result = prime * result + ((o == null) ? 0 : o.hashCode());
	}
	
	private void addHash(int i) {
		result = prime * result + i;
	}
	
	private void addHash(long l) {
		result = prime * result + (int) (l ^ (l >>> 32));
	}
	
	private void addHash(short s) {
		result = prime * result + s;
	}
	
	private void addHash(float f) {
		result = prime * result + Float.floatToIntBits(f);
	}
	
	private void addHash(double d) {
		long temp;
		temp = Double.doubleToLongBits(d);
		result = prime * result + (int) (temp ^ (temp >>> 32));
	}
	
	private void addHash(byte b) {
		result = prime * result + b;
	}
	
	private void addHash(char c) {
		result = prime * result + c;
	}
	
	private void addHash(boolean b) {
		result = prime * result + (b ? 1231 : 1237);
	}
	
	private void addLeftEquals(Object o) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		boolean eq;
		Object o2 = compareTo.lastObject;
		if (o == null) {
			eq = o2 == null;
		} else {
			eq = o.equals(o2);
		}
		if (!eq) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(int i) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (i != compareTo.lastInt) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(long l) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (l != compareTo.lastLong) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(short s) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (s != compareTo.lastShort) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(float f) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (f != compareTo.lastFloat) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(double d) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (d != compareTo.lastDouble) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(byte b) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (b != compareTo.lastByte) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(char c) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (c != compareTo.lastChar) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addLeftEquals(boolean b) {
		if (!equalSoFar) return;
		compareTo.populateField(fieldIndex);
		if (b != compareTo.lastBoolean) equalSoFar = false;
		fieldIndex++;
	}
	
	private void addRightEquals(Object o) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastObject = o;
	}
	
	private void addRightEquals(int i) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastInt = i;
	}
	
	private void addRightEquals(long l) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastLong = l;
	}
	
	private void addRightEquals(short s) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastShort = s;
	}
	
	private void addRightEquals(float f) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastFloat = f;
	}
	
	private void addRightEquals(double d) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastDouble = d;
	}
	
	private void addRightEquals(byte b) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastByte = b;
	}
	
	private void addRightEquals(char c) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastChar = c;
	}
	
	private void addRightEquals(boolean b) {
		if (desiredFieldIndex >= 0 && fieldIndex++ != desiredFieldIndex) return;
		lastBoolean = b;
	}
	
	public void addField(Object o) {
		if (inHash) addHash(o);
		if (inLeftEquals) addLeftEquals(o);
		if (inRightEquals) addRightEquals(o);
	}
	
	public void addField(int i) {
		if (inHash) addHash(i);
		if (inLeftEquals) addLeftEquals(i);
		if (inRightEquals) addRightEquals(i);
	}
	
	public void addField(long l) {
		if (inHash) addHash(l);
		if (inLeftEquals) addLeftEquals(l);
		if (inRightEquals) addRightEquals(l);
	}
	
	public void addField(short s) {
		if (inHash) addHash(s);
		if (inLeftEquals) addLeftEquals(s);
		if (inRightEquals) addRightEquals(s);
	}
	
	public void addField(float f) {
		if (inHash) addHash(f);
		if (inLeftEquals) addLeftEquals(f);
		if (inRightEquals) addRightEquals(f);
	}
	
	public void addField(double d) {
		if (inHash) addHash(d);
		if (inLeftEquals) addLeftEquals(d);
		if (inRightEquals) addRightEquals(d);
	}
	
	public void addField(byte b) {
		if (inHash) addHash(b);
		if (inLeftEquals) addLeftEquals(b);
		if (inRightEquals) addRightEquals(b);
	}
	
	public void addField(char c) {
		if (inHash) addHash(c);
		if (inLeftEquals) addLeftEquals(c);
		if (inRightEquals) addRightEquals(c);
	}
	
	public void addField(boolean b) {
		if (inHash) addHash(b);
		if (inLeftEquals) addLeftEquals(b);
		if (inRightEquals) addRightEquals(b);
	}
	
	private void populateField(int index) {
		inRightEquals = true;
		desiredFieldIndex = index;
		fieldIndex = 0;
		hashable.addFields(this);
		inRightEquals = false;
	}
	
	public boolean equals(HashCode hash) {
		if (hash == null) return false;
		Hashable hashable = hash.hashable;
		if (this.hashable == hashable) return true;
		if (this.hashable == null || hashable == null) return false;
		
		if (hash.hashCode() != hashCode()) return false;
		
		equalSoFar = true;
		inLeftEquals = true;
		compareTo = hash;
		desiredFieldIndex = -1;
		fieldIndex = 0;
		this.hashable.addFields(this);
		inLeftEquals = false;
		return equalSoFar;
		
	}
	
	public interface Hashable {
		void addFields(HashCode hashCode);
	}
}
