package tuxkids.tuxblocks.core.utils;

/**
 * Class for the automatic generation of hash-codes, based
 * on a list of values. It can also performs equality comparison
 * based on the given fields. To use this functionality, a class
 * should implement {@link Hashable}. For an example of using this
 * class for equality comparison, see {@link PlayNObject#equals(Object)} .
 * 
 * All comparison is done without heap allocation or boxing/unboxing.
 * Only primitive types are created.
 */
public class HashCode {
	private final static int prime = 31;
	private int result = 1;
	private Hashable hashable;
	private boolean inHash;
	private boolean inLeftEquals;
	private boolean inRightEquals;
	
	// the last ___ added by the object
	private Object lastObject;
	private int lastInt;
	private long lastLong;
	private short lastShort;
	private double lastDouble;
	private float lastFloat;
	private byte lastByte;
	private char lastChar;
	private boolean lastBoolean;
	
	// used during equality comparison
	private boolean equalSoFar;
	// the index of the field we're comparing now
	private int fieldIndex;
	// the other HashCode we're comparing to
	private HashCode compareTo;	
	// the field we are attempting to read from a hash code
	private int desiredFieldIndex;
	
	/** Creates this HashCode based on the given Hashable object */
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
	
	// methods for adding a field for hashcode generation
	
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
	
	// methods for reading a field from this HashCode's object
	// for equality comparison
	
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
	
	// methods for reading a field from another HashCode
	
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
	
	/** Registers this value for hashing and equality checks */
	public void addField(Object o) {
		if (inHash) addHash(o);
		if (inLeftEquals) addLeftEquals(o);
		if (inRightEquals) addRightEquals(o);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(int i) {
		if (inHash) addHash(i);
		if (inLeftEquals) addLeftEquals(i);
		if (inRightEquals) addRightEquals(i);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(long l) {
		if (inHash) addHash(l);
		if (inLeftEquals) addLeftEquals(l);
		if (inRightEquals) addRightEquals(l);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(short s) {
		if (inHash) addHash(s);
		if (inLeftEquals) addLeftEquals(s);
		if (inRightEquals) addRightEquals(s);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(float f) {
		if (inHash) addHash(f);
		if (inLeftEquals) addLeftEquals(f);
		if (inRightEquals) addRightEquals(f);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(double d) {
		if (inHash) addHash(d);
		if (inLeftEquals) addLeftEquals(d);
		if (inRightEquals) addRightEquals(d);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(byte b) {
		if (inHash) addHash(b);
		if (inLeftEquals) addLeftEquals(b);
		if (inRightEquals) addRightEquals(b);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(char c) {
		if (inHash) addHash(c);
		if (inLeftEquals) addLeftEquals(c);
		if (inRightEquals) addRightEquals(c);
	}

	/** Registers this value for hashing and equality checks */
	public void addField(boolean b) {
		if (inHash) addHash(b);
		if (inLeftEquals) addLeftEquals(b);
		if (inRightEquals) addRightEquals(b);
	}
	
	// Iterates through the other hashable's fields
	// until it reads the field with the desired index.
	// The desired field, therefore, will be stored in one
	// of the "lastXXX" fields of this class
	private void populateField(int index) {
		inRightEquals = true;
		desiredFieldIndex = index;
		fieldIndex = 0;
		hashable.addFields(this);
		inRightEquals = false;
	}
	
	/** 
	 * Compares this HashCode to another, based on the fields added by
	 * its Hashable
	 */
	public boolean equals(HashCode hash) {
		//TODO: support inheritance.. maybe?
		
		// check for obvious incompatibility
		if (hash == null) return false;
		Hashable hashable = hash.hashable;
		if (this.hashable == hashable) return true;
		if (this.hashable == null || hashable == null) return false;
		if (this.hashable.getClass() != hashable.getClass()) return false;
		
		// by definition, if the hashcodes aren't equal, nor are the fields 
		if (hash.hashCode() != hashCode()) return false;
		
		// The process works by having our Hashable
		// add each of its fields. After every one,
		// we have the other Hashable add its fields
		// but we only store the one that matches the field
		// out Hashable just added. Then we compare them and repeat
		// until we find an inequality or all the fields have been added.
		
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
